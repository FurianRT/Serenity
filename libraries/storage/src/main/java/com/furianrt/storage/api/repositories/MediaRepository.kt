package com.furianrt.storage.api.repositories

import com.furianrt.storage.api.entities.LocalNote

interface MediaRepository {
    suspend fun upsert(noteId: String, block: LocalNote.Content.MediaBlock)
    suspend fun delete(blockId: String, media: LocalNote.Content.Media)
    suspend fun getMedia(noteId: String): List<LocalNote.Content.Media>
}
