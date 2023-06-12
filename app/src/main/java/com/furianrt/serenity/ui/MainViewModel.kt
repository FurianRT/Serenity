package com.furianrt.serenity.ui

import androidx.lifecycle.ViewModel
import com.furianrt.notecontent.extensions.toUiNote
import com.furianrt.storage.api.entities.LocalNote
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
class MainViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
) : ViewModel() {

    init {
        loadNotes()
    }

    private val _state = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    private val _effect = Channel<MainEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnScrollToTopClick -> {
                _effect.trySend(MainEffect.ScrollToTop)
            }

            is MainEvent.OnSettingsClick -> {
            }

            is MainEvent.OnSearchClick -> {
            }

            is MainEvent.OnAddNoteClick -> {
            }
        }
    }

    private fun loadNotes() = launch {
        val notes = notesRepository.getAllNotes()
        if (notes.isEmpty()) {
            _state.tryEmit(MainUiState.Empty)
        } else {
            _state.tryEmit(MainUiState.Success(notes.map(LocalNote::toUiNote)))
        }
    }
}
