package com.furianrt.domain.entities

import android.net.Uri

class DeviceAlbum(
    val id: Long,
    val name: String,
    val thumbnail: Thumbnail,
    val mediaCount: Int,
) {
    sealed class Thumbnail(
        open val id: Long,
        open val uri: Uri,
    ) {
        class Image(
            id: Long,
            uri: Uri,
        ) : Thumbnail(id, uri)

        class Video(
            id: Long,
            uri: Uri,
            val duration: Int,
        ) : Thumbnail(id, uri)
    }
}