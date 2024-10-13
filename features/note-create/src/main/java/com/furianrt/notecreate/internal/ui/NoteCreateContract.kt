package com.furianrt.notecreate.internal.ui

import com.furianrt.notecreate.internal.ui.entites.NoteItem

internal data class NoteCreateUiState(
    val note: NoteItem,
    val isInEditMode: Boolean,
)

internal sealed interface NoteCreateEvent {
    data object OnButtonEditClick : NoteCreateEvent
    data object OnPageTitleFocusChange : NoteCreateEvent
    data object OnButtonBackClick : NoteCreateEvent
    data class OnContentChanged(val isChanged: Boolean) : NoteCreateEvent
}

internal sealed interface NoteCreateEffect {
    data object CloseScreen : NoteCreateEffect
    data object SaveCurrentNoteContent : NoteCreateEffect
}