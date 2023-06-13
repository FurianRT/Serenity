package com.furianrt.notecontent.entities

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
sealed interface UiNoteContent {
    @Immutable
    data class Title(
        val id: String,
        val text: String,
    ) : UiNoteContent

    @Immutable
    data class Image(
        val id: String,
        val uri: String,
    ) : UiNoteContent
}
