package com.furianrt.storage.api.entities

import android.net.Uri

sealed class DeviceMedia(
    val id: Long,
    val uri: Uri,
    val title: String,
    val date: Long,
) {
    class Image(
        id: Long,
        uri: Uri,
        title: String,
        date: Long,
    ) : DeviceMedia(id, uri, title, date)

    class Video(
        id: Long,
        uri: Uri,
        title: String,
        date: Long,
        val duration: Int,
        val size: Int,
    ) : DeviceMedia(id, uri, title, date)
}