package com.furianrt.search.internal.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.furianrt.search.api.entities.QueryData
import com.furianrt.search.internal.ui.SearchUiState.State.Success
import com.furianrt.search.internal.ui.entities.SearchListItem
import com.furianrt.search.internal.ui.entities.SelectedFilter
import com.furianrt.uikit.utils.DialogIdentifier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

@Stable
internal data class SearchUiState(
    val searchQuery: TextFieldState = TextFieldState(),
    val selectedFilters: ImmutableList<SelectedFilter> = persistentListOf(),
    val state: State = Success(),
) {
    val enableSelection: Boolean
        get() = state is Success && state.selectedNotesCount > 0

    sealed interface State {
        @Immutable
        data class Success(
            val items: ImmutableList<SearchListItem> = persistentListOf(),
            val scrollToPosition: Int? = null,
            val notesCount: Int = 0,
            val selectedNotesCount: Int = 0,
        ) : State

        data object Empty : State
    }
}

internal sealed interface SearchEvent {
    data object OnButtonBackClick : SearchEvent
    data object OnButtonCalendarClick : SearchEvent
    data object OnButtonClearQueryClick : SearchEvent
    data object OnScrolledToItem : SearchEvent
    data class OnTagClick(val title: String) : SearchEvent
    data class OnDateFilterClick(val date: SelectedFilter.DateRange) : SearchEvent
    data class OnRemoveFilterClick(val filter: SelectedFilter) : SearchEvent
    data class OnDateRangeSelected(val start: LocalDate, val end: LocalDate?) : SearchEvent
    data class OnNoteItemClick(val noteId: String) : SearchEvent
    data class OnNoteLongClick(val noteId: String) : SearchEvent
    data object OnDeleteSelectedNotesClick : SearchEvent
    data object OnConfirmDeleteSelectedNotesClick : SearchEvent
    data object OnCloseSelectionClick : SearchEvent
}

internal sealed interface SearchEffect {
    data class ShowDateSelector(
        val start: LocalDate?,
        val end: LocalDate?,
        val datesWithNotes: Set<LocalDate>,
    ) : SearchEffect
    data class OpenNoteViewScreen(
        val noteId: String,
        val identifier: DialogIdentifier,
        val queryData: QueryData,
    ) : SearchEffect

    data object CloseScreen : SearchEffect
    data class ShowConfirmNoteDeleteDialog(val notesCount: Int) : SearchEffect
    data class ShowSyncProgressMessage(val message: String) : SearchEffect
}