package com.furianrt.notecreate.internal.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.doWithState
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.notecreate.internal.ui.extensions.toNoteItem
import com.furianrt.toolspanel.api.NoteBackgroundProvider
import com.furianrt.uikit.extensions.getOrPut
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.DialogResult
import com.furianrt.uikit.utils.DialogResultCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

private const val KEY_NOTE_ID = "note_id"
private const val KEY_REQUEST_ID = "requestId"
private const val KEY_DIALOG_ID = "dialogId"

@HiltViewModel
internal class NoteCreateViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository,
    private val backgroundProvider: NoteBackgroundProvider,
    private val dialogResultCoordinator: DialogResultCoordinator,
) : ViewModel() {

    private val isInEditModeState = MutableStateFlow(true)

    val state = combine(
        isInEditModeState,
        notesRepository.getOrCreateTemplateNote(
            savedStateHandle.getOrPut(KEY_NOTE_ID, UUID.randomUUID().toString()),
        ),
    ) { isInEditMode, note ->
        NoteCreateUiState.Success(
            note = note.toNoteItem(
                background = backgroundProvider.getBackground(note.backgroundId),
            ),
            isInEditMode = isInEditMode,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NoteCreateUiState.Loading,
    )

    private val _effect = MutableSharedFlow<NoteCreateEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    private val dialogIdentifier by lazy(LazyThreadSafetyMode.NONE) {
        DialogIdentifier(
            requestId = savedStateHandle[KEY_REQUEST_ID]!!,
            dialogId = savedStateHandle[KEY_DIALOG_ID]!!,
        )
    }

    private var isContentChanged = false

    @OptIn(DelicateCoroutinesApi::class)
    fun onEvent(event: NoteCreateEvent) {
        when (event) {
            is NoteCreateEvent.OnPageTitleFocused -> enableEditMode()
            is NoteCreateEvent.OnButtonEditClick -> {
                if (isInEditModeState.value && isContentChanged) {
                    GlobalScope.launch { saveTemplate() }
                }
                toggleEditMode()
            }

            is NoteCreateEvent.OnButtonBackClick -> _effect.tryEmit(NoteCreateEffect.CloseScreen)
            is NoteCreateEvent.OnContentChanged -> {
                if (!isContentChanged && event.isChanged) {
                    isContentChanged = true
                    GlobalScope.launch { saveTemplate() }
                }
            }

            is NoteCreateEvent.OnButtonDateClick -> launch { showDateSelector() }
            is NoteCreateEvent.OnDateSelected -> {
                state.doWithState<NoteCreateUiState.Success> { successState ->
                    launch {
                        notesRepository.updateNoteDate(
                            noteId = successState.note.id,
                            date = ZonedDateTime.of(
                                event.date,
                                LocalTime.now(),
                                ZoneId.systemDefault()
                            ),
                        )
                    }
                }
            }

            is NoteCreateEvent.OnButtonDeleteClick -> if (isContentChanged) {
                _effect.tryEmit(NoteCreateEffect.ShowDeleteConfirmationDialog)
            } else {
                launch { deleteNote() }
            }

            is NoteCreateEvent.OnConfirmDeleteClick -> launch { deleteNote() }
            is NoteCreateEvent.OnPinClick -> launch { toggleNotePinnedState() }
        }
    }

    private suspend fun showDateSelector() {
        state.doWithState<NoteCreateUiState.Success> { successState ->
            _effect.tryEmit(
                NoteCreateEffect.ShowDateSelector(
                    date = successState.note.date.toLocalDate(),
                    datesWithNotes = notesRepository.getUniqueNotesDates().first(),
                ),
            )
        }
    }

    private suspend fun saveTemplate() {
        state.doWithState<NoteCreateUiState.Success> { successState ->
            notesRepository.setTemplate(successState.note.id, isTemplate = false)
            dialogResultCoordinator.onDialogResult(
                dialogIdentifier = dialogIdentifier,
                code = DialogResult.Ok(data = successState.note.id),
            )
        }
    }

    private fun toggleEditMode() {
        isInEditModeState.update { !it }
    }

    private fun enableEditMode() {
        isInEditModeState.update { true }
    }

    private suspend fun toggleNotePinnedState() {
        state.doWithState<NoteCreateUiState.Success> { successState ->
            val note = successState.note
            notesRepository.updateNoteIsPinned(note.id, !note.isPinned)
        }
    }

    private suspend fun deleteNote() {
        state.doWithState<NoteCreateUiState.Success> { successState ->
            notesRepository.setTemplate(successState.note.id, isTemplate = true)
            if (isContentChanged) {
                notesRepository.enqueueOneTimeCleanup()
            }
            _effect.tryEmit(NoteCreateEffect.CloseScreen)
        }
    }
}