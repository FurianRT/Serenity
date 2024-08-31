package com.furianrt.storage.internal.device.repositories

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns
import android.provider.MediaStore.Files.getContentUri
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.furianrt.core.DispatchersProvider
import com.furianrt.storage.api.entities.DeviceMedia
import com.furianrt.storage.api.entities.MediaPermissionStatus
import com.furianrt.storage.api.repositories.DeviceMediaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class DeviceMediaRepositoryImp @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatchersProvider,
) : DeviceMediaRepository {

    override suspend fun getMediaList(): List<DeviceMedia> = withContext(dispatchers.default) {
        val mediaList = mutableListOf<DeviceMedia>()
        val volumes = MediaStore.getExternalVolumeNames(context)
        volumes.forEach { volume ->
            if (isActive) {
                mediaList.addAll(getMediaFiles(volume))
            }
        }
        return@withContext mediaList.sortedByDescending(DeviceMedia::date)
    }

    override fun getMediaPermissionStatus(): MediaPermissionStatus = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            getMediaPermissionStatusUpsideDownCake()
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            getMediaPermissionStatusTiramisu()
        }

        READ_EXTERNAL_STORAGE.isGranted() -> MediaPermissionStatus.FULL_ACCESS

        else -> MediaPermissionStatus.DENIED
    }

    private fun getMediaPermissionStatusUpsideDownCake(): MediaPermissionStatus = when {
        READ_MEDIA_IMAGES.isGranted() || READ_MEDIA_VIDEO.isGranted() -> {
            MediaPermissionStatus.FULL_ACCESS
        }

        READ_MEDIA_VISUAL_USER_SELECTED.isGranted() -> {
            MediaPermissionStatus.PARTIAL_ACCESS
        }

        else -> MediaPermissionStatus.DENIED
    }

    private fun getMediaPermissionStatusTiramisu(): MediaPermissionStatus =
        if (READ_MEDIA_IMAGES.isGranted() || READ_MEDIA_VIDEO.isGranted()) {
            MediaPermissionStatus.FULL_ACCESS
        } else {
            MediaPermissionStatus.DENIED
        }

    private fun String.isGranted(): Boolean {
        return ContextCompat.checkSelfPermission(context, this) == PERMISSION_GRANTED
    }

    private suspend fun getMediaFiles(
        volumeName: String,
    ): List<DeviceMedia> = withContext(dispatchers.io) {
        val filesList = mutableListOf<DeviceMedia>()
        val collection = getContentUri(volumeName)
        val projection = arrayOf(
            FileColumns._ID,
            FileColumns.DATE_ADDED,
            FileColumns.DURATION,
            FileColumns.MEDIA_TYPE,
            FileColumns.WIDTH,
            FileColumns.HEIGHT,
        )
        val sortOrder = "${FileColumns.DATE_ADDED} DESC"
        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder,
        )
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(FileColumns._ID)
            val durationColumn = cursor.getColumnIndexOrThrow(FileColumns.DURATION)
            val dateColumn = cursor.getColumnIndexOrThrow(FileColumns.DATE_ADDED)
            val mediaTypeColumn = cursor.getColumnIndexOrThrow(FileColumns.MEDIA_TYPE)
            val widthColumn = cursor.getColumnIndexOrThrow(FileColumns.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(FileColumns.HEIGHT)
            while (cursor.moveToNext() && isActive) {
                val id = cursor.getLong(idColumn)
                val mediaType = cursor.getInt(mediaTypeColumn)
                val item = when (mediaType) {
                    FileColumns.MEDIA_TYPE_IMAGE -> DeviceMedia.Image(
                        id = id,
                        uri = ContentUris.withAppendedId(collection, id),
                        date = cursor.getLong(dateColumn),
                        ratio = cursor.getInt(widthColumn).toFloat() / cursor.getInt(heightColumn),
                    )

                    FileColumns.MEDIA_TYPE_VIDEO -> DeviceMedia.Video(
                        id = id,
                        uri = ContentUris.withAppendedId(collection, id),
                        duration = cursor.getInt(durationColumn),
                        date = cursor.getLong(dateColumn),
                        ratio = cursor.getInt(widthColumn).toFloat() / cursor.getInt(heightColumn),
                    )

                    else -> continue
                }
                filesList.add(item)
            }
        }
        return@withContext filesList
    }
}