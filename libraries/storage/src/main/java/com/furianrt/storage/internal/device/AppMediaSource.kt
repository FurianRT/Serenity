package com.furianrt.storage.internal.device

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.entities.LocalNote
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

private const val MEDIA_FOLDER = "media"
private const val VOICE_FOLDER = "voice"

@Singleton
internal class AppMediaSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatchersProvider,
) {
    suspend fun saveMediaFile(
        noteId: String,
        media: LocalNote.Content.Media,
    ): Uri? = withContext(dispatchers.io) {
        try {
            val destFile = File(context.filesDir, "$noteId/$MEDIA_FOLDER/${media.name}")
            destFile.parentFile?.mkdirs()
            context.contentResolver.openInputStream(media.uri)?.use { inputStream ->
                FileOutputStream(destFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return@withContext destFile.toUri()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteMediaFile(
        noteId: String,
        name: String,
    ): Boolean = withContext(dispatchers.io) {
        return@withContext try {
            File(context.filesDir, "$noteId/$MEDIA_FOLDER/$name").delete()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteMediaFile(noteId: String, names: Set<String>) {
        names.forEach { deleteMediaFile(noteId, it) }
    }

    suspend fun deleteAllMediaFiles(noteId: String): Boolean = withContext(dispatchers.io) {
        return@withContext try {
            File(context.filesDir, noteId).delete()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun createVoiceFile(
        noteId: String,
        voiceId: String,
    ): File? = withContext(dispatchers.io) {
        return@withContext try {
            File(context.filesDir, "$noteId/$VOICE_FOLDER/$voiceId").apply {
                parentFile?.mkdirs()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteVoiceFile(
        noteId: String,
        voiceId: String,
    ): Boolean = withContext(dispatchers.io) {
        return@withContext try {
            File(context.filesDir, "$noteId/$VOICE_FOLDER/$voiceId").delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteVoiceFile(noteId: String, voiceIds: Set<String>) {
        voiceIds.forEach { deleteVoiceFile(noteId, it) }
    }
}