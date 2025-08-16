package com.furianrt.storage.internal.repositories

import android.content.Context
import com.furianrt.core.DispatchersProvider
import com.furianrt.core.deepMap
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.NoteFontColor
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.entities.SimpleNote
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.storage.internal.cache.NoteCache
import com.furianrt.storage.internal.database.notes.dao.NoteDao
import com.furianrt.storage.internal.database.notes.entities.EntryNote
import com.furianrt.storage.internal.database.notes.entities.LinkedNote
import com.furianrt.storage.internal.database.notes.entities.PartNoteBackgroundId
import com.furianrt.storage.internal.database.notes.entities.PartNoteDate
import com.furianrt.storage.internal.database.notes.entities.PartNoteFont
import com.furianrt.storage.internal.database.notes.entities.PartNoteId
import com.furianrt.storage.internal.database.notes.entities.PartNoteIsPinned
import com.furianrt.storage.internal.database.notes.entities.PartNoteIsTemplate
import com.furianrt.storage.internal.database.notes.entities.PartNoteText
import com.furianrt.storage.internal.database.notes.mappers.toEntryNote
import com.furianrt.storage.internal.database.notes.mappers.toEntryNoteText
import com.furianrt.storage.internal.database.notes.mappers.toLocalNote
import com.furianrt.storage.internal.database.notes.mappers.toSimpleNote
import com.furianrt.storage.internal.preferences.AppearanceDataStore
import com.furianrt.storage.internal.workers.DatabaseCleanupWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject

internal class NotesRepositoryImp @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val noteDao: NoteDao,
    private val appearanceDataStore: AppearanceDataStore,
    private val noteCache: NoteCache,
    private val dispatchers: DispatchersProvider,
) : NotesRepository {

    override suspend fun insertNote(note: SimpleNote) {
        noteDao.insert(note.toEntryNote())
    }

    override suspend fun upsertNote(note: SimpleNote) {
        noteDao.upsert(note.toEntryNote())
    }

    override suspend fun updateNoteText(noteId: String, content: List<LocalNote.Content>) {
        noteDao.update(
            PartNoteText(
                id = noteId,
                text = content.toEntryNoteText(),
                textSpans = content
                    .filterIsInstance<LocalNote.Content.Title>()
                    .flatMap(LocalNote.Content.Title::spans),
            )
        )
    }

    override suspend fun updateNoteDate(noteId: String, date: ZonedDateTime) {
        noteDao.update(PartNoteDate(id = noteId, date = date))
    }

    override suspend fun updateNoteIsPinned(noteId: String, isPinned: Boolean) {
        noteDao.update(PartNoteIsPinned(noteId, isPinned))
    }

    override suspend fun updateNoteIsPinned(noteIds: Set<String>, isPinned: Boolean) {
        noteDao.update(noteIds.map { PartNoteIsPinned(it, isPinned) })
    }

    override suspend fun updateNoteBackgroundId(noteId: String, backgroundId: String?) {
        noteDao.update(PartNoteBackgroundId(noteId, backgroundId))
    }

    override suspend fun updateNoteFont(
        noteId: String,
        color: NoteFontColor?,
        family: NoteFontFamily?,
        size: Int,
    ) {
        noteDao.update(PartNoteFont(id = noteId, font = family, fontColor = color, fontSize = size))
    }

    override suspend fun setTemplate(noteId: String, isTemplate: Boolean) {
        noteDao.update(PartNoteIsTemplate(noteId, isTemplate = isTemplate))
    }

    override suspend fun deleteNote(noteId: String) {
        noteDao.delete(PartNoteId(noteId))
    }

    override suspend fun deleteNotes(noteIds: Set<String>) {
        noteDao.delete(noteIds.map { PartNoteId(it) }.toList())
    }

    override fun getAllNotes(): Flow<List<LocalNote>> = noteDao.getAllNotes()
        .deepMap(LinkedNote::toLocalNote)
        .map { notes ->
            notes.sortedWith(
                compareByDescending(LocalNote::isPinned)
                    .thenByDescending(LocalNote::date)
            )
        }
        .flowOn(dispatchers.default)

    override fun getAllNotes(query: String): Flow<List<LocalNote>> = if (query.isNotEmpty()) {
        noteDao.getAllNotes("%$query%")
            .deepMap(LinkedNote::toLocalNote)
            .map { it.sortedByDescending(LocalNote::date) }
            .flowOn(dispatchers.default)
    } else {
        getAllNotes()
    }

    override fun getNote(noteId: String): Flow<LocalNote?> =
        noteDao.getNote(noteId)
            .map { it?.toLocalNote() }
            .flowOn(dispatchers.default)

    override fun getOrCreateTemplateNote(
        noteId: String,
    ): Flow<SimpleNote> = noteDao.getSimpleNote(noteId)
        .map { note ->
            note?.toSimpleNote() ?: EntryNote(
                id = noteId,
                text = "",
                textSpans = emptyList(),
                font = appearanceDataStore.getDefaultNoteFont().first(),
                fontColor = appearanceDataStore.getDefaultNoteFontColor().first(),
                fontSize = appearanceDataStore.getDefaultNoteFontSize().first(),
                backgroundId = null,
                date = ZonedDateTime.now(),
                isPinned = false,
                isTemplate = true,
            )
                .also { noteDao.insert(it) }
                .toSimpleNote()
        }

    override fun getAllTemplates(): Flow<List<SimpleNote>> = noteDao.getAllTemplates()
        .deepMap(EntryNote::toSimpleNote)

    override fun getUniqueNotesDates(): Flow<Set<LocalDate>> =
        noteDao.getAllSimpleNotes()
            .deepMap { it.date.toLocalDate() }
            .map { it.toSet() }
            .flowOn(dispatchers.default)

    override fun getNoteContentFromCache(noteId: String): List<LocalNote.Content> {
        return noteCache.getNoteContent(noteId)
    }

    override fun cacheNoteContent(noteId: String, content: List<LocalNote.Content>) {
        noteCache.cacheNoteContent(noteId, content)
    }

    override fun deleteNoteContentFromCache(noteId: String) {
        noteCache.deleteCache(noteId)
    }

    override fun enqueueOneTimeCleanup() {
        DatabaseCleanupWorker.enqueueOneTime(context)
    }

    override fun enqueuePeriodicCleanup() {
        DatabaseCleanupWorker.enqueuePeriodic(context)
    }
}
