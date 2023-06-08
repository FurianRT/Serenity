package com.furianrt.storage.internal.notes.repositories

import com.furianrt.storage.api.entities.Note
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.storage.internal.notes.dao.NoteDao
import com.furianrt.storage.internal.notes.entities.DbNote
import com.furianrt.storage.internal.notes.entities.toNote
import javax.inject.Inject

internal class NotesRepositoryImp @Inject constructor(
    private val noteDao: NoteDao,
) : NotesRepository {

    override suspend fun getAllNotes(): List<Note> = noteDao.getAllNotes().map(DbNote::toNote)
}
