package com.furianrt.noteview.internal.ui.page

import androidx.lifecycle.ViewModel
import com.furianrt.core.mapImmutable
import com.furianrt.core.orFalse
import com.furianrt.core.updateState
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.extensions.toLocalNoteContent
import com.furianrt.noteview.internal.ui.entites.NoteViewScreenNote
import com.furianrt.noteview.internal.ui.extensions.addTitleTemplates
import com.furianrt.noteview.internal.ui.extensions.removeTitleTemplates
import com.furianrt.noteview.internal.ui.extensions.toNoteViewScreenNote
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.uikit.extensions.launch
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = PageViewModel.Factory::class)
internal class PageViewModel @AssistedInject constructor(
    private val notesRepository: NotesRepository,
    @Assisted private val noteId: String,
) : ViewModel() {

    private val _state = MutableStateFlow<PageUiState>(PageUiState.Loading)
    val state: StateFlow<PageUiState> = _state.asStateFlow()

    private val _effect = Channel<PageEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        observeNote()
    }

    fun onEvent(event: PageEvent) {
        when (event) {
            is PageEvent.OnEditModeStateChange -> onEditModeStateChange(event.isEnabled)

            is PageEvent.OnTagClick -> {
            }

            is PageEvent.OnTagRemoved -> {
            }

            is PageEvent.OnTitleTextChange -> {
                changeTitleText(event.id, event.text)
            }

            is PageEvent.OnTitleDoneEditing -> {
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
    }

    private fun observeNote() = launch {
        notesRepository.getNote(noteId)
            .map { it?.toNoteViewScreenNote() }
            .collectLatest(::handleNoteResult)
    }

    private fun handleNoteResult(note: NoteViewScreenNote?) {
        if (note == null) {
            _state.update { PageUiState.Empty }
            return
        }

        val isInEditMode = (_state.value as? PageUiState.Success)?.isInEditMode.orFalse()
        _state.update { localState ->
            when (localState) {
                is PageUiState.Empty, PageUiState.Loading -> PageUiState.Success(
                    content = if (isInEditMode) {
                        note.content.addTitleTemplates()
                    } else {
                        note.content.removeTitleTemplates()
                    },
                    tags = note.tags,
                    isInEditMode = false,
                )

                is PageUiState.Success -> localState.copy(
                    content = if (isInEditMode) {
                        note.content.addTitleTemplates()
                    } else {
                        note.content.removeTitleTemplates()
                    },
                    tags = note.tags,
                )
            }

        }
    }

    private fun changeTitleText(id: String, text: String) {
        _state.updateState<PageUiState.Success> { localState ->
            localState.copy(
                content = localState.content.mapImmutable { content ->
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

    @AssistedFactory
    interface Factory {
        fun create(noteId: String): PageViewModel
    }
}
