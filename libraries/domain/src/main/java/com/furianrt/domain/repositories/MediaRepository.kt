package com.furianrt.domain.repositories

import android.net.Uri
import com.furianrt.domain.entities.DeviceMedia
import com.furianrt.domain.entities.LocalMedia
import com.furianrt.domain.entities.LocalNote
import kotlinx.coroutines.flow.Flow
import java.io.File

interface MediaRepository {
    suspend fun insertMedia(
        noteId: String,
        media: List<LocalNote.Content.Media>,
        updateFile: Boolean = true,
    )
    suspend fun deleteMedia(noteId: String, media: List<LocalNote.Content.Media>)
    suspend fun deleteMediaFiles(noteId: String)
    fun getMedia(noteId: String): Flow<List<LocalNote.Content.Media>>
    fun getAllMedia(): Flow<List<LocalNote.Content.Media>>

    suspend fun getDeviceMediaList(): List<DeviceMedia>

    suspend fun saveToGallery(media: LocalMedia): Boolean

    suspend fun insertVoice(noteId: String, voices: List<LocalNote.Content.Voice>)
    suspend fun deleteVoice(noteId: String, voices: List<LocalNote.Content.Voice>)
    fun getVoices(noteId: String): Flow<List<LocalNote.Content.Voice>>
    fun getAllVoices(): Flow<List<LocalNote.Content.Voice>>

    suspend fun createMediaDestinationFile(noteId: String, mediaId: String): File?

    suspend fun createVoiceDestinationFile(noteId: String, voiceId: String): File?
    suspend fun deleteVoiceFile(noteId: String, voiceId: String): Boolean

    fun getRelativeUri(file: File): Uri
}
