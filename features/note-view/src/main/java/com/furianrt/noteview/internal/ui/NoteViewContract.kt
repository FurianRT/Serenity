package com.furianrt.noteview.internal.ui

import com.furianrt.noteview.internal.ui.entites.NoteItem
import kotlinx.collections.immutable.ImmutableList

internal sealed interface NoteViewUiState {
    data object Loading : NoteViewUiState
    data class Success(
        val initialPageIndex: Int,
        val isInEditMode: Boolean,
        val notes: ImmutableList<NoteItem>,
    ) : NoteViewUiState
}

internal sealed interface NoteViewEvent {
    data object OnButtonEditClick : NoteViewEvent
    data class OnButtonBackClick(val isContentSaved: Boolean) : NoteViewEvent
    data object OnPageTitleFocusChange : NoteViewEvent
    data class OnPageChange(val index: Int) : NoteViewEvent
    data class OnDeleteClick(val noteId: String) : NoteViewEvent
}

internal sealed interface NoteViewEffect {
    data object CloseScreen : NoteViewEffect
    data object SaveCurrentNoteContent : NoteViewEffect
}
