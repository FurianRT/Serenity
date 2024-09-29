package com.furianrt.common

import android.net.Uri

class MediaResult(
    val media: List<Media>,
) {
    sealed class Media(
        val name: String,
        val uri: Uri,
        val ratio: Float,
    ) {
        class Image(
            name: String,
            uri: Uri,
            ratio: Float,
        ) : Media(name, uri, ratio)

        class Video(
            name: String,
            uri: Uri,
            ratio: Float,
            val duration: Int,
        ) : Media(name, uri, ratio)
    }
}