package com.furianrt.storage.api.repositories

import com.furianrt.storage.api.entities.Note

interface NotesRepository {
    suspend fun getAllNotes(): List<Note>
}
