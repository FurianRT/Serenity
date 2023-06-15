package com.furianrt.notecontent.entities

import androidx.compose.runtime.Immutable

@Immutable
sealed class UiNoteTag(
    open val id: String,
    open val title: String,
) {
    @Immutable
    data class Regular(
        override val id: String,
        override val title: String,
    ) : UiNoteTag(id, title)

    @Immutable
    data class Editable(
        override val id: String,
        override val title: String,
    ) : UiNoteTag(id, title)

    @Immutable
    data class Template(
        override val id: String,
        override val title: String,
    ) : UiNoteTag(id, title)
}
