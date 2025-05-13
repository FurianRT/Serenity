package com.furianrt.search.internal.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.DispatchersProvider
import com.furianrt.core.buildImmutableList
import com.furianrt.core.findInstance
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.LocalTag
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.domain.usecase.GetFilteredNotesUseCase
import com.furianrt.search.api.entities.QueryData
import com.furianrt.search.internal.domain.GetAllUniqueTagsUseCase
import com.furianrt.search.internal.ui.entities.SearchListItem
import com.furianrt.search.internal.ui.entities.SelectedFilter
import com.furianrt.search.internal.ui.extensions.toNoteItem
import com.furianrt.search.internal.ui.extensions.toSelectedTag
import com.furianrt.search.internal.ui.extensions.toTagsList
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.DialogResult
import com.furianrt.uikit.utils.DialogResultCoordinator
import com.furianrt.uikit.utils.DialogResultListener
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

private const val TAG = "SearchViewModel"
private const val NOTE_VIEW_DIALOG_ID = 1
private const val QUERY_DEBOUNCE_DURATION = 300L

private class SearchData(
    val allTags: List<LocalTag>,
    val queryText: String,
    val selectedFilters: ImmutableList<SelectedFilter>,
    val scrollToNote: String?,
)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
internal class SearchViewModel @Inject constructor(
    getAllUniqueTagsUseCase: GetAllUniqueTagsUseCase,
    private val getFilteredNotesUseCase: GetFilteredNotesUseCase,
    private val notesRepository: NotesRepository,
    private val dispatchers: DispatchersProvider,
    private val dialogResultCoordinator: DialogResultCoordinator,
) : ViewModel(), DialogResultListener {

    private val scrollToNoteState = MutableStateFlow<String?>(null)
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
        scrollToNoteState,
    ) { allTags, queryText, selectedFilters, scrollToNote ->
        SearchData(
            allTags = allTags,
            queryText = queryText,
            selectedFilters = selectedFilters,
            scrollToNote = scrollToNote,
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
    }.flowOn(
        context = dispatchers.default,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState(searchQuery = queryState),
    )

    private val _effect = MutableSharedFlow<SearchEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    init {
        dialogResultCoordinator.addDialogResultListener(requestId = TAG, listener = this)
    }

    override fun onCleared() {
        dialogResultCoordinator.removeDialogResultListener(requestId = TAG, listener = this)
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnButtonCalendarClick, is SearchEvent.OnDateFilterClick -> launch {
                showDateSelector()
            }

            is SearchEvent.OnButtonBackClick -> _effect.tryEmit(SearchEffect.CloseScreen)
            is SearchEvent.OnButtonClearQueryClick -> clearQuery()
            is SearchEvent.OnRemoveFilterClick -> removeFilter(event.filter)
            is SearchEvent.OnTagClick -> addTagFilter(event.title)
            is SearchEvent.OnDateRangeSelected -> addDateFilter(event.start, event.end)
            is SearchEvent.OnScrolledToItem -> scrollToNoteState.update { null }
            is SearchEvent.OnNoteItemClick -> openNoteViewScreen(event.noteId)
        }
    }

    override fun onDialogResult(dialogId: Int, result: DialogResult) {
        when (dialogId) {
            NOTE_VIEW_DIALOG_ID -> if (result is DialogResult.Ok<*>) {
                val successState = state.value.state as? SearchUiState.State.Success ?: return
                val position = result.data as Int
                scrollToNoteState.update { successState.items.getOrNull(position + 1)?.id }
            }
        }
    }

    private fun clearQuery() {
        queryState.edit { delete(0, originalText.length) }
    }

    private fun removeFilter(filter: SelectedFilter) {
        selectedFiltersFlow.update { it.toPersistentList().remove(filter) }
    }

    private fun addTagFilter(title: String) {
        if (!state.value.selectedFilters.any { it.isSelected && it.id == title }) {
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

    private suspend fun showDateSelector() {
        val dateFilter = state.value.selectedFilters.findInstance<SelectedFilter.DateRange>()
        val dates = notesRepository.getUniqueNotesDates().first()
        _effect.tryEmit(
            SearchEffect.ShowDateSelector(
                start = dateFilter?.start,
                end = dateFilter?.end,
                datesWithNotes = dates,
            )
        )
    }

    private fun openNoteViewScreen(noteId: String) {
        val stateValue = state.value
        val dateFilter = stateValue.selectedFilters.findInstance<SelectedFilter.DateRange>()
        val selectedTags = selectedFiltersFlow.value
            .filterIsInstance<SelectedFilter.Tag>()
            .map(SelectedFilter.Tag::title)
            .toSet()
        _effect.tryEmit(
            SearchEffect.OpenNoteViewScreen(
                noteId = noteId,
                identifier = DialogIdentifier(
                    dialogId = NOTE_VIEW_DIALOG_ID,
                    requestId = TAG,
                ),
                queryData = QueryData(
                    query = stateValue.searchQuery.text.toString(),
                    tags = selectedTags,
                    startDate = dateFilter?.start,
                    endDate = dateFilter?.end,
                ),
            ),
        )
    }

    private suspend fun buildState(
        notes: List<LocalNote>,
        data: SearchData,
    ): SearchUiState = withContext(dispatchers.default) {
        val hasFilters = data.selectedFilters.isNotEmpty()
        val hasQuery = data.queryText.isNotBlank()
        val state = if (notes.isEmpty()) {
            SearchUiState.State.Empty
        } else {
            val items: ImmutableList<SearchListItem> = if (hasFilters || hasQuery) {
                buildImmutableList {
                    add(SearchListItem.NotesCountTitle(notes.count()))
                    addAll(notes.map(LocalNote::toNoteItem))
                }
            } else {
                persistentListOf(data.allTags.toTagsList())
            }
            SearchUiState.State.Success(
                items = items,
                scrollToPosition = items.indexOfFirstOrNull { item ->
                    item is SearchListItem.Note && item.id == data.scrollToNote
                },
            )
        }
        val unselectedTags = if (hasFilters) {
            val noteIds = notes.map(LocalNote::id).toSet()
            data.allTags
                .filter { tag -> data.selectedFilters.none { it.id == tag.title } }
                .sortedBy { it.noteIds.intersect(noteIds).isEmpty() }
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