package com.furianrt.serenity.ui

import androidx.lifecycle.ViewModel
import com.furianrt.serenity.ui.extensions.toMainScreenNotes
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<MainUiState>(MainUiState.Loading())
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MainEffect>(replay = 1)
    val effect = _effect.asSharedFlow()

    init {
        observeNotes()
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnNoteClick -> {
                _effect.tryEmit(MainEffect.OpenNoteScreen(event.note.id))
            }

            is MainEvent.OnNoteTagClick -> {
            }

            is MainEvent.OnScrollToTopClick -> {
                _effect.tryEmit(MainEffect.ScrollToTop)
            }

            is MainEvent.OnSettingsClick -> {
                _effect.tryEmit(MainEffect.ScrollToTop)
            }

            is MainEvent.OnSearchClick -> {
            }

            is MainEvent.OnAddNoteClick -> {
            }

            is MainEvent.OnAssistantHintClick -> {
            }
        }
    }

    private fun observeNotes() = launch {
        notesRepository.getAllNotes().collectLatest { notes ->
            if (notes.isEmpty()) {
                _state.tryEmit(MainUiState.Empty())
            } else {
                _state.tryEmit(
                    MainUiState.Success(
                        notes = notes.toMainScreenNotes(),
                    ),
                )
            }
        }
    }
}
