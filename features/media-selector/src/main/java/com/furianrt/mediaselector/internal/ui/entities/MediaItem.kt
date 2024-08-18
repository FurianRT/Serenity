package com.furianrt.mediaselector.internal.ui.entities

import android.net.Uri

sealed class MediaItem(
    open val id: Long,
    open val uri: Uri,
    open val title: String,
    open val isSelected: Boolean,
) {
    data class Image(
        override val id: Long,
        override val uri: Uri,
        override val title: String,
        override val isSelected: Boolean,
    ) : MediaItem(id, uri, title, isSelected)

    data class Video(
        override val id: Long,
        override val uri: Uri,
        override val title: String,
        override val isSelected: Boolean,
        val duration: String,
    ) : MediaItem(id, uri, title, isSelected)
}
