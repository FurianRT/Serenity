package com.furianrt.storage.internal.notes.repositories

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.storage.internal.notes.dao.NoteDao
import com.furianrt.storage.internal.notes.entities.EntryNote
import com.furianrt.storage.internal.notes.extensions.toNote
import javax.inject.Inject

internal class NotesRepositoryImp @Inject constructor(
    private val noteDao: NoteDao,
) : NotesRepository {

    override suspend fun getAllNotes(): List<LocalNote> = noteDao.getAllNotes().map(EntryNote::toNote)
}
