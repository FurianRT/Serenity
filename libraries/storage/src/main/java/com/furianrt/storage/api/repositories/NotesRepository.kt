package com.furianrt.storage.api.repositories

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.entities.LocalSimpleNote
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    suspend fun getAllNotes(): Flow<List<LocalNote>>

    suspend fun getAllNotesSimple(): Flow<List<LocalSimpleNote>>

    suspend fun getNote(noteId: String): LocalNote?
}
