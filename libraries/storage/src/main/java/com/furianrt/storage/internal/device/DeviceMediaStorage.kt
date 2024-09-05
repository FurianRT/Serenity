package com.furianrt.storage.internal.device

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.furianrt.core.DispatchersProvider
import com.furianrt.storage.api.entities.DeviceMedia
import com.furianrt.storage.api.entities.MediaPermissionStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class DeviceMediaStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatchersProvider,
) {

    suspend fun getMediaList(): List<DeviceMedia> = withContext(dispatchers.default) {
        val mediaList = mutableListOf<DeviceMedia>()
        val volumes = MediaStore.getExternalVolumeNames(context)
        volumes.forEach { volume ->
            if (isActive) {
                mediaList.addAll(getMediaFiles(volume))
            }
        }
        return@withContext mediaList.sortedByDescending(DeviceMedia::date)
    }

    fun getMediaPermissionStatus(): MediaPermissionStatus = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            getMediaPermissionStatusUpsideDownCake()
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            getMediaPermissionStatusTiramisu()
        }

        Manifest.permission.READ_EXTERNAL_STORAGE.isGranted() -> MediaPermissionStatus.FULL_ACCESS

        else -> MediaPermissionStatus.DENIED
    }

    private fun getMediaPermissionStatusUpsideDownCake(): MediaPermissionStatus = when {
        Manifest.permission.READ_MEDIA_IMAGES.isGranted() || Manifest.permission.READ_MEDIA_VIDEO.isGranted() -> {
            MediaPermissionStatus.FULL_ACCESS
        }

        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED.isGranted() -> {
            MediaPermissionStatus.PARTIAL_ACCESS
        }

        else -> MediaPermissionStatus.DENIED
    }

    private fun getMediaPermissionStatusTiramisu(): MediaPermissionStatus =
        if (Manifest.permission.READ_MEDIA_IMAGES.isGranted() || Manifest.permission.READ_MEDIA_VIDEO.isGranted()) {
            MediaPermissionStatus.FULL_ACCESS
        } else {
            MediaPermissionStatus.DENIED
        }

    private fun String.isGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            this
        ) == PermissionChecker.PERMISSION_GRANTED
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
        )
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder,
        )
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
            val mediaTypeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT)
            while (cursor.moveToNext() && isActive) {
                val id = cursor.getLong(idColumn)
                val mediaType = cursor.getInt(mediaTypeColumn)
                val item = when (mediaType) {
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> DeviceMedia.Image(
                        id = id,
                        uri = ContentUris.withAppendedId(collection, id),
                        date = cursor.getLong(dateColumn),
                        ratio = cursor.getInt(widthColumn).toFloat() / cursor.getInt(heightColumn),
                    )

                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> DeviceMedia.Video(
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