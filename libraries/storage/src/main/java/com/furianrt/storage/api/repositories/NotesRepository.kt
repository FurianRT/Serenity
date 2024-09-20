package com.furianrt.storage.api.repositories

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.entities.SimpleNote
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    suspend fun insetNote(note: SimpleNote)
    suspend fun updateNoteText(noteId: String, content: List<LocalNote.Content>)
    suspend fun deleteNote(noteId: String)
    fun getAllNotes(): Flow<List<LocalNote>>
    fun getNote(noteId: String): Flow<LocalNote?>
}
