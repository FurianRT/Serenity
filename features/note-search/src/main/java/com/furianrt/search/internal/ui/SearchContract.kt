package com.furianrt.search.internal.ui

import androidx.compose.foundation.text.input.TextFieldState
import com.furianrt.search.internal.ui.entities.SearchListItem
import com.furianrt.search.internal.ui.entities.TagsList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal data class SearchUiState(
    val searchQuery: TextFieldState = TextFieldState(),
    val selectedTags: ImmutableList<TagsList.SelectableItem> = persistentListOf(),
    val state: State = State.Loading,
) {
    sealed interface State {
        data class Success(
            val items: ImmutableList<SearchListItem>,
        ) : State

        data object Loading : State
        data object Empty : State
    }
}

internal sealed interface SearchEvent {

}

internal sealed interface SearchEffect {

}