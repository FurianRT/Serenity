package com.furianrt.storage.internal.notes.repositories

import com.furianrt.core.deepMap
import com.furianrt.storage.api.TransactionsHelper
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.entities.LocalSimpleNote
import com.furianrt.storage.api.repositories.ImagesRepository
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.storage.api.repositories.TagsRepository
import com.furianrt.storage.internal.notes.dao.NoteDao
import com.furianrt.storage.internal.notes.dao.NoteTitleDao
import com.furianrt.storage.internal.notes.dao.NoteToTagDao
import com.furianrt.storage.internal.notes.entities.EntryNote
import com.furianrt.storage.internal.notes.entities.LinkedNote
import com.furianrt.storage.internal.notes.mappers.toEntryNote
import com.furianrt.storage.internal.notes.mappers.toEntryNoteTitle
import com.furianrt.storage.internal.notes.mappers.toEntryNoteToTag
import com.furianrt.storage.internal.notes.mappers.toLocalNote
import com.furianrt.storage.internal.notes.mappers.toLocalSimpleNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class NotesRepositoryImp @Inject constructor(
    private val noteDao: NoteDao,
    private val noteTitleDao: NoteTitleDao,
    private val tagsRepository: TagsRepository,
    private val imagesRepository: ImagesRepository,
    private val noteToTagDao: NoteToTagDao,
    private val transactionsHelper: TransactionsHelper,
) : NotesRepository {

    override suspend fun upsertNote(note: LocalNote) = transactionsHelper.withTransaction {
        noteDao.upsert(note.toEntryNote())
        note.tags.forEach { upsertTag(note.id, it) }
        note.content.forEach { upsertNoteContent(note.id, it) }
    }

    override suspend fun deleteNote(note: LocalNote) = transactionsHelper.withTransaction {
        noteDao.delete(note.toEntryNote()) // TODO удалять еще и файлы картинок
        tagsRepository.deleteTagsWithoutNotes()
    }

    override suspend fun getAllNotes(): Flow<List<LocalNote>> = noteDao.getAllLinkedNotes()
        .deepMap(LinkedNote::toLocalNote)

    override suspend fun getAllNotesSimple(): Flow<List<LocalSimpleNote>> = noteDao.getAllNotes()
        .deepMap(EntryNote::toLocalSimpleNote)

    override suspend fun getNote(noteId: String): Flow<LocalNote?> =
        noteDao.getNote(noteId).map { it?.toLocalNote() }

    override suspend fun upsertNoteContent(
        noteId: String,
        content: List<LocalNote.Content>,
    ) = transactionsHelper.withTransaction { content.forEach { upsertNoteContent(noteId, it) } }

    override suspend fun upsertNoteTitle(noteId: String, title: LocalNote.Content.Title) =
        if (title.text.isEmpty()) {
            noteTitleDao.delete(title.toEntryNoteTitle(noteId))
        } else {
            noteTitleDao.upsert(title.toEntryNoteTitle(noteId))
        }

    override suspend fun upsertNoteTitle(
        noteId: String,
        titles: List<LocalNote.Content.Title>,
    ) = transactionsHelper.withTransaction { titles.forEach { upsertNoteTitle(noteId, it) } }

    private suspend fun upsertTag(noteId: String, tag: LocalNote.Tag) =
        transactionsHelper.withTransaction {
            tagsRepository.upsert(tag)
            noteToTagDao.upsert(tag.toEntryNoteToTag(noteId))
        }

    private suspend fun upsertNoteContent(noteId: String, content: LocalNote.Content) {
        when (content) {
            is LocalNote.Content.Title -> upsertNoteTitle(noteId, content)
            is LocalNote.Content.ImagesBlock -> imagesRepository.upsert(noteId, content)
        }
    }
}
