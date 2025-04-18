package com.furianrt.permissions.utils

import android.Manifest
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private enum class MediaPermissionStatus {
    FULL_ACCESS,
    PARTIAL_ACCESS,
    DENIED
}

@Singleton
class PermissionsUtils @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        fun getMediaPermissionList(): List<String> {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                listOf(
                    READ_MEDIA_VIDEO,
                    READ_MEDIA_IMAGES,
                    READ_MEDIA_VISUAL_USER_SELECTED,
                )
            } else {
                listOf(
                    READ_MEDIA_VIDEO,
                    READ_MEDIA_IMAGES,
                )
            }
        }

        fun getAudioRecordPermission(): String {
            return Manifest.permission.RECORD_AUDIO
        }
    }

    fun hasPartialMediaAccess(): Boolean {
        return getMediaPermissionStatus() == MediaPermissionStatus.PARTIAL_ACCESS
    }

    fun mediaAccessDenied(): Boolean {
        return getMediaPermissionStatus() == MediaPermissionStatus.DENIED
    }

    private fun getMediaPermissionStatus(): MediaPermissionStatus {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            getMediaPermissionStatusUpsideDownCake()
        } else {
            getMediaPermissionStatusTiramisu()
        }
    }

    private fun getMediaPermissionStatusUpsideDownCake(): MediaPermissionStatus =
        when {
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
        return ContextCompat.checkSelfPermission(
            context,
            this
        ) == PermissionChecker.PERMISSION_GRANTED
    }
}