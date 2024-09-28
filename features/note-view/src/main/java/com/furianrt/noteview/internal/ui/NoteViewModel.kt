package com.furianrt.noteview.internal.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.furianrt.core.mapImmutable
import com.furianrt.core.updateState
import com.furianrt.domain.usecase.DeleteNoteUseCase
import com.furianrt.noteview.internal.ui.extensions.toNoteItem
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.noteview.api.NoteViewRoute
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
import javax.inject.Inject

@HiltViewModel
internal class NoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository,
    private val dialogResultCoordinator: DialogResultCoordinator,
    private val deleteNoteUseCase: DeleteNoteUseCase,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<NoteViewRoute>()

    private val _state = MutableStateFlow<NoteViewUiState>(NoteViewUiState.Loading)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NoteViewEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    init {
        observeNotes()
    }

    fun onEvent(event: NoteViewEvent) {
        when (event) {
            is NoteViewEvent.OnPageTitleFocusChange -> enableEditMode()
            is NoteViewEvent.OnButtonEditClick -> toggleEditMode()
            is NoteViewEvent.OnButtonBackClick -> {
                if (!event.isContentSaved) {
                    _effect.tryEmit(NoteViewEffect.SaveCurrentNoteContent)
                }
                _effect.tryEmit(NoteViewEffect.CloseScreen)
            }

            is NoteViewEvent.OnPageChange -> dialogResultCoordinator.onDialogResult(
                dialogIdentifier = DialogIdentifier(
                    requestId = route.requestId,
                    dialogId = route.dialogId,
                ),
                code = DialogResult.Ok(data = event.index),
            )

            is NoteViewEvent.OnDeleteClick -> {
                disableEditMode()
                launch { deleteNoteUseCase(event.noteId) }
            }
        }
    }

    private fun observeNotes() = launch {
        notesRepository.getAllNotes().collectLatest { notes ->
            if (notes.isEmpty()) {
                _effect.tryEmit(NoteViewEffect.CloseScreen)
                return@collectLatest
            }
            _state.update { localState ->
                when (localState) {
                    is NoteViewUiState.Success -> localState.copy(
                        initialPageIndex = notes.indexOfFirst { it.id == route.noteId },
                        notes = notes.mapImmutable(LocalNote::toNoteItem),
                    )

                    is NoteViewUiState.Loading -> NoteViewUiState.Success(
                        initialPageIndex = notes.indexOfFirst { it.id == route.noteId },
                        notes = notes.mapImmutable(LocalNote::toNoteItem),
                        isInEditMode = false,
                    )
                }
            }
        }
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
}
