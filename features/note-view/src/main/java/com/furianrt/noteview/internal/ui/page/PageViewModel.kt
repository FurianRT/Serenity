package com.furianrt.noteview.internal.ui.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.furianrt.core.mapImmutable
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.noteview.internal.ui.extensions.toContainerScreenNote
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.uikit.extensions.launch
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

internal class PageViewModel @AssistedInject constructor(
    private val notesRepository: NotesRepository,
    @Assisted private val noteId: String,
) : ViewModel() {

    init {
        observeNote()
    }

    private val _state = MutableStateFlow<PageUiState>(PageUiState.Loading)
    val state: StateFlow<PageUiState> = _state.asStateFlow()

    private val _effect = Channel<PageEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: PageEvent) {
        when (event) {
            is PageEvent.OnEditModeStateChange -> {
                onEditModeStateChange(event.isEnabled)
            }

            is PageEvent.OnTagClick -> {
            }

            is PageEvent.OnTagRemoved -> {
            }
        }
    }

    private fun onEditModeStateChange(isEnabled: Boolean) {
        val oldState = state.value
        when {
            oldState is PageUiState.Success.View && isEnabled -> {
                val newState = PageUiState.Success.Edit(
                    content = oldState.content,
                    tags = oldState.tags.mapImmutable { tag ->
                        if (tag is UiNoteTag.Regular) {
                            tag.copy(isRemovable = true)
                        } else {
                            tag
                        }
                    },
                )
                _state.tryEmit(newState)
            }

            oldState is PageUiState.Success.Edit && !isEnabled -> {
                val newState = PageUiState.Success.View(
                    content = oldState.content,
                    tags = oldState.tags.mapImmutable { tag ->
                        if (tag is UiNoteTag.Regular) {
                            tag.copy(isRemovable = false)
                        } else {
                            tag
                        }
                    },
                )
                _state.tryEmit(newState)
            }
        }
    }

    private fun observeNote() = launch {
        val note = requireNotNull(notesRepository.getNote(noteId)).toContainerScreenNote()
        _state.tryEmit(PageUiState.Success.View(note.content, note.tags))
    }

    @AssistedFactory
    interface Factory {
        fun create(noteId: String): PageViewModel
    }

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface FactoryProvider {
        fun provide(): Factory
    }

    companion object {
        fun provideFactory(
            factory: Factory,
            noteId: String,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return factory.create(noteId) as T
            }
        }
    }
}
