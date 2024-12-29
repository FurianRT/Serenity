package com.furianrt.notecreate.internal.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.notecreate.internal.domain.InsertNoteUseCase
import com.furianrt.notecreate.internal.ui.entites.NoteItem
import com.furianrt.notecreate.internal.ui.extensions.toSimpleNote
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.DialogResult
import com.furianrt.uikit.utils.DialogResultCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class NoteCreateViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val insertNoteUseCase: InsertNoteUseCase,
    private val notesRepository: NotesRepository,
    private val dialogResultCoordinator: DialogResultCoordinator,
) : ViewModel() {

    private val _state = MutableStateFlow(buildInitialState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NoteCreateEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    private val dialogIdentifier by lazy(LazyThreadSafetyMode.NONE) {
        DialogIdentifier(
            requestId = savedStateHandle["requestId"]!!,
            dialogId = savedStateHandle["dialogId"]!!,
        )
    }

    private var isContentChanged = false

    override fun onCleared() {
        if (isContentChanged) {
            launch(NonCancellable) {
                saveNote()
                _effect.emit(NoteCreateEffect.SaveCurrentNoteContent)
            }
        }
    }

    fun onEvent(event: NoteCreateEvent) {
        when (event) {
            is NoteCreateEvent.OnPageTitleFocusChange -> enableEditMode()
            is NoteCreateEvent.OnButtonEditClick -> {
                if (_state.value.isInEditMode) {
                    launch { saveNote() }
                }
                toggleEditMode()
            }

            is NoteCreateEvent.OnButtonBackClick -> _effect.tryEmit(NoteCreateEffect.CloseScreen)
            is NoteCreateEvent.OnContentChanged -> isContentChanged = event.isChanged
            is NoteCreateEvent.OnButtonDateClick -> {
                _effect.tryEmit(
                    NoteCreateEffect.ShowDateSelector(state.value.note.date.toLocalDate()),
                )
            }

            is NoteCreateEvent.OnDateSelected -> {
                val zonedDateTime = ZonedDateTime.of(
                    event.date,
                    LocalTime.now(),
                    ZoneId.systemDefault()
                )
                launch {
                    notesRepository.updateNoteDate(
                        noteId = state.value.note.id,
                        date = zonedDateTime,
                    )
                }
                _state.update { it.copy(note = it.note.copy(date = zonedDateTime)) }
            }
        }
    }

    private fun buildInitialState() = NoteCreateUiState(
        note = NoteItem(
            id = UUID.randomUUID().toString(),
            date = ZonedDateTime.now(),
            fontFamily = UiNoteFontFamily.QUICK_SAND,
            fontColor = UiNoteFontColor.WHITE,  //TODO сделать дефолтный шрифт
        ),
        isInEditMode = true,
    )

    private suspend fun saveNote() {
        insertNoteUseCase(_state.value.note.toSimpleNote())
        dialogResultCoordinator.onDialogResult(
            dialogIdentifier = dialogIdentifier,
            code = DialogResult.Ok(data = _state.value.note.id),
        )
    }

    private fun toggleEditMode() {
        _state.update { it.copy(isInEditMode = !it.isInEditMode) }
    }

    private fun enableEditMode() {
        _state.update { it.copy(isInEditMode = true) }
    }
}