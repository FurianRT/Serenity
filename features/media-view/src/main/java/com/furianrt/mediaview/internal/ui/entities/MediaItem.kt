package com.furianrt.mediaview.internal.ui.entities

import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
internal sealed class MediaItem(
    open val id: String,
    open val name: String,
    open val uri: Uri,
    open val ratio: Float,
) {
    @Immutable
    data class Image(
        override val id: String,
        override val name: String,
        override val uri: Uri,
        override val ratio: Float,
    ) : MediaItem(id, name, uri, ratio)

    @Immutable
    data class Video(
        override val id: String,
        override val name: String,
        override val uri: Uri,
        override val ratio: Float,
        val duration: Int,
    ) : MediaItem(id, name, uri, ratio)
}