package com.furianrt.storage.internal.repositories

import com.furianrt.core.deepFilter
import com.furianrt.core.deepMap
import com.furianrt.domain.TransactionsHelper
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.NoteFontColor
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.entities.SimpleNote
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.storage.internal.cache.NoteCache
import com.furianrt.storage.internal.database.notes.dao.NoteDao
import com.furianrt.storage.internal.database.notes.entities.LinkedNote
import com.furianrt.storage.internal.database.notes.entities.PartNoteDate
import com.furianrt.storage.internal.database.notes.entities.PartNoteFont
import com.furianrt.storage.internal.database.notes.entities.PartNoteId
import com.furianrt.storage.internal.database.notes.entities.PartNoteIsPinned
import com.furianrt.storage.internal.database.notes.entities.PartNoteText
import com.furianrt.storage.internal.database.notes.mappers.toEntryNote
import com.furianrt.storage.internal.database.notes.mappers.toEntryNoteText
import com.furianrt.storage.internal.database.notes.mappers.toLocalNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import javax.inject.Inject

internal class NotesRepositoryImp @Inject constructor(
    private val noteDao: NoteDao,
    private val noteCache: NoteCache,
    private val transactionsHelper: TransactionsHelper,
) : NotesRepository {

    override suspend fun insetNote(note: SimpleNote) {
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

    override suspend fun updateNoteFont(
        noteId: String,
        color: NoteFontColor,
        family: NoteFontFamily,
        size: Int,
    ) {
        noteDao.update(PartNoteFont(id = noteId, font = family, fontColor = color, fontSize = size))
    }

    override suspend fun deleteNote(noteId: String) = transactionsHelper.startTransaction {
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

    override fun getAllNotes(query: String): Flow<List<LocalNote>> = if (query.isNotEmpty()) {
        noteDao.getAllNotes("%$query%")
            .deepMap(LinkedNote::toLocalNote)
            .deepFilter { note ->
                note.content.any { content ->
                    content is LocalNote.Content.Title &&
                            content.text.contains(query, ignoreCase = true)
                }
            }
            .map { it.sortedByDescending(LocalNote::date) }
    } else {
        getAllNotes()
    }

    override fun getNote(noteId: String): Flow<LocalNote?> =
        noteDao.getNote(noteId).map { it?.toLocalNote() }

    override fun getNoteContentFromCache(noteId: String): List<LocalNote.Content> {
        return noteCache.getNoteContent(noteId)
    }

    override fun cacheNoteContent(noteId: String, content: List<LocalNote.Content>) {
        noteCache.cacheNoteContent(noteId, content)
    }

    override fun deleteNoteContentFromCache(noteId: String) {
        noteCache.deleteCache(noteId)
    }
}
