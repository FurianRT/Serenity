package com.furianrt.serenity.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.domain.usecase.DeleteNoteUseCase
import com.furianrt.serenity.ui.extensions.toMainScreenNotes
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.uikit.extensions.launch
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
internal class MainViewModel @Inject constructor(
    notesRepository: NotesRepository,
    private val dialogResultCoordinator: DialogResultCoordinator,
    private val deleteNoteUseCase: DeleteNoteUseCase,
) : ViewModel(), DialogResultListener {

    private val scrollToNoteState = MutableStateFlow<String?>(null)
    val state: StateFlow<MainUiState> = combine(
        notesRepository.getAllNotes(),
        scrollToNoteState,
    ) { notes, noteId ->
        if (notes.isEmpty()) {
            MainUiState.Empty
        } else {
            MainUiState.Success(
                notes = notes.toMainScreenNotes(),
                scrollToPosition = notes.indexOfFirstOrNull { it.id == noteId },
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainUiState.Loading,
    )

    private val _effect = MutableSharedFlow<MainEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    init {
        dialogResultCoordinator.addDialogResultListener(requestId = TAG, listener = this)
    }

    override fun onCleared() {
        dialogResultCoordinator.removeDialogResultListener(requestId = TAG, listener = this)
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnScrolledToItem -> scrollToNoteState.update { null }
            is MainEvent.OnScrollToTopClick -> _effect.tryEmit(MainEffect.ScrollToTop)
            is MainEvent.OnSettingsClick -> _effect.tryEmit(MainEffect.OpenSettingsScreen)
            is MainEvent.OnSearchClick -> {}
            is MainEvent.OnAddNoteClick -> _effect.tryEmit(
                MainEffect.OpenNoteCreateScreen(
                    dialogId = NOTE_CREATE_DIALOG_ID,
                    requestId = TAG,
                )
            )

            is MainEvent.OnNoteClick -> _effect.tryEmit(
                MainEffect.OpenNoteViewScreen(
                    noteId = event.note.id,
                    dialogId = NOTE_VIEW_DIALOG_ID,
                    requestId = TAG,
                )
            )

            is MainEvent.OnNoteLongClick -> launch {
                deleteNoteUseCase(event.note.id)
            }
        }
    }

    override fun onDialogResult(dialogId: Int, result: DialogResult) {
        when (dialogId) {
            NOTE_VIEW_DIALOG_ID -> if (result is DialogResult.Ok<*>) {
                val successState = state.value as? MainUiState.Success ?: return
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
