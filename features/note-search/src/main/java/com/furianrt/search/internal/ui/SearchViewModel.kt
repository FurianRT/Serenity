package com.furianrt.search.internal.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.findInstance
import com.furianrt.core.hasItem
import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.LocalTag
import com.furianrt.search.internal.domain.GetAllUniqueTagsUseCase
import com.furianrt.search.internal.domain.GetFilteredNotesUseCase
import com.furianrt.search.internal.ui.entities.SelectedFilter
import com.furianrt.search.internal.ui.extensions.toFiltersList
import com.furianrt.search.internal.ui.extensions.toNoteItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private class SearchData(
    val allTags: List<LocalTag>,
    val queryText: String,
    val selectedFilters: ImmutableList<SelectedFilter>,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class SearchViewModel @Inject constructor(
    getAllUniqueTagsUseCase: GetAllUniqueTagsUseCase,
    private val getFilteredNotesUseCase: GetFilteredNotesUseCase,
) : ViewModel() {

    private val queryState = TextFieldState()
    private val queryTextFlow = snapshotFlow { queryState.text.toString() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "",
        )
    private val selectedFiltersFlow: MutableStateFlow<ImmutableList<SelectedFilter>> =
        MutableStateFlow(persistentListOf())

    val state: StateFlow<SearchUiState> = combine(
        getAllUniqueTagsUseCase(),
        queryTextFlow,
        selectedFiltersFlow,
    ) { allTags, queryText, selectedFilters ->
        SearchData(
            allTags = allTags,
            queryText = queryText,
            selectedFilters = selectedFilters,
        )
    }.flatMapLatest { data ->
        val dateFilter = data.selectedFilters.findInstance<SelectedFilter.DateRange>()
        val tagFilters = data.selectedFilters.filterIsInstance<SelectedFilter.Tag>()
        getFilteredNotesUseCase(
            query = data.queryText,
            tagsNames = tagFilters.map(SelectedFilter.Tag::title).toSet(),
            startDate = dateFilter?.start,
            endDate = dateFilter?.end,
        ).map { notes ->
            buildState(
                notes = notes,
                data = data,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState(searchQuery = queryState),
    )

    private val _effect = MutableSharedFlow<SearchEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnButtonCalendarClick -> {}
            is SearchEvent.OnButtonBackClick -> _effect.tryEmit(SearchEffect.CloseScreen)
            is SearchEvent.OnButtonClearQueryClick -> {
                queryState.edit { delete(0, originalText.length) }
            }

            is SearchEvent.OnRemoveFilterClick -> selectedFiltersFlow.update { tags ->
                tags.toPersistentList().remove(event.filter)
            }

            is SearchEvent.OnTagClick -> {
                if (!selectedFiltersFlow.value.hasItem { it.id == event.title }) {
                    selectedFiltersFlow.update { tags ->
                        tags.toPersistentList().add(SelectedFilter.Tag(event.title))
                    }
                }
            }
        }
    }

    private fun buildState(
        notes: List<LocalNote>,
        data: SearchData,
    ) = SearchUiState(
        searchQuery = queryState,
        selectedFilters = data.selectedFilters,
        state = if (notes.isEmpty()) {
            SearchUiState.State.Empty
        } else {
            SearchUiState.State.Success(
                items = if (data.queryText.isEmpty() && data.selectedFilters.isEmpty()) {
                    persistentListOf(data.allTags.toFiltersList())
                } else {
                    notes.mapImmutable(LocalNote::toNoteItem)
                },
            )
        },
    )
}