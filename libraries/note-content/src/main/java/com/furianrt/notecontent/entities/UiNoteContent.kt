package com.furianrt.notecontent.entities

import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import java.util.UUID

sealed class UiNoteContent(open val id: String, open val position: Int) {

    @Stable
    data class Title(
        override val position: Int,
        override val id: String = UUID.randomUUID().toString(),
        val state: TextFieldState = TextFieldState(),
    ) : UiNoteContent(id, position)

    @Immutable
    data class MediaBlock(
        override val id: String,
        override val position: Int,
        val media: ImmutableList<Media>,
    ) : UiNoteContent(id, position) {
        sealed class Media(
            open val id: String,
            open val uri: Uri,
            open val position: Int,
            open val ratio: Float,
        )

        data class Image(
            override val id: String,
            override val uri: Uri,
            override val position: Int,
            override val ratio: Float,
        ) : Media(id, uri, position, ratio)

        data class Video(
            override val id: String,
            override val uri: Uri,
            override val position: Int,
            override val ratio: Float,
            val duration: Int,
        ) : Media(id, uri, position, ratio)
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
