package com.furianrt.search.internal.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.DispatchersProvider
import com.furianrt.core.buildImmutableList
import com.furianrt.core.findInstance
import com.furianrt.core.hasItem
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.LocalTag
import com.furianrt.search.internal.domain.GetAllUniqueTagsUseCase
import com.furianrt.search.internal.domain.GetFilteredNotesUseCase
import com.furianrt.search.internal.ui.entities.SearchListItem
import com.furianrt.search.internal.ui.entities.SelectedFilter
import com.furianrt.search.internal.ui.extensions.toNoteItem
import com.furianrt.search.internal.ui.extensions.toSelectedTag
import com.furianrt.search.internal.ui.extensions.toTagsList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

private const val QUERY_DEBOUNCE_DURATION = 300L

private class SearchData(
    val allTags: List<LocalTag>,
    val queryText: String,
    val selectedFilters: ImmutableList<SelectedFilter>,
)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
internal class SearchViewModel @Inject constructor(
    getAllUniqueTagsUseCase: GetAllUniqueTagsUseCase,
    private val getFilteredNotesUseCase: GetFilteredNotesUseCase,
    private val dispatchers: DispatchersProvider,
) : ViewModel() {

    private val queryState = TextFieldState()
    private val queryTextFlow = snapshotFlow { queryState.text.toString() }
        .debounce(QUERY_DEBOUNCE_DURATION)
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
            is SearchEvent.OnButtonCalendarClick -> showDateSelector()
            is SearchEvent.OnButtonBackClick -> _effect.tryEmit(SearchEffect.CloseScreen)
            is SearchEvent.OnButtonClearQueryClick -> clearQuery()
            is SearchEvent.OnRemoveFilterClick -> removeFilter(event.filter)
            is SearchEvent.OnTagClick -> addTagFilter(event.title)
            is SearchEvent.OnDateRangeSelected -> addDateFilter(event.start, event.end)
        }
    }

    private fun clearQuery() {
        queryState.edit { delete(0, originalText.length) }
    }

    private fun removeFilter(filter: SelectedFilter) {
        selectedFiltersFlow.update { it.toPersistentList().remove(filter) }
    }

    private fun addTagFilter(title: String) {
        if (!state.value.selectedFilters.hasItem { it.isSelected && it.id == title }) {
            selectedFiltersFlow.update { it.toPersistentList().add(SelectedFilter.Tag(title)) }
        }
    }

    private fun addDateFilter(start: LocalDate, end: LocalDate?) {
        val filterItem = SelectedFilter.DateRange(start, end)
        selectedFiltersFlow.update { filters ->
            filters.toPersistentList()
                .removeAll { it.id == filterItem.id }
                .add(filterItem)
        }
    }

    private fun showDateSelector() {
        val dateFilter = state.value.selectedFilters.findInstance<SelectedFilter.DateRange>()
        _effect.tryEmit(SearchEffect.ShowDateSelector(dateFilter?.start, dateFilter?.end))
    }

    private suspend fun buildState(
        notes: List<LocalNote>,
        data: SearchData,
    ): SearchUiState = withContext(dispatchers.default) {
        val hasFiltersOrQuery = data.queryText.isNotEmpty() || data.selectedFilters.isNotEmpty()
        val state = if (notes.isEmpty()) {
            SearchUiState.State.Empty
        } else {
            SearchUiState.State.Success(
                items = if (hasFiltersOrQuery) {
                    buildImmutableList {
                        add(SearchListItem.NotesCountTitle(notes.count()))
                        addAll(notes.map(LocalNote::toNoteItem))
                    }
                } else {
                    persistentListOf(data.allTags.toTagsList())
                },
            )
        }
        val unselectedTags = if (hasFiltersOrQuery) {
            val noteIds = notes.map(LocalNote::id).toSet()
            data.allTags
                .filter { tag ->
                    tag.noteIds.intersect(noteIds).isNotEmpty() &&
                            !data.selectedFilters.hasItem { it.id == tag.title }
                }
                .map(LocalTag::toSelectedTag)
        } else {
            emptyList()
        }

        return@withContext SearchUiState(
            searchQuery = queryState,
            selectedFilters = data.selectedFilters.toPersistentList().addAll(unselectedTags),
            state = state,
        )
    }
}