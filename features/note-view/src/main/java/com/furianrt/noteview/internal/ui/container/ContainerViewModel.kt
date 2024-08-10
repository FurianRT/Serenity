package com.furianrt.noteview.internal.ui.container

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.furianrt.core.mapImmutable
import com.furianrt.noteview.internal.ui.extensions.toContainerScreenNote
import com.furianrt.storage.api.entities.LocalSimpleNote
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _effect = MutableSharedFlow<ContainerEffect>(extraBufferCapacity = 1)
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
                _state.update { it.toggleEditModeState() }
            }

            is ContainerEvent.OnButtonBackClick -> {
                _effect.tryEmit(ContainerEffect.CloseScreen)
            }

            is ContainerEvent.OnPageTitleFocusChange -> {
                _state.update { it.enableEditModeState() }
            }
        }
    }

    private fun observeNotes() = launch {
        notesRepository.getAllNotesSimple().collectLatest { notes ->
            if (notes.isEmpty()) {
                _state.update { ContainerUiState.Empty }
            } else {
                _state.update { localState ->
                    when (localState) {
                        is ContainerUiState.Success -> localState.copy(
                            initialPageIndex = notes.indexOfFirst { it.id == initialNoteId },
                            notes = notes.mapImmutable(LocalSimpleNote::toContainerScreenNote),
                        )

                        is ContainerUiState.Empty, is ContainerUiState.Loading -> {
                            ContainerUiState.Success(
                                initialPageIndex = notes.indexOfFirst { it.id == initialNoteId },
                                notes = notes.mapImmutable(LocalSimpleNote::toContainerScreenNote),
                                isInEditMode = false,
                            )
                        }


                    }
                }
            }
        }
    }

    private fun ContainerUiState.toggleEditModeState() = when (this) {
        is ContainerUiState.Success -> copy(isInEditMode = !isInEditMode)
        else -> this
    }

    private fun ContainerUiState.enableEditModeState() = when (this) {
        is ContainerUiState.Success -> copy(isInEditMode = true)
        else -> this
    }
}
