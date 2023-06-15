package com.furianrt.storage.internal.notes.repositories

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.entities.LocalSimpleNote
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.storage.internal.notes.dao.NoteDao
import com.furianrt.storage.internal.notes.entities.EntryNote
import com.furianrt.storage.internal.notes.entities.LinkedNote
import com.furianrt.storage.internal.notes.extensions.toLocalNote
import com.furianrt.storage.internal.notes.extensions.toLocalSimpleNote
import javax.inject.Inject

internal class NotesRepositoryImp @Inject constructor(
    private val noteDao: NoteDao,
) : NotesRepository {

    override suspend fun getAllNotes(): List<LocalNote> = noteDao.getAllLinkedNotes()
        .map(LinkedNote::toLocalNote)

    override suspend fun getAllNotesSimple(): List<LocalSimpleNote> = noteDao.getAllNotes()
        .map(EntryNote::toLocalSimpleNote)
}
