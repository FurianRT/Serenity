package com.furianrt.storage.internal.device

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import com.furianrt.common.ErrorTracker
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.entities.LocalNote
import com.furianrt.storage.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

private const val MEDIA_FOLDER = "media"
private const val VOICE_FOLDER = "voice"
private const val IMAGE_COMPRESS_AMOUNT = 75

internal class SavedMediaData(
    val name: String,
    val uri: Uri,
)

@Singleton
internal class AppMediaSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatchersProvider,
    private val errorTracker: ErrorTracker,
) {
    suspend fun saveMediaFile(
        noteId: String,
        media: LocalNote.Content.Media,
    ): SavedMediaData? = try {
        when (media) {
            is LocalNote.Content.Image -> saveImage(media, noteId)
            is LocalNote.Content.Video -> saveVideo(media, noteId)
        }
    } catch (e: Exception) {
        errorTracker.trackNonFatalError(e)
        null
    }

    suspend fun deleteMediaFile(
        noteId: String,
        media: LocalNote.Content.Media,
    ): Boolean = withContext(dispatchers.io) {
        return@withContext try {
            File(context.filesDir, "$noteId/$MEDIA_FOLDER/${media.id}${media.name}").delete()
        } catch (e: Exception) {
            errorTracker.trackNonFatalError(e)
            false
        }
    }

    suspend fun deleteMediaFile(noteId: String, media: Set<LocalNote.Content.Media>) {
        media.forEach { deleteMediaFile(noteId, it) }
    }

    suspend fun deleteAllMediaFiles(noteId: String): Boolean = withContext(dispatchers.io) {
        return@withContext try {
            File(context.filesDir, noteId).deleteRecursively()
        } catch (e: Exception) {
            errorTracker.trackNonFatalError(e)
            false
        }
    }

    suspend fun createMediaFile(
        noteId: String,
        mediaId: String,
        mediaName: String,
    ): File? = withContext(dispatchers.io) {
        try {
            val file = File(context.filesDir, "$noteId/$MEDIA_FOLDER/$mediaId$mediaName")

            file.parentFile?.mkdirs()

            if (file.exists()) {
                file.delete()
            }

            if (file.createNewFile()) {
                return@withContext file
            } else {
                return@withContext null
            }
        } catch (e: Exception) {
            errorTracker.trackNonFatalError(e)
            null
        }
    }

    suspend fun createVoiceFile(
        noteId: String,
        voiceId: String,
    ): File? = withContext(dispatchers.io) {
        try {
            val file = File(context.filesDir, "$noteId/$VOICE_FOLDER/$voiceId")

            file.parentFile?.mkdirs()

            if (file.exists()) {
                file.delete()
            }

            if (file.createNewFile()) {
                return@withContext file
            } else {
                return@withContext null
            }
        } catch (e: Exception) {
            errorTracker.trackNonFatalError(e)
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
            errorTracker.trackNonFatalError(e)
            false
        }
    }

    suspend fun deleteVoiceFile(noteId: String, voiceIds: Set<String>) {
        voiceIds.forEach { deleteVoiceFile(noteId, it) }
    }

    fun getRelativeUri(file: File): Uri {
        return FileProvider.getUriForFile(context, BuildConfig.FILE_PROVIDER_AUTHORITY, file)
    }

    private suspend fun saveImage(
        image: LocalNote.Content.Image,
        noteId: String,
    ): SavedMediaData? = withContext(dispatchers.io) {
        val bitmap = context.contentResolver.openInputStream(image.uri)
            ?.use(BitmapFactory::decodeStream)
            ?: return@withContext null

        val imageNewName = image.name.replaceFileExtension(".webp")
        val destFile = createMediaFile(
            noteId = noteId,
            mediaId = image.id,
            mediaName = imageNewName,
        ) ?: return@withContext null

        FileOutputStream(destFile).use { outputStream ->
            bitmap.compress(
                Bitmap.CompressFormat.WEBP_LOSSY,
                IMAGE_COMPRESS_AMOUNT,
                outputStream,
            )
        }

        try {
            getExifInterface(image.uri)?.let { sourceExif ->
                val destExif = ExifInterface(destFile)
                destExif.setAttribute(
                    ExifInterface.TAG_ORIENTATION,
                    sourceExif.getAttribute(ExifInterface.TAG_ORIENTATION),
                )
                destExif.saveAttributes()
            }
        } catch (e: Exception) {
            errorTracker.trackNonFatalError(e)
        }

        return@withContext SavedMediaData(
            name = imageNewName,
            uri = getRelativeUri(destFile),
        )
    }

    private suspend fun saveVideo(
        video: LocalNote.Content.Video,
        noteId: String,
    ): SavedMediaData? = withContext(dispatchers.io) {
        val destFile = createMediaFile(
            noteId = noteId,
            mediaId = video.id,
            mediaName = video.name,
        ) ?: return@withContext null

        context.contentResolver.openInputStream(video.uri)?.use { inputStream ->
            FileOutputStream(destFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return@withContext SavedMediaData(
            name = video.name,
            uri = getRelativeUri(destFile),
        )
    }

    private fun getExifInterface(uri: Uri): ExifInterface? = try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            ExifInterface(inputStream)
        }
    } catch (e: Exception) {
        errorTracker.trackNonFatalError(e)
        null
    }

    private fun String.replaceFileExtension(newExtension: String): String {
        val lastDotIndex = lastIndexOf('.')
        val withoutExtension = if (lastDotIndex != -1) {
            substring(0, lastDotIndex)
        } else {
            this
        }
        return withoutExtension + newExtension
    }
}