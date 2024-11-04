package com.furianrt.notecontent.entities

import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

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
        sealed class Media(
            open val name: String,
            open val uri: Uri,
            open val ratio: Float,
            open val addedTime: Long,
        )

        data class Image(
            override val name: String,
            override val uri: Uri,
            override val ratio: Float,
            override val addedTime: Long,
        ) : Media(name, uri, ratio, addedTime)

        data class Video(
            override val name: String,
            override val uri: Uri,
            override val ratio: Float,
            override val addedTime: Long,
            val duration: Int,
        ) : Media(name, uri, ratio, addedTime)
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
