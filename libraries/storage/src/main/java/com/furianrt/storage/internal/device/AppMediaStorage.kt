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

    suspend fun saveMediaFile(
        media: LocalNote.Content.Media,
    ): LocalNote.Content.Media? =
        when (media) {
            is LocalNote.Content.Image -> saveImage(media)
            is LocalNote.Content.Video -> saveVideo(media)
        }

    suspend fun deleteMediaFile(id: String): Boolean = withContext(dispatchers.io) {
        return@withContext File(context.filesDir, id).delete()
    }

    private suspend fun saveImage(
        image: LocalNote.Content.Image,
    ): LocalNote.Content.Image? = withContext(dispatchers.default) {
        try {
            val destFile = File(context.filesDir, "${image.id}.webp")
            val resultImage = LocalNote.Content.Image(
                id = image.id,
                uri = destFile.toUri(),
                addedTime = image.addedTime,
                ratio = image.ratio,
            )
            if (destFile.exists()) {
                return@withContext resultImage
            }

            FileOutputStream(destFile).use { fos ->
                val source = ImageDecoder.createSource(context.contentResolver, image.uri)
                val sourceBitmap = source.decodeBitmap { _, _ -> }
                sourceBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, COMPRESSION_VALUE, fos)
                fos.flush()
            }

            return@withContext resultImage
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun saveVideo(
        video: LocalNote.Content.Video,
    ): LocalNote.Content.Video? = withContext(dispatchers.default) {

        return@withContext LocalNote.Content.Video(
            id = video.id,
            uri = Uri.EMPTY,
            ratio = 1f,
            duration = 0,
            addedTime = 0,
        )
    }
}