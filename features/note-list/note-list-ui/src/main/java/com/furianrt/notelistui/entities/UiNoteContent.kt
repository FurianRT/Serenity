package com.furianrt.notelistui.entities

import android.net.Uri
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.furianrt.notelistui.composables.title.NoteTitleState
import kotlinx.collections.immutable.ImmutableList
import java.time.ZonedDateTime

sealed class UiNoteContent(
    open val id: String,
    val bringIntoViewRequester: BringIntoViewRequester = BringIntoViewRequester(),
) {

    @Stable
    data class Title(
        override val id: String,
        val state: NoteTitleState,
        val focusRequester: FocusRequester = FocusRequester(),
    ) : UiNoteContent(id)

    @Stable
    data class Voice(
        override val id: String,
        val uri: Uri,
        val duration: Long,
        val progressState: ProgressState,
        val volume: ImmutableList<Float>,
    ) : UiNoteContent(id) {

        @Stable
        data class ProgressState(
            val progress: MutableFloatState = mutableFloatStateOf(0f),
        )

        val currentDuration: Long
            get() = (duration * progressState.progress.floatValue).toLong()
    }

    @Immutable
    data class MediaBlock(
        override val id: String,
        val media: ImmutableList<Media>,
    ) : UiNoteContent(id) {

        @Immutable
        sealed class Media(
            open val id: String,
            open val name: String,
            open val uri: Uri,
            open val ratio: Float,
            open val addedDate: ZonedDateTime,
        )

        @Immutable
        data class Image(
            override val id: String,
            override val name: String,
            override val uri: Uri,
            override val ratio: Float,
            override val addedDate: ZonedDateTime,
        ) : Media(id, name, uri, ratio, addedDate)

        @Immutable
        data class Video(
            override val id: String,
            override val name: String,
            override val uri: Uri,
            override val ratio: Float,
            override val addedDate: ZonedDateTime,
            val duration: Int,
        ) : Media(id, name, uri, ratio, addedDate)
    }
}

fun UiNoteContent?.isEmptyTitle() = this is UiNoteContent.Title && state.text.isEmpty()

val UiNoteContent.MediaBlock.contentHeight: Dp
    get() = when (media.count()) {
        1 -> 150.dp
        2 -> 130.dp
        3 -> 120.dp
        4 -> 150.dp
        else -> 180.dp
    }
