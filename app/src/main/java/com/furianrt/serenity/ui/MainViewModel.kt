package com.furianrt.serenity.ui

import androidx.lifecycle.ViewModel
import com.furianrt.serenity.ui.extensions.toMainScreenNotes
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
) : ViewModel() {

    init {
        loadNotes()
    }

    private val _state = MutableStateFlow<MainUiState>(MainUiState.Loading())
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    private val _effect = Channel<MainEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnNoteClick -> {
            }

            is MainEvent.OnNoteTagClick -> {
            }

            is MainEvent.OnScrollToTopClick -> {
                _effect.trySend(MainEffect.ScrollToTop)
            }

            is MainEvent.OnSettingsClick -> {
                val newState = if (_state.value.hint != null) {
                    _state.value.updateHint(hint = null)
                } else {
                    _state.value.updateHint(
                        hint = MainUiState.AssistantHint(
                            message = "Hi, i’m your personal AI powered assistant. I can do a lot of things. Let me show you!",
                            forceShow = true,
                        ),
                    )
                }
                _state.tryEmit(newState)
            }

            is MainEvent.OnSearchClick -> {
            }

            is MainEvent.OnAddNoteClick -> {
            }

            is MainEvent.OnAssistantHintClick -> {
            }
        }
    }

    private fun loadNotes() = launch {
        val notes = notesRepository.getAllNotes()
        if (notes.isEmpty()) {
            _state.tryEmit(MainUiState.Empty())
        } else {
            _state.tryEmit(
                MainUiState.Success(
                    notes = notes.toMainScreenNotes().toImmutableList(),
                ),
            )
        }
    }

    private fun MainUiState.updateHint(hint: MainUiState.AssistantHint?) = when (this) {
        is MainUiState.Success -> copy(hint = hint)
        is MainUiState.Loading -> copy(hint = hint)
        is MainUiState.Empty -> copy(hint = hint)
    }
}
