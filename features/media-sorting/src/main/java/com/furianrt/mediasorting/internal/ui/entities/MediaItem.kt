package com.furianrt.mediasorting.internal.ui.entities

import android.net.Uri
import androidx.compose.runtime.Immutable
import java.time.ZonedDateTime

@Immutable
internal sealed class MediaItem(
    open val id: String,
    open val name: String,
    open val uri: Uri,
    open val ratio: Float,
    open val addedDate: ZonedDateTime,
) {
    @Immutable
    data class Image(
        override val id: String,
        override val name: String,
        override val uri: Uri,
        override val ratio: Float,
        override val addedDate: ZonedDateTime,
    ) : MediaItem(id, name, uri, ratio, addedDate)

    @Immutable
    data class Video(
        override val id: String,
        override val name: String,
        override val uri: Uri,
        override val ratio: Float,
        override val addedDate: ZonedDateTime,
        val duration: Int,
    ) : MediaItem(id, name, uri, ratio, addedDate)
}