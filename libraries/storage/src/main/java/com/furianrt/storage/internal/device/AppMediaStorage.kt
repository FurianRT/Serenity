package com.furianrt.storage.internal.device

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.core.graphics.decodeBitmap
import androidx.core.net.toUri
import com.furianrt.core.DispatchersProvider
import com.furianrt.storage.api.entities.LocalNote
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

private const val COMPRESSION_VALUE = 70

@Singleton
internal class AppMediaStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatchersProvider,
) {

    suspend fun saveMediaFile(noteId: String, media: LocalNote.Content.Media): Uri? = when (media) {
        is LocalNote.Content.Image -> saveImage(noteId, media)
        is LocalNote.Content.Video -> saveVideo(noteId, media)
    }

    suspend fun deleteMediaFile(noteId: String, id: String): Boolean = withContext(dispatchers.io) {
        return@withContext try {
            File(context.filesDir, "$noteId/$id").delete()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteAllMediaFiles(noteId: String): Boolean = withContext(dispatchers.io) {
        return@withContext try {
            File(context.filesDir, noteId).delete()
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun saveImage(
        noteId: String,
        image: LocalNote.Content.Image,
    ): Uri? = withContext(dispatchers.default) {
        try {
            val destFile = File(context.filesDir, "$noteId/${image.id}.webp")
            if (destFile.exists()) {
                return@withContext destFile.toUri()
            }
            destFile.parentFile?.mkdirs()
            FileOutputStream(destFile).use { fos ->
                val source = ImageDecoder.createSource(context.contentResolver, image.uri)
                val sourceBitmap = source.decodeBitmap { _, _ -> }
                sourceBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, COMPRESSION_VALUE, fos)
                fos.flush()
            }

            return@withContext destFile.toUri()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun saveVideo(
        noteId: String,
        video: LocalNote.Content.Video,
    ): Uri? = withContext(dispatchers.default) {

        return@withContext Uri.EMPTY
    }
}