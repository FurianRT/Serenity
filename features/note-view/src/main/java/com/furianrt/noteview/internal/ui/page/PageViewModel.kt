package com.furianrt.noteview.internal.ui.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.furianrt.core.mapImmutable
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.extensions.toLocalNoteContent
import com.furianrt.noteview.internal.ui.entites.NoteViewScreenNote
import com.furianrt.noteview.internal.ui.extensions.addTitleTemplates
import com.furianrt.noteview.internal.ui.extensions.removeTitleTemplates
import com.furianrt.noteview.internal.ui.extensions.toContainerScreenNote
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.uikit.extensions.launch
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

private const val TITLE_FOCUS_DELAY = 200L

internal class PageViewModel @AssistedInject constructor(
    private val notesRepository: NotesRepository,
    @Assisted private val noteId: String,
) : ViewModel() {

    private val _state = MutableStateFlow<PageUiState>(PageUiState.Loading)
    val state: StateFlow<PageUiState> = _state.asStateFlow()

    private val _effect = Channel<PageEffect>()
    val effect = _effect.receiveAsFlow()

    private var titleFocusJob: Job? = null
    private var clickedTitleId: String? = null

    init {
        observeNote()
    }

    private var isInEditMode = false
        set(value) {
            onEditModeStateChange(value)
            field = value
        }

    fun onEvent(event: PageEvent) {
        when (event) {
            is PageEvent.OnEditModeStateChange -> {
                isInEditMode = event.isEnabled
            }

            is PageEvent.OnTagClick -> {
            }

            is PageEvent.OnTagRemoved -> {
            }

            is PageEvent.OnTitleTextChange -> {
                changeTitleText(event.id, event.text)
            }

            is PageEvent.OnTitleDoneEditing -> {
            }

            is PageEvent.OnTitleClick -> {
                clickedTitleId = event.id
            }
        }
    }

    private fun onEditModeStateChange(isEnabled: Boolean) {
        val currentState = _state.value
        if (currentState !is PageUiState.Success) {
            return
        }
        val newContent = if (isEnabled) {
            currentState.content.addTitleTemplates()
        } else {
            currentState.content.removeTitleTemplates()
        }

        _state.update { PageUiState.Success(newContent, currentState.tags, isEnabled) }
        saveNoteContent(currentState.content)

        if (isEnabled) {
            tryFocusTitle()
        } else {
            cleatTitleFocus()
        }
    }

    private fun observeNote() = launch {
        notesRepository.getNote(noteId)
            .map { it?.toContainerScreenNote() }
            .collectLatest(::handleNoteResult)
    }

    private fun handleNoteResult(note: NoteViewScreenNote?) {
        if (note == null) {
            _state.update { PageUiState.Empty }
            return
        }

        val newContent = if (isInEditMode) {
            note.content.addTitleTemplates()
        } else {
            note.content.removeTitleTemplates()
        }

        _state.update { PageUiState.Success(newContent, note.tags, isInEditMode) }
    }

    private fun changeTitleText(id: String, text: String) {
        _state.update { currentState ->
            if (currentState !is PageUiState.Success) {
                return@update currentState
            }
            currentState.copy(
                content = currentState.content.mapImmutable { content ->
                    if (content is UiNoteContent.Title && content.id == id) {
                        content.copy(text = text)
                    } else {
                        content
                    }
                },
            )
        }
    }

    private fun saveNoteContent(content: List<UiNoteContent>) = launch {
        notesRepository.upsertNoteContent(
            noteId = noteId,
            content = content.map(UiNoteContent::toLocalNoteContent),
        )
    }

    private fun tryFocusTitle() {
        titleFocusJob?.cancel()
        titleFocusJob = launch {
            delay((TITLE_FOCUS_DELAY))
            val noteContent = (_state.value as? PageUiState.Success)?.content ?: return@launch
            val lastTitleIndex = if (clickedTitleId != null) {
                noteContent.indexOfFirst { it.id == clickedTitleId }
            } else {
                noteContent.indexOfLast { it is UiNoteContent.Title }
            }
            clickedTitleId = null
            if (lastTitleIndex == -1) {
                return@launch
            }
            _effect.trySend(PageEffect.FocusTitle(lastTitleIndex))
        }
    }

    private fun cleatTitleFocus() {
        titleFocusJob?.cancel()
        _effect.trySend(PageEffect.FocusTitle(null))
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
