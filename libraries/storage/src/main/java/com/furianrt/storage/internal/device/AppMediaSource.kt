package com.furianrt.storage.internal.device

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import com.furianrt.common.ErrorTracker
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.entities.CustomSticker
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.NoteCustomBackground
import com.furianrt.storage.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val MEDIA_FOLDER = "media"
private const val VOICE_FOLDER = "voice"
private const val NOTE_BACKGROUND_FOLDER = "note_backgrounds"
private const val STICKERS_FOLDER = "stickers"
private const val EXPORT_FOLDER = "export"
private const val TEMP_MEDIA_FOLDER = "temp_media"
private const val IMAGE_COMPRESS_AMOUNT = 50
private const val BACKGROUND_COMPRESS_AMOUNT = 40
private const val STICKER_COMPRESS_AMOUNT = 30

internal class SavedMediaData(
    val name: String,
    val uri: Uri,
)

@Singleton
internal class AppMediaSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
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
    ): File? = createFile(folder = "$noteId/$MEDIA_FOLDER/$mediaId$mediaName")

    suspend fun createVoiceFile(
        noteId: String,
        voiceId: String,
    ): File? = createFile(folder = "$noteId/$VOICE_FOLDER/$voiceId")

    suspend fun createNoteBackgroundFile(
        id: String,
        name: String,
    ): File? = createFile(folder = "$NOTE_BACKGROUND_FOLDER/$id$name")

    suspend fun createCustomStickerFile(
        id: String,
        name: String,
    ): File? = createFile(folder = "$STICKERS_FOLDER/$id$name")

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

    suspend fun getAspectRatio(file: File): Float = withContext(dispatchers.io) {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, options)

        val width = options.outWidth
        val height = options.outHeight

        if (width > 0 && height > 0) {
            height.toFloat() / width.toFloat()
        } else {
            1f
        }
    }

    suspend fun deleteFile(file: File): Boolean = withContext(dispatchers.io) {
        return@withContext file.delete()
    }

    suspend fun deleteFile(uri: Uri): Boolean = withContext(dispatchers.io) {
        try {
            val relativePath = uri.path?.removePrefix("/my_files/") ?: return@withContext false
            val file = File(context.filesDir, relativePath)

            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            errorTracker.trackNonFatalError(e)
            false
        }
    }

    suspend fun saveNoteBackground(
        background: NoteCustomBackground,
    ): SavedMediaData? = withContext(dispatchers.io) {
        val inputStream = context.contentResolver.openInputStream(background.uri)
            ?: return@withContext null

        val bitmap = withContext(dispatchers.default) {
            inputStream.use(BitmapFactory::decodeStream)
        } ?: return@withContext null

        val extension = ".webp"
        val destFile = createNoteBackgroundFile(
            id = background.id,
            name = extension,
        ) ?: return@withContext null

        withContext(dispatchers.default) {
            destFile.outputStream().use { outputStream ->
                bitmap.compress(
                    Bitmap.CompressFormat.WEBP_LOSSY,
                    BACKGROUND_COMPRESS_AMOUNT,
                    outputStream,
                )
            }
        }

        try {
            getExifInterface(background.uri)?.let { sourceExif ->
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
            name = extension,
            uri = getRelativeUri(destFile),
        )
    }

    suspend fun deleteNoteBackgroundFile(
        background: NoteCustomBackground,
    ): Boolean = withContext(dispatchers.io) {
        return@withContext try {
            File(
                context.filesDir,
                "$NOTE_BACKGROUND_FOLDER/${background.id}${background.name}"
            ).delete()
        } catch (e: Exception) {
            errorTracker.trackNonFatalError(e)
            false
        }
    }

    suspend fun saveSticker(
        sticker: CustomSticker,
    ): SavedMediaData? = withContext(dispatchers.io) {
        val inputStream = context.contentResolver.openInputStream(sticker.uri)
            ?: return@withContext null

        val bitmap = withContext(dispatchers.default) {
            inputStream.use(BitmapFactory::decodeStream)
        } ?: return@withContext null

        val extension = ".webp"
        val destFile = createCustomStickerFile(
            id = sticker.id,
            name = extension,
        ) ?: return@withContext null

        withContext(dispatchers.default) {
            destFile.outputStream().use { outputStream ->
                bitmap.compress(
                    Bitmap.CompressFormat.WEBP_LOSSY,
                    STICKER_COMPRESS_AMOUNT,
                    outputStream,
                )
            }
        }

        try {
            getExifInterface(sticker.uri)?.let { sourceExif ->
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
            name = extension,
            uri = getRelativeUri(destFile),
        )
    }

    suspend fun deleteStickerFile(
        sticker: CustomSticker,
    ): Boolean = withContext(dispatchers.io) {
        return@withContext try {
            File(
                context.filesDir,
                "$STICKERS_FOLDER/${sticker.id}${sticker.name}"
            ).delete()
        } catch (e: Exception) {
            errorTracker.trackNonFatalError(e)
            false
        }
    }

    suspend fun createTempPdfFile(
        title: String,
        bitmaps: List<Bitmap>,
    ): Uri? = withContext(dispatchers.io) {
        File(context.cacheDir, EXPORT_FOLDER).deleteRecursively()
        createCacheFile(folder = "$EXPORT_FOLDER/$title.pdf")?.let { file ->
            bitmaps.toPdf(file)
            getRelativeUri(file)
        }
    }

    suspend fun createTempMediaFile(
        name: String,
    ): File? = withContext(dispatchers.io) {
        createCacheFile(folder = "$TEMP_MEDIA_FOLDER/$name")
    }

    suspend fun deleteTempMediaFile(
        name: String,
    ) = withContext(dispatchers.io) {
        File(context.cacheDir, "$TEMP_MEDIA_FOLDER/$name").deleteRecursively()
    }

    suspend fun clearCache() = withContext(dispatchers.io) {
        context.cacheDir.deleteRecursively()
    }

    private suspend fun List<Bitmap>.toPdf(
        outputFile: File,
    ) = withContext(dispatchers.io) {
        if (isEmpty()) return@withContext

        val document = PdfDocument()
        try {
            forEachIndexed { index, bitmap ->
                if (bitmap.isRecycled) return@forEachIndexed

                val pageInfo = PdfDocument.PageInfo.Builder(
                    bitmap.width,
                    bitmap.height,
                    index + 1,
                ).create()

                val page = document.startPage(pageInfo)

                val softwareBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
                    bitmap.copy(Bitmap.Config.ARGB_8888, false)
                } else {
                    bitmap
                }

                try {
                    page.canvas.drawBitmap(softwareBitmap, 0f, 0f, null)
                } finally {
                    document.finishPage(page)
                    if (softwareBitmap != bitmap) {
                        softwareBitmap.recycle()
                    }
                }
            }

            FileOutputStream(outputFile).buffered().use(document::writeTo)
        } catch (e: Exception) {
            errorTracker.trackNonFatalError(e)
        } finally {
            document.close()
        }
    }


    private suspend fun createFile(
        folder: String,
    ): File? = withContext(dispatchers.io) {
        try {
            val file = File(context.filesDir, folder)

            file.parentFile?.mkdirs()

            if (file.exists()) {
                file.delete()
            }

            if (file.createNewFile()) {
                return@withContext file
            } else {
                throw IOException("Can't create file")
            }
        } catch (e: Exception) {
            errorTracker.trackNonFatalError(e)
            null
        }
    }

    private suspend fun createCacheFile(
        folder: String,
    ): File? = withContext(dispatchers.io) {
        try {
            val file = File(context.cacheDir, folder)

            file.parentFile?.mkdirs()

            if (file.exists()) {
                file.delete()
            }

            if (file.createNewFile()) {
                return@withContext file
            } else {
                throw IOException("Can't create file")
            }
        } catch (e: Exception) {
            errorTracker.trackNonFatalError(e)
            null
        }
    }

    private suspend fun saveImage(
        image: LocalNote.Content.Image,
        noteId: String,
    ): SavedMediaData? = withContext(dispatchers.io) {
        val inputStream = context.contentResolver.openInputStream(image.uri)
            ?: return@withContext null

        val bitmap = withContext(dispatchers.default) {
            inputStream.use(BitmapFactory::decodeStream)
        } ?: return@withContext null

        val extension = ".webp"
        val destFile = createMediaFile(
            noteId = noteId,
            mediaId = image.id,
            mediaName = extension,
        ) ?: return@withContext null

        withContext(dispatchers.default) {
            destFile.outputStream().use { outputStream ->
                bitmap.compress(
                    Bitmap.CompressFormat.WEBP_LOSSY,
                    IMAGE_COMPRESS_AMOUNT,
                    outputStream,
                )
            }
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
            name = extension,
            uri = getRelativeUri(destFile),
        )
    }

    private suspend fun saveVideo(
        video: LocalNote.Content.Video,
        noteId: String,
    ): SavedMediaData? = withContext(dispatchers.io) {
        val extension = video.name
            .substringAfterLast(delimiter = ".", missingDelimiterValue = "")
            .takeIf { it.isNotEmpty() }
            ?.let { ".$it" }
            .orEmpty()

        val destFile = createMediaFile(
            noteId = noteId,
            mediaId = video.id,
            mediaName = extension,
        ) ?: return@withContext null

        context.contentResolver.openInputStream(video.uri)?.use { inputStream ->
            destFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return@withContext SavedMediaData(
            name = extension,
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
}