package com.furianrt.noteview.internal.ui.container

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.furianrt.noteview.internal.ui.extensions.toContainerScreenNotes
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
internal class ContainerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository,
) : ViewModel() {

    init {
        loadNotes()
    }

    private val _state = MutableStateFlow<ContainerUiState>(ContainerUiState.Loading)
    val state: StateFlow<ContainerUiState> = _state.asStateFlow()

    private val _effect = Channel<ContainerEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: ContainerEvent) {
    }

    private fun loadNotes() = launch {
        val notes = notesRepository.getAllNotes()
        if (notes.isEmpty()) {
            _state.tryEmit(ContainerUiState.Empty)
        } else {
            _state.tryEmit(
                ContainerUiState.Success(
                    initialPageIndex = 2,
                    notes = notes.toContainerScreenNotes(),
                ),
            )
        }
    }
}
