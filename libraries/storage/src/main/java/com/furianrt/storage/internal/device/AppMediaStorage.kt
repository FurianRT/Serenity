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

private const val COMPRESSION_VALUE = 10

internal class AppMediaStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatchersProvider,
) {

    suspend fun saveMediaFile(media: LocalNote.Content.Media): LocalNote.Content.Media? =
        when (media) {
            is LocalNote.Content.Image -> saveImage(media)
            is LocalNote.Content.Video -> saveVideo(media)
        }

    suspend fun deleteMediaFile(id: String): Boolean = withContext(dispatchers.io) {
        return@withContext File(context.filesDir, id).delete()
    }

    private suspend fun saveImage(
        image: LocalNote.Content.Image,
    ): LocalNote.Content.Image? = withContext(dispatchers.io) {
        try {
            val destFile = File(context.filesDir, "${image.id}.webp")

            val appImage = LocalNote.Content.Image(
                id = image.id,
                uri = destFile.toUri(),
                date = image.date,
                ratio = image.ratio,
            )

            if (destFile.exists()) {
                return@withContext appImage
            }

            FileOutputStream(destFile).use { fos ->
                val source = ImageDecoder.createSource(context.contentResolver, image.uri)
                val sourceBitmap = source.decodeBitmap { _, _ -> }
                sourceBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, COMPRESSION_VALUE, fos)
                fos.flush()
            }

            return@withContext appImage
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun saveVideo(
        video: LocalNote.Content.Video,
    ): LocalNote.Content.Video? = withContext(dispatchers.io) {

        return@withContext LocalNote.Content.Video(
            id = video.id,
            uri = Uri.EMPTY,
            ratio = 0f,
            duration = 0,
            date = 0,
        )
    }
}