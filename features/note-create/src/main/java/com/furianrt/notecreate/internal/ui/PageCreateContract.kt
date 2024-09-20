package com.furianrt.notecreate.internal.ui

internal data class NoteCreateUiState(
    val noteId: String,
    val timestamp: Long,
    val isInEditMode: Boolean,
)

internal sealed interface NoteCreateEvent {
    data object OnButtonEditClick : NoteCreateEvent
    data object OnPageTitleFocusChange : NoteCreateEvent
    data class OnButtonBackClick(val isContentSaved: Boolean) : NoteCreateEvent
}

internal sealed interface NoteCreateEffect {
    data object CloseScreen : NoteCreateEffect
    data object SaveCurrentNoteContent : NoteCreateEffect
}