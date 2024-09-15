package com.furianrt.serenity.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.serenity.ui.extensions.toMainScreenNotes
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.uikit.utils.DialogResult
import com.furianrt.uikit.utils.DialogResultCoordinator
import com.furianrt.uikit.utils.DialogResultListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val NOTE_VIEW_DIALOG_ID = 1
private const val NOTE_VIEW_REQUEST_ID = "MainViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class MainViewModel @Inject constructor(
    notesRepository: NotesRepository,
    private val dialogResultCoordinator: DialogResultCoordinator,
) : ViewModel(), DialogResultListener {

    private var scrollToPosition: Int? = null

    init {
        dialogResultCoordinator.addDialogResultListener(
            requestId = NOTE_VIEW_REQUEST_ID,
            listener = this,
        )
    }

    override fun onCleared() {
        dialogResultCoordinator.removeDialogResultListener(
            requestId = NOTE_VIEW_REQUEST_ID,
            listener = this,
        )
    }

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

    private val _effect = MutableSharedFlow<MainEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow().onSubscription {
        scrollToPosition?.let { _effect.tryEmit(MainEffect.ScrollToPosition(it)) }
        scrollToPosition = null
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnNoteClick -> {
                _effect.tryEmit(
                    MainEffect.OpenNoteScreen(
                        noteId = event.note.id,
                        dialogId = NOTE_VIEW_DIALOG_ID,
                        requestId = NOTE_VIEW_REQUEST_ID,
                    )
                )
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

    override fun onDialogResult(dialogId: Int, result: DialogResult) {
        when (dialogId) {
            NOTE_VIEW_DIALOG_ID -> if (result is DialogResult.Ok<*>) {
                scrollToPosition = result.data as Int
            }
        }
    }
}
