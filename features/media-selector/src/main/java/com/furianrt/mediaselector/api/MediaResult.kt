package com.furianrt.mediaselector.api

import android.net.Uri
import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class MediaResult(
    val media: ImmutableList<Media>,
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