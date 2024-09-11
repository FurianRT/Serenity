package com.furianrt.storage.internal.repositories

import com.furianrt.core.deepMap
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.storage.api.repositories.TagsRepository
import com.furianrt.storage.internal.database.notes.dao.NoteDao
import com.furianrt.storage.internal.database.notes.entities.LinkedNote
import com.furianrt.storage.internal.database.notes.mappers.toEntryNote
import com.furianrt.storage.internal.database.notes.mappers.toEntryNoteText
import com.furianrt.storage.internal.database.notes.mappers.toLocalNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class NotesRepositoryImp @Inject constructor(
    private val noteDao: NoteDao,
    private val tagsRepository: TagsRepository,
) : NotesRepository {

    override suspend fun updateNoteText(noteId: String, content: List<LocalNote.Content>) {
        noteDao.updateNoteText(noteId, content.toEntryNoteText())
    }

    // TODO вынести в UseCase
    override suspend fun deleteNote(note: LocalNote) {
        noteDao.delete(note.toEntryNote())
        tagsRepository.deleteUnusedTags()
    }

    override fun getAllNotes(): Flow<List<LocalNote>> = noteDao.getAllNotes()
        .deepMap(LinkedNote::toLocalNote)

    override fun getNote(noteId: String): Flow<LocalNote?> =
        noteDao.getNote(noteId).map { it?.toLocalNote() }
}
