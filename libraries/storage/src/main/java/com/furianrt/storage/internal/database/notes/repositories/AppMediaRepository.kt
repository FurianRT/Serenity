package com.furianrt.storage.internal.database.notes.repositories

import com.furianrt.storage.api.entities.AppMedia
import com.furianrt.storage.api.entities.LocalNote

internal interface AppMediaRepository {
    suspend fun saveMediaFile(media: LocalNote.Content.Media): AppMedia?
    suspend fun deleteMediaFile(id : String): Boolean
}