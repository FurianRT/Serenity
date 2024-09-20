package com.furianrt.noteview.internal.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.furianrt.core.mapImmutable
import com.furianrt.core.updateState
import com.furianrt.noteview.internal.ui.extensions.toNoteItem
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.repositories.NotesRepository
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
    private val savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository,
    private val dialogResultCoordinator: DialogResultCoordinator,
) : ViewModel() {

    private val _state = MutableStateFlow<NoteViewUiState>(NoteViewUiState.Loading)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NoteViewEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    private val initialNoteId by lazy(LazyThreadSafetyMode.NONE) {
        savedStateHandle.get<String>("noteId")
    }

    private val dialogIdentifier by lazy(LazyThreadSafetyMode.NONE) {
        DialogIdentifier(
            requestId = savedStateHandle["requestId"]!!,
            dialogId = savedStateHandle["dialogId"]!!,
        )
    }

    init {
        observeNotes()
    }

    fun onEvent(event: NoteViewEvent) {
        when (event) {
            is NoteViewEvent.OnButtonEditClick -> {
                _state.updateState<NoteViewUiState.Success> { it.toggleEditMode() }
            }

            is NoteViewEvent.OnButtonBackClick -> {
                if (!event.isContentSaved) {
                    _effect.tryEmit(NoteViewEffect.SaveCurrentNoteContent)
                }
                _effect.tryEmit(NoteViewEffect.CloseScreen)
            }

            is NoteViewEvent.OnPageTitleFocusChange -> {
                _state.updateState<NoteViewUiState.Success> { it.enableEditMode() }
            }

            is NoteViewEvent.OnPageChange -> dialogResultCoordinator.onDialogResult(
                dialogIdentifier = dialogIdentifier,
                code = DialogResult.Ok(data = event.index),
            )
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
                        initialPageIndex = notes.indexOfFirst { it.id == initialNoteId },
                        notes = notes.mapImmutable(LocalNote::toNoteItem),
                    )

                    is NoteViewUiState.Loading -> NoteViewUiState.Success(
                        initialPageIndex = notes.indexOfFirst { it.id == initialNoteId },
                        notes = notes.mapImmutable(LocalNote::toNoteItem),
                        isInEditMode = false,
                    )
                }
            }
        }
    }

    private fun NoteViewUiState.Success.toggleEditMode() = copy(isInEditMode = !isInEditMode)

    private fun NoteViewUiState.Success.enableEditMode() = copy(isInEditMode = true)
}