package com.furianrt.notecontent.entities

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import java.util.UUID

@Immutable
sealed class UiNoteContent(open val id: String, open val position: Int) {

    @Immutable
    data class Title(
        override val position: Int,
        override val id: String = UUID.randomUUID().toString(),
        val text: String = "",
    ) : UiNoteContent(id, position)

    @Immutable
    data class MediaBlock(
        override val id: String,
        override val position: Int,
        val media: ImmutableList<Media>,
    ) : UiNoteContent(id, position) {
        @Immutable
        sealed class Media(
            open val id: String,
            open val position: Int,
            open val ratio: Float,
        ) {
            @Immutable
            data class Image(
                override val id: String,
                override val position: Int,
                override val ratio: Float,
                val uri: String,
            ) : Media(id, position, ratio)

            /*  @Immutable
              data class Video(
                  val id: String,
              ) : Media*/
        }
    }

    fun changePosition(newPosition: Int) = when (this) {
        is Title -> copy(position = newPosition)
        is MediaBlock -> copy(position = newPosition)
    }
}

val UiNoteContent.MediaBlock.contentHeightDp: Int
    get() = when (media.count()) {
        1 -> 130
        2 -> 120
        3 -> 110
        4 -> 150
        else -> 180
    }
