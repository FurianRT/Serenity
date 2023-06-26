package com.furianrt.storage.api.repositories

import com.furianrt.storage.api.entities.LocalNote

interface ImagesRepository {
    suspend fun upsert(noteId: String, block: LocalNote.Content.ImagesBlock)
}
