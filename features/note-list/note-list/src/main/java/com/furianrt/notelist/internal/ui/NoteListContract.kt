package com.furianrt.notelist.internal.ui

import androidx.compose.runtime.Immutable
import com.furianrt.notelist.internal.ui.entities.NoteListScreenNote
import com.furianrt.uikit.theme.NoteFont
import com.furianrt.uikit.utils.DialogIdentifier
import kotlinx.collections.immutable.ImmutableList

internal sealed interface NoteListUiState {
    data object Loading : NoteListUiState

    data object Empty : NoteListUiState

    @Immutable
    data class Success(
        val notes: ImmutableList<NoteListScreenNote>,
        val scrollToPosition: Int?,
        val selectedNotesCount: Int,
        val font: NoteFont,
    ) : NoteListUiState

    val enableSelection: Boolean
        get() = this is Success && selectedNotesCount > 0
}

internal val NoteListUiState.hasNotes
    get() = this is NoteListUiState.Success && notes.isNotEmpty()

internal sealed interface NoteListEvent {
    data class OnNoteClick(val note: NoteListScreenNote) : NoteListEvent
    data class OnNoteLongClick(val note: NoteListScreenNote) : NoteListEvent
    data object OnScrollToTopClick : NoteListEvent
    data object OnSettingsClick : NoteListEvent
    data object OnSearchClick : NoteListEvent
    data object OnAddNoteClick : NoteListEvent
    data object OnScrolledToItem : NoteListEvent
    data object OnDeleteSelectedNotesClick : NoteListEvent
    data object OnConfirmDeleteSelectedNotesClick : NoteListEvent
    data object OnCloseSelectionClick : NoteListEvent
}

internal sealed interface NoteListEffect {
    data object ScrollToTop : NoteListEffect
    data object OpenSettingsScreen : NoteListEffect
    data object OpenNoteSearchScreen : NoteListEffect
    data class OpenNoteCreateScreen(val identifier: DialogIdentifier) : NoteListEffect
    data class OpenNoteViewScreen(
        val noteId: String,
        val identifier: DialogIdentifier,
    ) : NoteListEffect

    data class ShowConfirmNoteDeleteDialog(val notesCount: Int) : NoteListEffect
    data class ShowSyncProgressMessage(val message: String) : NoteListEffect
}
