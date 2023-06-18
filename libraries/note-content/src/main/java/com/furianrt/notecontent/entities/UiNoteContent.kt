package com.furianrt.notecontent.entities

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList

@Stable
sealed class UiNoteContent(open val id: String, open val position: Int) {
    @Immutable
    data class Title(
        override val id: String,
        override val position: Int,
        val text: String,
    ) : UiNoteContent(id, position)

    @Immutable
    data class MediaBlock(
        override val id: String,
        override val position: Int,
        val images: ImmutableList<Image>,
    ) : UiNoteContent(id, position)

    @Immutable
    data class Image(val id: String, val uri: String)
}
