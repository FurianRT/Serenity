package com.furianrt.storage.api.repositories

import com.furianrt.storage.api.entities.LocalNote

interface NotesRepository {
    suspend fun getAllNotes(): List<LocalNote>
}
