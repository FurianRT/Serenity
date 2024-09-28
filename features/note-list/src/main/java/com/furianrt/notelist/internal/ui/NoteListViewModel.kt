package com.furianrt.notelist.internal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.domain.usecase.DeleteNoteUseCase
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.notelist.internal.ui.extensions.toMainScreenNotes
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.DialogResult
import com.furianrt.uikit.utils.DialogResultCoordinator
import com.furianrt.uikit.utils.DialogResultListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val TAG = "MainViewModel"
private const val NOTE_VIEW_DIALOG_ID = 1
private const val NOTE_CREATE_DIALOG_ID = 2

@HiltViewModel
internal class NoteListViewModel @Inject constructor(
    notesRepository: NotesRepository,
    private val dialogResultCoordinator: DialogResultCoordinator,
    private val deleteNoteUseCase: DeleteNoteUseCase,
) : ViewModel(), DialogResultListener {

    private val scrollToNoteState = MutableStateFlow<String?>(null)
    val state: StateFlow<NoteListUiState> = combine(
        notesRepository.getAllNotes(),
        scrollToNoteState,
    ) { notes, noteId ->
        if (notes.isEmpty()) {
            NoteListUiState.Empty
        } else {
            NoteListUiState.Success(
                notes = notes.toMainScreenNotes(),
                scrollToPosition = notes.indexOfFirstOrNull { it.id == noteId },
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NoteListUiState.Loading,
    )

    private val _effect = MutableSharedFlow<NoteListEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    init {
        dialogResultCoordinator.addDialogResultListener(requestId = TAG, listener = this)
    }

    override fun onCleared() {
        dialogResultCoordinator.removeDialogResultListener(requestId = TAG, listener = this)
    }

    fun onEvent(event: NoteListEvent) {
        when (event) {
            is NoteListEvent.OnScrolledToItem -> scrollToNoteState.update { null }
            is NoteListEvent.OnScrollToTopClick -> _effect.tryEmit(NoteListEffect.ScrollToTop)
            is NoteListEvent.OnSettingsClick -> _effect.tryEmit(NoteListEffect.OpenSettingsScreen)
            is NoteListEvent.OnSearchClick -> {}
            is NoteListEvent.OnAddNoteClick -> _effect.tryEmit(
                NoteListEffect.OpenNoteCreateScreen(
                    identifier = DialogIdentifier(
                        dialogId = NOTE_CREATE_DIALOG_ID,
                        requestId = TAG,
                    ),
                )
            )

            is NoteListEvent.OnNoteClick -> _effect.tryEmit(
                NoteListEffect.OpenNoteViewScreen(
                    noteId = event.note.id,
                    identifier = DialogIdentifier(
                        dialogId = NOTE_VIEW_DIALOG_ID,
                        requestId = TAG,
                    ),
                )
            )

            is NoteListEvent.OnNoteLongClick -> launch {
                deleteNoteUseCase(event.note.id)
            }
        }
    }

    override fun onDialogResult(dialogId: Int, result: DialogResult) {
        when (dialogId) {
            NOTE_VIEW_DIALOG_ID -> if (result is DialogResult.Ok<*>) {
                val successState = state.value as? NoteListUiState.Success ?: return
                val position = result.data as Int
                scrollToNoteState.update { successState.notes[position].id }
            }

            NOTE_CREATE_DIALOG_ID -> if (result is DialogResult.Ok<*>) {
                scrollToNoteState.update { result.data as String }
            } else {
                scrollToNoteState.update { null }
            }
        }
    }
}
