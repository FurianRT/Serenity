package com.furianrt.notecontent.entities

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList

@Stable
sealed class UiNoteContent(open val position: Int) {
    @Immutable
    data class TitlesBlock(
        override val position: Int,
        val titles: ImmutableList<Title>,
    ) : UiNoteContent(position)

    @Immutable
    data class Title(val id: String, val text: String)

    @Immutable
    data class ImagesBlock(
        override val position: Int,
        val images: ImmutableList<Image>,
    ) : UiNoteContent(position)

    @Immutable
    data class Image(val id: String, val uri: String)
}
