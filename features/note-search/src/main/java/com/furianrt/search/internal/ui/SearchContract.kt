package com.furianrt.search.internal.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import com.furianrt.search.internal.ui.entities.SearchListItem
import com.furianrt.search.internal.ui.entities.SelectedFilter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

@Immutable
internal data class SearchUiState(
    val searchQuery: TextFieldState = TextFieldState(),
    val selectedFilters: ImmutableList<SelectedFilter> = persistentListOf(),
    val state: State = State.Success(persistentListOf()),
) {
    sealed interface State {
        @Immutable
        data class Success(val items: ImmutableList<SearchListItem>) : State
        data object Empty : State
    }
}

internal sealed interface SearchEvent {
    data object OnButtonBackClick : SearchEvent
    data object OnButtonCalendarClick : SearchEvent
    data object OnButtonClearQueryClick : SearchEvent
    data class OnTagClick(val title: String) : SearchEvent
    data class OnRemoveFilterClick(val filter: SelectedFilter) : SearchEvent
    data class OnDateRangeSelected(val start: LocalDate, val end: LocalDate?) : SearchEvent
}

internal sealed interface SearchEffect {
    data class ShowDateSelector(val start: LocalDate?, val end: LocalDate?) : SearchEffect
    data object CloseScreen : SearchEffect
}