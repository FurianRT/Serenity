package com.furianrt.domain.entities

import android.net.Uri

sealed class DeviceMedia(
    val id: Long,
    val name: String,
    val uri: Uri,
    val date: Long,
    val ratio: Float,
) {
    class Image(
        id: Long,
        name: String,
        uri: Uri,
        date: Long,
        ratio: Float,
    ) : DeviceMedia(id, name, uri, date, ratio)

    class Video(
        id: Long,
        name: String,
        uri: Uri,
        date: Long,
        ratio: Float,
        val duration: Int,
    ) : DeviceMedia(id, name, uri, date, ratio)
}