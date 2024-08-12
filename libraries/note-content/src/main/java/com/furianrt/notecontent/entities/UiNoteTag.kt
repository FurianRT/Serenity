package com.furianrt.notecontent.entities

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import java.util.UUID

@Immutable
sealed class UiNoteTag(
    open val id: String,
) {
    @Immutable
    data class Regular(
        val title: String,
        val isRemovable: Boolean,
        override val id: String = UUID.randomUUID().toString(),
    ) : UiNoteTag(id)

    @Stable
    data class Template(
        val textState: TextFieldState = TextFieldState(),
        override val id: String = UUID.randomUUID().toString(),
    ) : UiNoteTag(id)
}
