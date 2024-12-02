package com.furianrt.notelistui.entities

import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import java.time.ZonedDateTime

sealed class UiNoteContent(open val id: String) {

    @Stable
    data class Title(
        override val id: String,
        val state: TextFieldState = TextFieldState(),
    ) : UiNoteContent(id)

    @Immutable
    data class MediaBlock(
        override val id: String,
        val media: ImmutableList<Media>,
    ) : UiNoteContent(id) {

        @Immutable
        sealed class Media(
            open val name: String,
            open val uri: Uri,
            open val ratio: Float,
            open val addedDate: ZonedDateTime,
        )

        @Immutable
        data class Image(
            override val name: String,
            override val uri: Uri,
            override val ratio: Float,
            override val addedDate: ZonedDateTime,
        ) : Media(name, uri, ratio, addedDate)

        @Immutable
        data class Video(
            override val name: String,
            override val uri: Uri,
            override val ratio: Float,
            override val addedDate: ZonedDateTime,
            val duration: Int,
        ) : Media(name, uri, ratio, addedDate)
    }
}

val UiNoteContent.MediaBlock.contentHeight: Dp
    get() = when (media.count()) {
        1 -> 130.dp
        2 -> 120.dp
        3 -> 110.dp
        4 -> 150.dp
        else -> 180.dp
    }
