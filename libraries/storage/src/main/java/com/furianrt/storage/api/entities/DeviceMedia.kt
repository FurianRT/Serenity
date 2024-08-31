package com.furianrt.storage.api.entities

import android.net.Uri

sealed class DeviceMedia(
    val id: Long,
    val uri: Uri,
    val date: Long,
    val ratio: Float,
) {
    class Image(
        id: Long,
        uri: Uri,
        date: Long,
        ratio: Float,
    ) : DeviceMedia(id, uri, date, ratio)

    class Video(
        id: Long,
        uri: Uri,
        date: Long,
        ratio: Float,
        val duration: Int,
    ) : DeviceMedia(id, uri, date, ratio)
}