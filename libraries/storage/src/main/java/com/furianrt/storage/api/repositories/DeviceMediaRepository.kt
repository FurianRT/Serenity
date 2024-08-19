package com.furianrt.storage.api.repositories

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.os.Build
import com.furianrt.storage.api.entities.DeviceMedia
import com.furianrt.storage.api.entities.MediaPermissionStatus

interface DeviceMediaRepository {
    suspend fun getMediaList(): List<DeviceMedia>
    fun getMediaPermissionStatus(): MediaPermissionStatus

    companion object {
        fun getMediaPermissionList(): List<String> = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> listOf(
                READ_MEDIA_VIDEO,
                READ_MEDIA_IMAGES,
                READ_MEDIA_VISUAL_USER_SELECTED,
            )

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
                READ_MEDIA_VIDEO,
                READ_MEDIA_IMAGES,
            )

            else -> listOf(READ_EXTERNAL_STORAGE)
        }
    }
}