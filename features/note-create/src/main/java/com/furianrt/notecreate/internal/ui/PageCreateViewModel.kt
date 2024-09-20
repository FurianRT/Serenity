package com.furianrt.notecreate.internal.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.furianrt.notecreate.internal.domain.InsertNoteUseCase
import com.furianrt.storage.api.entities.SimpleNote
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class PageCreateViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val insertNoteUseCase: InsertNoteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(buildInitialState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NoteCreateEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    fun onEvent(event: NoteCreateEvent) {
        when (event) {
            is NoteCreateEvent.OnPageTitleFocusChange -> enableEditMode()
            is NoteCreateEvent.OnButtonEditClick -> {
                if (_state.value.isInEditMode) {
                    launch { saveNote() }
                }
                toggleEditMode()
            }
            is NoteCreateEvent.OnButtonBackClick -> launch {
                if (!event.isContentSaved) {
                    saveNote()
                    _effect.tryEmit(NoteCreateEffect.SaveCurrentNoteContent)
                }
                _effect.tryEmit(NoteCreateEffect.CloseScreen)
            }
        }
    }

    private fun buildInitialState() = NoteCreateUiState(
        noteId = UUID.randomUUID().toString(),
        timestamp = System.currentTimeMillis(),
        isInEditMode = true,
    )

    private suspend fun saveNote() {
        insertNoteUseCase(
            SimpleNote(
                id = _state.value.noteId,
                timestamp = _state.value.timestamp,
            )
        )
    }

    private fun toggleEditMode() {
        _state.update { it.copy(isInEditMode = !it.isInEditMode) }
    }

    private fun enableEditMode() {
        _state.update { it.copy(isInEditMode = true) }
    }
}