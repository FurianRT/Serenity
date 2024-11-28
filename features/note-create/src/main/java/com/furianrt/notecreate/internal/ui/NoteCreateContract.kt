package com.furianrt.notecreate.internal.ui

import com.furianrt.notecreate.internal.ui.entites.NoteItem
import java.time.LocalDate

internal data class NoteCreateUiState(
    val note: NoteItem,
    val isInEditMode: Boolean,
)

internal sealed interface NoteCreateEvent {
    data object OnButtonEditClick : NoteCreateEvent
    data object OnPageTitleFocusChange : NoteCreateEvent
    data object OnButtonBackClick : NoteCreateEvent
    data object OnButtonDateClick : NoteCreateEvent
    data class OnContentChanged(val isChanged: Boolean) : NoteCreateEvent
    data class OnDateSelected(val date: LocalDate) : NoteCreateEvent
}

internal sealed interface NoteCreateEffect {
    data object CloseScreen : NoteCreateEffect
    data object SaveCurrentNoteContent : NoteCreateEffect
    data class ShowDateSelector(val date: LocalDate) : NoteCreateEffect
}