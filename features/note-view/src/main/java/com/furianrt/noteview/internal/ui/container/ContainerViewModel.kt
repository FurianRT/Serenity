package com.furianrt.noteview.internal.ui.container

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.furianrt.core.mapImmutable
import com.furianrt.storage.api.entities.LocalSimpleNote
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class ContainerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ContainerUiState>(ContainerUiState.Loading)
    val state: StateFlow<ContainerUiState> = _state.asStateFlow()

    private val _effect = Channel<ContainerEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        observeNotes()
    }

    fun onEvent(event: ContainerEvent) {
        when (event) {
            is ContainerEvent.OnButtonEditClick -> {
                _state.update { it.toggleEditModeState() }
            }

            is ContainerEvent.OnButtonBackClick -> {
                _effect.trySend(ContainerEffect.CloseScreen)
            }

            is ContainerEvent.OnPageTitleFocusChange -> {
                _state.update { it.enableEditModeState() }
            }
        }
    }

    private fun observeNotes() = launch {
        notesRepository.getAllNotesSimple().collectLatest { notes ->
            if (notes.isEmpty()) {
                _state.tryEmit(ContainerUiState.Empty)
            } else {
                val noteId = savedStateHandle.get<String>("noteId")
                _state.tryEmit(
                    ContainerUiState.Success(
                        initialPageIndex = notes.indexOfFirst { it.id == noteId },
                        date = "30 Sep 1992",
                        notesIds = notes.mapImmutable(LocalSimpleNote::id),
                        isInEditMode = false,
                    ),
                )
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
