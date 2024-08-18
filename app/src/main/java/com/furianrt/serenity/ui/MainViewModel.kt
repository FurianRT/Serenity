package com.furianrt.serenity.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.serenity.ui.extensions.toMainScreenNotes
import com.furianrt.storage.api.repositories.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class MainViewModel @Inject constructor(
    notesRepository: NotesRepository,
) : ViewModel() {

    val state: StateFlow<MainUiState> = notesRepository.getAllNotes()
        .mapLatest { notes ->
            if (notes.isEmpty()) {
                MainUiState.Empty
            } else {
                MainUiState.Success(
                    notes = notes.toMainScreenNotes(),
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainUiState.Loading,
        )

    private val _effect = MutableSharedFlow<MainEffect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

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
                _effect.tryEmit(MainEffect.OpenSettingsScreen)
            }

            is MainEvent.OnSearchClick -> {
            }

            is MainEvent.OnAddNoteClick -> {
            }
        }
    }
}
