package com.furianrt.storage.api.repositories

import com.furianrt.storage.api.entities.LocalNote
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    suspend fun deleteNote(note: LocalNote)
    suspend fun updateNoteText(noteId: String, content: List<LocalNote.Content>)
    fun getAllNotes(): Flow<List<LocalNote>>
    fun getNote(noteId: String): Flow<LocalNote?>
}
