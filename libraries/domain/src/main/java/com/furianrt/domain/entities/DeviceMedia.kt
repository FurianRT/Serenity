package com.furianrt.domain.entities

import android.net.Uri

sealed class DeviceMedia(
    open val id: Long,
    open val name: String,
    open val uri: Uri,
    open val date: Long,
    open val ratio: Float,
    open val albumId: Long?,
    open val albumName: String?,
) {
    data class Image(
        override val id: Long,
        override val name: String,
        override val uri: Uri,
        override val date: Long,
        override val ratio: Float,
        override val albumId: Long?,
        override val albumName: String?,
    ) : DeviceMedia(id, name, uri, date, ratio, albumId, albumName)

    data class Video(
        override val id: Long,
        override val name: String,
        override val uri: Uri,
        override val date: Long,
        override val ratio: Float,
        override val albumId: Long?,
        override val albumName: String?,
        val duration: Int,
    ) : DeviceMedia(id, name, uri, date, ratio, albumId, albumName)
}