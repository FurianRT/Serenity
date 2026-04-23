package com.furianrt.domain.entities

import android.net.Uri

data class DeviceAlbum(
    val id: Long,
    val name: String,
    val thumbnail: Thumbnail,
    val mediaCount: Int,
) {
    sealed class Thumbnail(
        open val id: Long,
        open val uri: Uri,
    ) {
        data class Image(
            override val id: Long,
            override val uri: Uri,
        ) : Thumbnail(id, uri)

        data class Video(
            override val id: Long,
            override val uri: Uri,
            val duration: Int,
        ) : Thumbnail(id, uri)
    }
}