package com.furianrt.storage.api.entities

import android.net.Uri

sealed class AppMedia(
    val id: String,
    val uri: Uri,
    val date: Long,
    val ratio: Float,
) {
    class Image(
        id: String,
        uri: Uri,
        date: Long,
        ratio: Float,
    ) : AppMedia(id, uri, date, ratio)

    class Video(
        id: String,
        uri: Uri,
        date: Long,
        ratio: Float,
        val duration: Int,
    ) : AppMedia(id, uri, date, ratio)
}