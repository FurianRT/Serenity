package com.furianrt.storage.internal.repositories

import com.furianrt.core.deepMap
import com.furianrt.storage.internal.database.TransactionsHelper
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.repositories.MediaRepository
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.storage.api.repositories.TagsRepository
import com.furianrt.storage.internal.database.notes.dao.NoteDao
import com.furianrt.storage.internal.database.notes.entities.LinkedNote
import com.furianrt.storage.internal.database.notes.mappers.toEntryNote
import com.furianrt.storage.internal.database.notes.mappers.toEntryNoteText
import com.furianrt.storage.internal.database.notes.mappers.toLocalNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class NotesRepositoryImp @Inject constructor(
    private val noteDao: NoteDao,
    private val tagsRepository: TagsRepository,
    private val mediaRepository: MediaRepository,
    private val transactionsHelper: TransactionsHelper,
) : NotesRepository {

    override suspend fun upsertNote(note: LocalNote) = transactionsHelper.startTransaction {
        noteDao.upsert(note.toEntryNote())
        note.tags.forEach { tagsRepository.upsert(noteId = note.id, tag = it) }
        updateNoteMedia(note.id, note.content)
    }

    override suspend fun updateNoteContent(
        noteId: String,
        content: List<LocalNote.Content>,
    ) = transactionsHelper.startTransaction {
        updateNoteMedia(noteId, content)
        noteDao.updateNoteText(noteId, content.toEntryNoteText())
    }

    override suspend fun deleteNote(note: LocalNote) = transactionsHelper.startTransaction {
        val media = mediaRepository.getMedia(note.id).first()
        media.forEach { mediaRepository.delete(note.id, it) }
        noteDao.delete(note.toEntryNote())
        tagsRepository.deleteUnusedTags()
    }

    override fun getAllNotes(): Flow<List<LocalNote>> = noteDao.getAllNotes()
        .deepMap(LinkedNote::toLocalNote)

    override fun getNote(noteId: String): Flow<LocalNote?> =
        noteDao.getNote(noteId)
            .map { it?.toLocalNote() }
            .distinctUntilChanged()

    private suspend fun updateNoteMedia(noteId: String, media: List<LocalNote.Content>) {
        media.forEach { content ->
            if (content is LocalNote.Content.MediaBlock) {
                content.media.forEach { mediaRepository.upsert(noteId, it) }
            }
        }
    }
}
