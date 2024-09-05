package com.furianrt.storage.api.repositories

import com.furianrt.storage.api.entities.LocalNote
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    suspend fun upsertNote(note: LocalNote)

    suspend fun deleteNote(note: LocalNote)

    suspend fun updateNoteContent(noteId: String, content: List<LocalNote.Content>)

    fun getAllNotes(): Flow<List<LocalNote>>

    fun getNote(noteId: String): Flow<LocalNote?>
}
