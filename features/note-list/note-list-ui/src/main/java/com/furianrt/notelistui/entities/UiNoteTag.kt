package com.furianrt.notelistui.entities

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import java.util.UUID

sealed class UiNoteTag(
    open val id: String,
) {
    data class Regular(
        val title: String,
        val isRemovable: Boolean,
    ) : UiNoteTag(title)

    @Stable
    data class Template(
        val textState: TextFieldState = TextFieldState(),
        override val id: String = UUID.randomUUID().toString(),
    ) : UiNoteTag(id)

    companion object {
        const val BLOCK_ID = "tags_block"
    }
}
