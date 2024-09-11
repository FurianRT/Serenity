package com.furianrt.mediaselector.api.entities

import android.net.Uri

class MediaSelectorResult(
    val media: List<Media>,
) {
    sealed class Media(
        val uri: Uri,
        val ratio: Float,
    ) {
        class Image(
            uri: Uri,
            ratio: Float,
        ) : Media(uri, ratio)

        class Video(
            uri: Uri,
            ratio: Float,
            val duration: Int,
        ) : Media(uri, ratio)
    }
}