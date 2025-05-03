package com.furianrt.storage.internal.device

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import com.furianrt.common.ErrorTracker
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.entities.DeviceMedia
import com.furianrt.domain.entities.LocalMedia
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val MAX_VIDEO_DURATION = 60 * 60 * 1000 // 1 hour

@Singleton
internal class SharedMediaSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatchersProvider,
    private val errorTracker: ErrorTracker,
) {
    suspend fun saveToGallery(
        media: LocalMedia,
    ): Boolean = withContext(dispatchers.io) {
        val values = ContentValues().apply {
            put(MediaStore.Files.FileColumns.DISPLAY_NAME, media.name)
            put(MediaStore.Files.FileColumns.MEDIA_TYPE, media.mediaType)
            put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.Files.FileColumns.IS_PENDING, 1)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
            values,
        )
        return@withContext if (uri != null) {
            try {
                resolver.openInputStream(media.uri)?.use { inputStream ->
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                true
            } catch (e: IOException) {
                errorTracker.trackNonFatalError(e)
                false
            }
        } else {
            false
        }
    }

    suspend fun getMediaList(): List<DeviceMedia> = withContext(dispatchers.io) {
        val mediaList = mutableListOf<DeviceMedia>()
        val volumes = MediaStore.getExternalVolumeNames(context)
        volumes.forEach { volume ->
            if (isActive) {
                mediaList.addAll(getMediaFiles(volume))
            }
        }
        return@withContext mediaList.sortedByDescending(DeviceMedia::date)
    }

    private suspend fun getMediaFiles(
        volumeName: String,
    ): List<DeviceMedia> = withContext(dispatchers.io) {
        val filesList = mutableListOf<DeviceMedia>()
        val collection = MediaStore.Files.getContentUri(volumeName)
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.DURATION,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT,
            MediaStore.Files.FileColumns.ORIENTATION,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.MIME_TYPE,
        )

        val selection = "(${MediaStore.Files.FileColumns.MEDIA_TYPE} = ? OR " +
                "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?) AND " +
                "(${MediaStore.Files.FileColumns.DURATION} < ? OR " +
                "${MediaStore.Files.FileColumns.DURATION} IS NULL)"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(),
            MAX_VIDEO_DURATION.toString(),
        )
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        val query = context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder,
        )
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
            val mediaTypeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT)
            val orientationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.ORIENTATION)
            while (cursor.moveToNext() && isActive) {
                val id = cursor.getLong(idColumn)
                val mediaType = cursor.getInt(mediaTypeColumn)
                val orientation = cursor.getInt(orientationColumn)
                val item = when (mediaType) {
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> DeviceMedia.Image(
                        id = id,
                        name = cursor.getString(nameColumn),
                        uri = ContentUris.withAppendedId(collection, id),
                        date = cursor.getLong(dateColumn),
                        ratio = cursor.getInt(widthColumn).toFloat() / cursor.getInt(heightColumn),
                    )

                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> DeviceMedia.Video(
                        id = id,
                        name = cursor.getString(nameColumn),
                        uri = ContentUris.withAppendedId(collection, id),
                        duration = cursor.getInt(durationColumn),
                        date = cursor.getLong(dateColumn),
                        ratio = if (orientation == 0 || orientation == 180) {
                            cursor.getInt(widthColumn).toFloat() / cursor.getInt(heightColumn)
                        } else {
                            cursor.getInt(heightColumn).toFloat() / cursor.getInt(widthColumn)
                        },
                    )

                    else -> continue
                }
                filesList.add(item)
            }
        }
        return@withContext filesList
    }

    private val LocalMedia.mediaType: Int
        get() = when (this.type) {
            LocalMedia.Type.IMAGE -> MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
            LocalMedia.Type.VIDEO -> MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
        }
}