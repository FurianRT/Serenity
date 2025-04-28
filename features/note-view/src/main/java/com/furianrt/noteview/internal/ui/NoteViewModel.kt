package com.furianrt.noteview.internal.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.furianrt.core.doWithState
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.core.mapImmutable
import com.furianrt.core.updateState
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.domain.usecase.DeleteNoteUseCase
import com.furianrt.domain.usecase.GetFilteredNotesUseCase
import com.furianrt.noteview.api.NoteViewRoute
import com.furianrt.noteview.api.SearchDataType
import com.furianrt.noteview.internal.ui.extensions.toNoteItem
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.DialogResult
import com.furianrt.uikit.utils.DialogResultCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.math.min
import kotlin.reflect.typeOf

private const val EXTRA_CURRENT_PAGE = "current_page"

@HiltViewModel
internal class NoteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository,
    private val dialogResultCoordinator: DialogResultCoordinator,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val getFilteredNotesUseCase: GetFilteredNotesUseCase,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<NoteViewRoute>(
        typeMap = mapOf(typeOf<NoteViewRoute.SearchData?>() to SearchDataType),
    )

    private val _state = MutableStateFlow<NoteViewUiState>(NoteViewUiState.Loading)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NoteViewEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    private var isContentChanged = false

    init {
        observeNotes()
    }

    override fun onCleared() {
        if (isContentChanged) {
            launch {
                _effect.emit(NoteViewEffect.SaveCurrentNoteContent)
            }
        }
    }

    fun onEvent(event: NoteViewEvent) {
        when (event) {
            is NoteViewEvent.OnPageTitleFocusChange -> enableEditMode()
            is NoteViewEvent.OnButtonEditClick -> toggleEditMode()
            is NoteViewEvent.OnContentChanged -> isContentChanged = event.isChanged
            is NoteViewEvent.OnButtonBackClick -> _effect.tryEmit(NoteViewEffect.CloseScreen)
            is NoteViewEvent.OnButtonDateClick -> _state.doWithState<NoteViewUiState.Success> {
                _effect.tryEmit(NoteViewEffect.ShowDateSelector(date = it.date.toLocalDate()))
            }

            is NoteViewEvent.OnDateSelected -> {
                _state.doWithState<NoteViewUiState.Success> { successState ->
                    val zonedDateTime = ZonedDateTime.of(
                        event.date,
                        LocalTime.now(),
                        ZoneId.systemDefault(),
                    )
                    launch {
                        notesRepository.updateNoteDate(
                            noteId = successState.currentNote.id,
                            date = zonedDateTime,
                        )
                    }
                }
            }

            is NoteViewEvent.OnPageChange -> {
                _state.updateState<NoteViewUiState.Success> { currentState ->
                    savedStateHandle[EXTRA_CURRENT_PAGE] = event.index
                    currentState.copy(
                        currentPageIndex = event.index,
                        date = currentState.notes[event.index].date,
                    )
                }
                sendPageChangeResult(event.index)
            }

            is NoteViewEvent.OnDeleteClick -> {
                _effect.tryEmit(NoteViewEffect.ShowDeleteConfirmationDialog(event.noteId))
            }

            is NoteViewEvent.OnConfirmDeleteClick -> {
                disableEditMode()
                launch { deleteNoteUseCase(event.noteId) }
            }

            is NoteViewEvent.OnPinClick -> launch {
                toggleNotePinnedState(event.noteId, event.isPinned)
            }
        }
    }

    private fun observeNotes() = launch {
        val notesFlow = if (route.searchData != null) {
            getFilteredNotesUseCase(
                query = route.searchData.query,
                tagsNames = route.searchData.tags,
                startDate = route.searchData.startDate,
                endDate = route.searchData.endDate,
            )
        } else {
            notesRepository.getAllNotes()
        }

        notesFlow.collectLatest { notes ->
            if (notes.isEmpty()) {
                _effect.tryEmit(NoteViewEffect.CloseScreen)
                return@collectLatest
            }
            _state.update { localState ->
                when (localState) {
                    is NoteViewUiState.Success -> {
                        val initialPageIndex = notes.indexOfFirst { it.id == route.noteId }
                        val notesItems = notes.mapImmutable(LocalNote::toNoteItem)
                        val currentPageIndex = notesItems.indexOfFirstOrNull {
                            it.id == localState.currentNote.id
                        } ?: min(localState.currentPageIndex, notesItems.lastIndex)

                        localState.copy(
                            initialPageIndex = initialPageIndex,
                            currentPageIndex = currentPageIndex,
                            notes = notesItems,
                            date = notesItems[currentPageIndex].date,
                        )
                    }

                    is NoteViewUiState.Loading -> {
                        val initialPageIndex = notes.indexOfFirst { it.id == route.noteId }
                        val notesItems = notes.mapImmutable(LocalNote::toNoteItem)
                        NoteViewUiState.Success(
                            initialPageIndex = initialPageIndex,
                            currentPageIndex = savedStateHandle[EXTRA_CURRENT_PAGE]
                                ?: initialPageIndex,
                            notes = notesItems,
                            date = notesItems[initialPageIndex].date,
                            isInEditMode = false,
                        )
                    }
                }
            }
        }
    }

    private fun sendPageChangeResult(page: Int) {
        dialogResultCoordinator.onDialogResult(
            dialogIdentifier = DialogIdentifier(
                requestId = route.requestId,
                dialogId = route.dialogId,
            ),
            code = DialogResult.Ok(data = page),
        )
    }

    private fun toggleEditMode() {
        _state.updateState<NoteViewUiState.Success> { it.copy(isInEditMode = !it.isInEditMode) }
    }

    private fun enableEditMode() {
        _state.updateState<NoteViewUiState.Success> { it.copy(isInEditMode = true) }
    }

    private fun disableEditMode() {
        _state.updateState<NoteViewUiState.Success> { it.copy(isInEditMode = false) }
    }

    private suspend fun toggleNotePinnedState(noteId: String, isPinned: Boolean) {
        notesRepository.updateNoteIsPinned(noteId, !isPinned)
    }
}
