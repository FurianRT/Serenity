package com.furianrt.mediaview.internal.ui.entities

import android.net.Uri

internal sealed class MediaItem(
    open val name: String,
    open val uri: Uri,
    open val ratio: Float,
) {
    data class Image(
        override val name: String,
        override val uri: Uri,
        override val ratio: Float,
    ) : MediaItem(name, uri, ratio)

    data class Video(
        override val name: String,
        override val uri: Uri,
        override val ratio: Float,
        val duration: Int,
    ) : MediaItem(name, uri, ratio)
}