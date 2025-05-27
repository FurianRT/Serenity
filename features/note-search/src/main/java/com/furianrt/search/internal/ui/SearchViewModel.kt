package com.furianrt.search.internal.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.DispatchersProvider
import com.furianrt.core.buildImmutableList
import com.furianrt.core.findInstance
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.LocalTag
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.domain.managers.SyncManager
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.domain.usecase.DeleteNoteUseCase
import com.furianrt.domain.usecase.GetFilteredNotesUseCase
import com.furianrt.search.api.entities.QueryData
import com.furianrt.search.internal.domain.GetAllUniqueTagsUseCase
import com.furianrt.search.internal.ui.entities.SearchListItem
import com.furianrt.search.internal.ui.entities.SelectedFilter
import com.furianrt.search.internal.ui.extensions.toNoteItem
import com.furianrt.search.internal.ui.extensions.toSelectedTag
import com.furianrt.search.internal.ui.extensions.toTagsList
import com.furianrt.uikit.R as uiR
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
import kotlinx.coroutines.flow.onEach
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
    val scrollToPosition: Int?,
)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
internal class SearchViewModel @Inject constructor(
    getAllUniqueTagsUseCase: GetAllUniqueTagsUseCase,
    private val getFilteredNotesUseCase: GetFilteredNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val notesRepository: NotesRepository,
    private val dispatchers: DispatchersProvider,
    private val dialogResultCoordinator: DialogResultCoordinator,
    private val syncManager: SyncManager,
    private val resourcesManager: ResourcesManager,
) : ViewModel(), DialogResultListener {

    private val scrollToPositionState = MutableStateFlow<Int?>(null)
    private val selectedNotesState = MutableStateFlow<Set<String>>(emptySet())
    private val queryState = TextFieldState()
    private val queryTextFlow = snapshotFlow { queryState.text.toString() }
        .debounce(QUERY_DEBOUNCE_DURATION)
        .onEach { scrollToPositionState.update { 0 } }
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
        scrollToPositionState,
    ) { allTags, queryText, selectedFilters, scrollToPosition ->
        SearchData(
            allTags = allTags,
            queryText = queryText,
            selectedFilters = selectedFilters,
            scrollToPosition = scrollToPosition,
        )
    }.flatMapLatest { data ->
        val dateFilter = data.selectedFilters.findInstance<SelectedFilter.DateRange>()
        val tagFilters = data.selectedFilters.filterIsInstance<SelectedFilter.Tag>()
        combine(
            selectedNotesState,
            getFilteredNotesUseCase(
                query = data.queryText,
                tagsNames = tagFilters.map(SelectedFilter.Tag::title).toSet(),
                startDate = dateFilter?.start,
                endDate = dateFilter?.end,
            ),
        ) { selectedNotes, notes ->
            buildState(
                notes = notes,
                data = data,
                selectedNotes = selectedNotes,
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
            is SearchEvent.OnScrolledToItem -> scrollToPositionState.update { null }
            is SearchEvent.OnNoteItemClick -> if (selectedNotesState.value.isEmpty()) {
                openNoteViewScreen(event.noteId)
            } else {
                addOrRemoveSelectedNote(event.noteId)
            }

            is SearchEvent.OnCloseSelectionClick -> clearSelectedNotes()
            is SearchEvent.OnConfirmDeleteSelectedNotesClick -> launch {
                deleteSelectedNotes(selectedNotesState.value)
            }

            is SearchEvent.OnDeleteSelectedNotesClick -> onDeleteSelectedNotesClick()
            is SearchEvent.OnNoteLongClick -> addOrRemoveSelectedNote(event.noteId)
        }
    }

    override fun onDialogResult(dialogId: Int, result: DialogResult) {
        /* when (dialogId) {
             NOTE_VIEW_DIALOG_ID -> if (result is DialogResult.Ok<*>) {
                 val successState = state.value.state as? SearchUiState.State.Success ?: return
                 val position = result.data as Int
                 scrollToNoteState.update { successState.items.getOrNull(position + 1)?.id }
             }
         }*/
    }

    private fun clearQuery() {
        if (selectedNotesState.value.isEmpty()) {
            queryState.edit { delete(0, originalText.length) }
        }
    }

    private fun removeFilter(filter: SelectedFilter) {
        if (selectedNotesState.value.isEmpty()) {
            selectedFiltersFlow.update { it.toPersistentList().remove(filter) }
            scrollToPositionState.update { 0 }
        }
    }

    private fun addTagFilter(title: String) {
        val alreadyHasFilter = state.value.selectedFilters.any { it.isSelected && it.id == title }
        val isSelectionActive = selectedNotesState.value.isEmpty()
        if (isSelectionActive && !alreadyHasFilter) {
            selectedFiltersFlow.update { it.toPersistentList().add(SelectedFilter.Tag(title)) }
            scrollToPositionState.update { 0 }
        }
    }

    private fun addDateFilter(start: LocalDate, end: LocalDate?) {
        val filterItem = SelectedFilter.DateRange(start, end)
        selectedFiltersFlow.update { filters ->
            filters.toPersistentList()
                .removeAll { it.id == filterItem.id }
                .add(filterItem)
        }
        scrollToPositionState.update { 0 }
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

    private suspend fun deleteSelectedNotes(notes: Set<String>) {
        clearSelectedNotes()
        deleteNoteUseCase(notes)
    }

    private fun clearSelectedNotes() {
        selectedNotesState.update { emptySet() }
    }

    private fun onDeleteSelectedNotesClick() {
        when {
            syncManager.isBackupInProgress() -> _effect.tryEmit(
                SearchEffect.ShowSyncProgressMessage(
                    message = resourcesManager.getString(uiR.string.backup_in_progress),
                ),
            )

            syncManager.isRestoreInProgress() -> _effect.tryEmit(
                SearchEffect.ShowSyncProgressMessage(
                    message = resourcesManager.getString(uiR.string.restore_in_progress),
                ),
            )

            else -> _effect.tryEmit(
                SearchEffect.ShowConfirmNoteDeleteDialog(
                    notesCount = selectedNotesState.value.count(),
                )
            )
        }
    }

    private fun addOrRemoveSelectedNote(noteId: String) {
        selectedNotesState.update { selectedNotes ->
            if (selectedNotes.contains(noteId)) {
                selectedNotes.toMutableSet().apply { remove(noteId) }
            } else {
                selectedNotes.toMutableSet().apply { add(noteId) }
            }

        }
    }

    private suspend fun buildState(
        notes: List<LocalNote>,
        selectedNotes: Set<String>,
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
                    val notesItems = notes.map { note ->
                        note.toNoteItem(isSelected = selectedNotes.contains(note.id))
                    }
                    addAll(notesItems)
                }
            } else {
                persistentListOf(data.allTags.toTagsList())
            }
            SearchUiState.State.Success(
                items = items,
                notesCount = notes.count(),
                selectedNotesCount = selectedNotes.count(),
                scrollToPosition = data.scrollToPosition,
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