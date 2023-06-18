package com.furianrt.storage.internal.notes.repositories

import com.furianrt.core.deepMap
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.entities.LocalSimpleNote
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.storage.internal.notes.dao.NoteDao
import com.furianrt.storage.internal.notes.entities.EntryNote
import com.furianrt.storage.internal.notes.entities.LinkedNote
import com.furianrt.storage.internal.notes.extensions.toLocalNote
import com.furianrt.storage.internal.notes.extensions.toLocalSimpleNote
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class NotesRepositoryImp @Inject constructor(
    private val noteDao: NoteDao,
) : NotesRepository {

    override suspend fun getAllNotes(): Flow<List<LocalNote>> = noteDao.getAllLinkedNotes()
        .deepMap(LinkedNote::toLocalNote)

    override suspend fun getAllNotesSimple(): Flow<List<LocalSimpleNote>> = noteDao.getAllNotes()
        .deepMap(EntryNote::toLocalSimpleNote)

    override suspend fun getNote(noteId: String): LocalNote? =
        noteDao.getNote(noteId)?.toLocalNote()
}
