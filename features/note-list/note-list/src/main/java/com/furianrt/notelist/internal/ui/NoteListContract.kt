package com.furianrt.notelist.internal.ui

import androidx.compose.runtime.Immutable
import com.furianrt.notelist.internal.ui.entities.NoteListScreenNote
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.theme.NoteFont
import com.furianrt.uikit.utils.DialogIdentifier

internal data class NoteListUiState(
    val theme: UiThemeColor,
    val hasAutoBackupFailure: Boolean,
    val content: Content,
) {
    sealed interface Content {
        data object Loading : Content

        data object Empty : Content

        @Immutable
        data class Success(
            val notes: List<NoteListScreenNote>,
            val scrollToPosition: Int?,
            val selectedNotesCount: Int,
            val font: NoteFont,
        ) : Content

        val enableSelection: Boolean
            get() = this is Success && selectedNotesCount > 0
    }
}

internal val NoteListUiState.hasNotes
    get() = content is NoteListUiState.Content.Success && content.notes.isNotEmpty()

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
    data object OnCloseBackupErrorClick : NoteListEvent
    data object OnFixBackupErrorClick : NoteListEvent
}

internal sealed interface NoteListEffect {
    data object ScrollToTop : NoteListEffect
    data object OpenSettingsScreen : NoteListEffect
    data object OpenNoteSearchScreen : NoteListEffect
    data object OpenBackupScreen : NoteListEffect
    data class OpenNoteCreateScreen(val identifier: DialogIdentifier) : NoteListEffect
    data class OpenNoteViewScreen(
        val noteId: String,
        val identifier: DialogIdentifier,
    ) : NoteListEffect

    data class ShowConfirmNoteDeleteDialog(val notesCount: Int) : NoteListEffect
    data class ShowSyncProgressMessage(val message: String) : NoteListEffect
}
