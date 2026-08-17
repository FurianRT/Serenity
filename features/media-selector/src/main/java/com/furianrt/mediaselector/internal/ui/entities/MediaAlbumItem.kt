package com.furianrt.mediaselector.internal.ui.entities

import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
internal data class MediaAlbumItem(
    val id: String,
    val name: String,
    val thumbnail: Thumbnail?,
    val mediaCount: Int,
) {
    @Immutable
    sealed class Thumbnail(
        open val id: String,
        open val uri: Uri,
    ) {
        @Immutable
        data class Image(
            override val id: String,
            override val uri: Uri,
        ) : Thumbnail(id, uri)

        @Immutable
        data class Video(
            override val id: String,
            override val uri: Uri,
            val duration: Int,
        ) : Thumbnail(id, uri)
    }

    companion object {
        const val ALL_MEDIA_ALBUM_ID = "all_vedia_album"
    }
}