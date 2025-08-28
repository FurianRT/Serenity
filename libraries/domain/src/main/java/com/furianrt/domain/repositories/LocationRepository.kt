package com.furianrt.domain.repositories

import com.furianrt.domain.entities.NoteLocation

interface LocationRepository {
    suspend fun insert(noteId: String, location: NoteLocation)
    suspend fun delete(noteId: String)
    suspend fun detectLocation(): NoteLocation?
}