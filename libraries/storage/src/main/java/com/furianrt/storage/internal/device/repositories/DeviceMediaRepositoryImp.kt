package com.furianrt.storage.internal.device.repositories

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.furianrt.core.DispatchersProvider
import com.furianrt.storage.api.entities.DeviceMedia
import com.furianrt.storage.api.repositories.DeviceMediaRepository
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val MAX_VIDEO_DURATION = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)

internal class DeviceMediaRepositoryImp @Inject constructor(
    private val context: Context,
    private val dispatchers: DispatchersProvider,
) : DeviceMediaRepository {
    override suspend fun getMediaList(): List<DeviceMedia> = withContext(dispatchers.io) {
        val mediaList = mutableListOf<DeviceMedia>()
        val volumes = MediaStore.getExternalVolumeNames(context)
        volumes.forEach { volume ->
            if (isActive) {
                mediaList.addAll(getImages(volume))
            }
            if (isActive) {
                mediaList.addAll(getVideo(volume))
            }
        }
        return@withContext mediaList.sortedByDescending(DeviceMedia::date)
    }

    private suspend fun getImages(
        volumeName: String,
    ): List<DeviceMedia.Image> = withContext(dispatchers.io) {
        val photoList = mutableListOf<DeviceMedia.Image>()
        val collection = MediaStore.Images.Media.getContentUri(volumeName)
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder,
        )
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            while (cursor.moveToNext() && isActive) {
                val id = cursor.getLong(idColumn)
                val item = DeviceMedia.Image(
                    id = id,
                    uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    ),
                    title = cursor.getString(nameColumn),
                    date = cursor.getLong(dateColumn),
                )
                photoList.add(item)
            }
        }

        return@withContext photoList
    }

    private suspend fun getVideo(
        volumeName: String,
    ): List<DeviceMedia.Video> = withContext(dispatchers.io) {
        val videoList = mutableListOf<DeviceMedia.Video>()
        val collection = MediaStore.Video.Media.getContentUri(volumeName)
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
        )
        val selection = "${MediaStore.Video.Media.DURATION} <= ?"
        val selectionArgs = arrayOf(MAX_VIDEO_DURATION.toString())
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"
        val query = context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder,
        )
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            while (cursor.moveToNext() && isActive) {
                val id = cursor.getLong(idColumn)
                val item = DeviceMedia.Video(
                    id = id,
                    uri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    ),
                    title = cursor.getString(nameColumn),
                    duration = cursor.getInt(durationColumn),
                    size = cursor.getInt(sizeColumn),
                    date = cursor.getLong(dateColumn),
                )
                videoList.add(item)
            }
        }
        return@withContext videoList
    }
}