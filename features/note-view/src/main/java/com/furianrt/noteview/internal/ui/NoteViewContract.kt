package com.furianrt.noteview.internal.ui

import androidx.compose.runtime.Immutable
import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.noteview.internal.ui.entites.NoteItem
import com.furianrt.uikit.theme.NoteFont
import kotlinx.collections.immutable.ImmutableList
import java.time.LocalDate
import java.time.ZonedDateTime

internal sealed interface NoteViewUiState {
    data object Loading : NoteViewUiState

    @Immutable
    data class Success(
        val initialPageIndex: Int,
        val currentPageIndex: Int,
        val isInEditMode: Boolean,
        val notes: ImmutableList<NoteItem>,
        val date: ZonedDateTime,
        val font: NoteFont,
    ) : NoteViewUiState {

        val currentNote: NoteItem
            get() = notes[currentPageIndex]
    }
}

internal sealed interface NoteViewEvent {
    data object OnButtonEditClick : NoteViewEvent
    data object OnButtonBackClick : NoteViewEvent
    data object OnButtonDateClick : NoteViewEvent
    data object OnPageTitleFocused : NoteViewEvent
    data class OnPageChange(val index: Int) : NoteViewEvent
    data class OnDeleteClick(val noteId: String) : NoteViewEvent
    data class OnConfirmDeleteClick(val noteId: String) : NoteViewEvent
    data class OnPinClick(val noteId: String, val isPinned: Boolean) : NoteViewEvent
    data class OnContentChanged(val isChanged: Boolean) : NoteViewEvent
    data class OnDateSelected(val date: LocalDate) : NoteViewEvent
    data class OnBackgroundChanged(
        val noteId: String,
        val background: UiNoteBackground?,
    ) : NoteViewEvent
}

internal sealed interface NoteViewEffect {
    data object CloseScreen : NoteViewEffect
    data class ShowDateSelector(
        val date: LocalDate,
        val datesWithNotes: Set<LocalDate>,
    ) : NoteViewEffect

    data class ShowDeleteConfirmationDialog(val noteId: String) : NoteViewEffect
    data class ShowSyncProgressMessage(val message: String) : NoteViewEffect
}
