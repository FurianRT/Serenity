package com.furianrt.noteview.internal.ui.container

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.furianrt.core.mapImmutable
import com.furianrt.core.updateState
import com.furianrt.noteview.internal.ui.extensions.toContainerScreenNote
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class ContainerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ContainerUiState>(ContainerUiState.Loading)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ContainerEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    private val initialNoteId by lazy(LazyThreadSafetyMode.NONE) {
        savedStateHandle.get<String>("noteId")
    }

    init {
        observeNotes()
    }

    fun onEvent(event: ContainerEvent) {
        when (event) {
            is ContainerEvent.OnButtonEditClick -> {
                _state.updateState<ContainerUiState.Success> { it.toggleEditMode() }
            }

            is ContainerEvent.OnButtonBackClick -> {
                if (!event.isContentSaved) {
                    _effect.tryEmit(ContainerEffect.SaveCurrentNoteContent)
                }
                _effect.tryEmit(ContainerEffect.CloseScreen)
            }

            is ContainerEvent.OnPageTitleFocusChange -> {
                _state.updateState<ContainerUiState.Success> { it.enableEditMode() }
            }
        }
    }

    private fun observeNotes() = launch {
        notesRepository.getAllNotes().collectLatest { notes ->
            if (notes.isEmpty()) {
                _effect.tryEmit(ContainerEffect.CloseScreen)
                return@collectLatest
            }
            _state.update { localState ->
                when (localState) {
                    is ContainerUiState.Success -> localState.copy(
                        initialPageIndex = notes.indexOfFirst { it.id == initialNoteId },
                        notes = notes.mapImmutable(LocalNote::toContainerScreenNote),
                    )

                    is ContainerUiState.Loading -> ContainerUiState.Success(
                        initialPageIndex = notes.indexOfFirst { it.id == initialNoteId },
                        notes = notes.mapImmutable(LocalNote::toContainerScreenNote),
                        isInEditMode = false,
                    )
                }
            }
        }
    }

    private fun ContainerUiState.Success.toggleEditMode() = copy(isInEditMode = !isInEditMode)

    private fun ContainerUiState.Success.enableEditMode() = copy(isInEditMode = true)
}
