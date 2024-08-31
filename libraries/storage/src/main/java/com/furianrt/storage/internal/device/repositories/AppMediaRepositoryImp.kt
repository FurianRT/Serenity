package com.furianrt.storage.internal.device.repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.core.graphics.decodeBitmap
import androidx.core.net.toUri
import com.furianrt.core.DispatchersProvider
import com.furianrt.storage.api.entities.AppMedia
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.internal.database.notes.repositories.AppMediaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

private const val COMPRESSION_VALUE = 10

internal class AppMediaRepositoryImp @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatchersProvider,
) : AppMediaRepository {

    override suspend fun saveMediaFile(media: LocalNote.Content.Media): AppMedia? = when (media) {
        is LocalNote.Content.Image -> saveImage(media)
        is LocalNote.Content.Video -> saveVideo(media)
    }

    override suspend fun deleteMediaFile(id : String): Boolean = withContext(dispatchers.io) {
        return@withContext File(context.filesDir, id).delete()
    }

    private suspend fun saveImage(
        image: LocalNote.Content.Image,
    ): AppMedia.Image? = withContext(dispatchers.io) {
        try {
            val destFile = File(context.filesDir, "${image.id}.webp")
            val source = ImageDecoder.createSource(context.contentResolver, image.uri)
            var ratio = 0f
            val sourceBitmap = source.decodeBitmap { info, _ ->
                ratio = info.size.width.toFloat() / info.size.height.toFloat()
            }
            FileOutputStream(destFile).use { fos ->
                sourceBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, COMPRESSION_VALUE, fos)
                fos.flush()
            }
            return@withContext AppMedia.Image(
                id = UUID.randomUUID().toString(),
                uri = destFile.toUri(),
                date = System.currentTimeMillis(),
                ratio = ratio,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun saveVideo(
        video: LocalNote.Content.Video,
    ): AppMedia.Video? = withContext(dispatchers.io) {

        return@withContext AppMedia.Video(
            id = UUID.randomUUID().toString(),
            uri = Uri.EMPTY,
            date = System.currentTimeMillis(),
            ratio = 0f,
            duration = 0,
        )
    }
}