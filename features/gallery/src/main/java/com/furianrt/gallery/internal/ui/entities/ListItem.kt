package com.furianrt.gallery.internal.ui.entities

import android.net.Uri

internal sealed class ListItem(
    open val id: String,
) {
    data class Title(
        val text: String,
    ) : ListItem(text)

    data class Photo(
        override val id: String,
        val uri: Uri,
        val label: String,
        val rotation: Float,
    ) : ListItem(id)
}
