package com.furianrt.storage.api.repositories

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.entities.LocalSimpleNote

interface NotesRepository {
    suspend fun getAllNotes(): List<LocalNote>

    suspend fun getAllNotesSimple(): List<LocalSimpleNote>
}
