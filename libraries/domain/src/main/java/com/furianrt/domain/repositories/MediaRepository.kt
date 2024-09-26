package com.furianrt.domain.repositories

import com.furianrt.domain.entities.DeviceMedia
import com.furianrt.domain.entities.LocalNote
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    suspend fun insert(noteId: String, media: List<LocalNote.Content.Media>)
    suspend fun delete(noteId: String, media: List<LocalNote.Content.Media>)
    suspend fun deleteMediaFiles(noteId: String)
    fun getMedia(noteId: String): Flow<List<LocalNote.Content.Media>>

    suspend fun getDeviceMediaList(): List<DeviceMedia>

    suspend fun saveToGallery(media: LocalNote.Content.Media): Boolean
}
