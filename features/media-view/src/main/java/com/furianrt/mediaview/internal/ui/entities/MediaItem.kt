package com.furianrt.mediaview.internal.ui.entities

import android.net.Uri

internal sealed class MediaItem(
    open val name: String,
    open val uri: Uri,
    open val ratio: Float,
    open val addedTime: Long,
) {
    data class Image(
        override val name: String,
        override val uri: Uri,
        override val ratio: Float,
        override val addedTime: Long,
    ) : MediaItem(name, uri, ratio, addedTime)

    data class Video(
        override val name: String,
        override val uri: Uri,
        override val ratio: Float,
        override val addedTime: Long,
        val duration: Int,
    ) : MediaItem(name, uri, ratio, addedTime)
}