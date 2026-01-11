package com.furianrt.domain.entities

import android.net.Uri

sealed class DeviceMedia(
    val id: Long,
    val name: String,
    val uri: Uri,
    val date: Long,
    val ratio: Float,
    val albumId: Long?,
    val albumName: String?,
) {
    class Image(
        id: Long,
        name: String,
        uri: Uri,
        date: Long,
        ratio: Float,
        albumId: Long?,
        albumName: String?,
    ) : DeviceMedia(id, name, uri, date, ratio, albumId, albumName)

    class Video(
        id: Long,
        name: String,
        uri: Uri,
        date: Long,
        ratio: Float,
        albumId: Long?,
        albumName: String?,
        val duration: Int,
    ) : DeviceMedia(id, name, uri, date, ratio, albumId, albumName)
}