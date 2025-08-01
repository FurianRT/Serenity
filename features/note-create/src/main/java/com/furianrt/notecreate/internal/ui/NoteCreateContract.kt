package com.furianrt.notecreate.internal.ui

import com.furianrt.notecreate.internal.ui.entites.NoteItem
import java.time.LocalDate

internal sealed interface NoteCreateUiState {
    data class Success(
        val note: NoteItem,
        val isInEditMode: Boolean,
    ) : NoteCreateUiState

    data object Loading : NoteCreateUiState
}

internal sealed interface NoteCreateEvent {
    data object OnButtonEditClick : NoteCreateEvent
    data object OnPageTitleFocused : NoteCreateEvent
    data object OnButtonBackClick : NoteCreateEvent
    data object OnButtonDateClick : NoteCreateEvent
    data object OnButtonDeleteClick : NoteCreateEvent
    data object OnConfirmDeleteClick : NoteCreateEvent
    data object OnPinClick : NoteCreateEvent
    data class OnContentChanged(val isChanged: Boolean) : NoteCreateEvent
    data class OnDateSelected(val date: LocalDate) : NoteCreateEvent
}

internal sealed interface NoteCreateEffect {
    data object CloseScreen : NoteCreateEffect
    data class ShowDateSelector(
        val date: LocalDate,
        val datesWithNotes: Set<LocalDate>,
    ) : NoteCreateEffect

    data object ShowDeleteConfirmationDialog : NoteCreateEffect
}