package com.furianrt.noteview.internal.ui.page

import androidx.lifecycle.ViewModel
import com.furianrt.core.lastIndexOf
import com.furianrt.core.orFalse
import com.furianrt.core.updateState
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.notecontent.extensions.toLocalNoteContent
import com.furianrt.notecontent.extensions.toLocalNoteTag
import com.furianrt.notecontent.extensions.toRegular
import com.furianrt.noteview.internal.ui.entites.NoteViewScreenNote
import com.furianrt.noteview.internal.ui.extensions.addSecondTagTemplate
import com.furianrt.noteview.internal.ui.extensions.addTagTemplate
import com.furianrt.noteview.internal.ui.extensions.addTitleTemplates
import com.furianrt.noteview.internal.ui.extensions.removeSecondTagTemplate
import com.furianrt.noteview.internal.ui.extensions.removeTagTemplate
import com.furianrt.noteview.internal.ui.extensions.removeTitleTemplates
import com.furianrt.noteview.internal.ui.extensions.toNoteViewScreenNote
import com.furianrt.noteview.internal.ui.page.PageEffect.*
import com.furianrt.noteview.internal.ui.page.PageEvent.*
import com.furianrt.storage.api.entities.MediaPermissionStatus
import com.furianrt.storage.api.repositories.MediaRepository
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.storage.api.repositories.TagsRepository
import com.furianrt.uikit.extensions.launch
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = PageViewModel.Factory::class)
internal class PageViewModel @AssistedInject constructor(
    private val notesRepository: NotesRepository,
    private val tagsRepository: TagsRepository,
    private val mediaRepository: MediaRepository,
    @Assisted private val noteId: String,
) : ViewModel() {

    private val _state = MutableStateFlow<PageUiState>(PageUiState.Loading)
    val state: StateFlow<PageUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PageEffect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    private val isInEditMode: Boolean
        get() = (_state.value as? PageUiState.Success)?.isInEditMode.orFalse()

    init {
        observeNote()
    }

    fun onEvent(event: PageEvent) {
        when (event) {
            is OnEditModeStateChange -> changeEditModeState(event.isEnabled)
            is OnTagRemoveClick -> removeTag(event.tag)
            is OnTagDoneEditing -> addTag(event.tag)
            is OnTagTextEntered -> addSecondTagTemplate()
            is OnTagTextCleared -> tryToRemoveSecondTagTemplate()
            is OnSelectMediaClick -> tryRequestMediaPermissions()
            is OnMediaPermissionsSelected -> tryOpenMediaSelector()
        }
    }

    private fun removeTag(tag: UiNoteTag.Regular) {
        _state.updateState<PageUiState.Success> { it.removeTag(tag) }
        launch { tagsRepository.deleteForNote(noteId, tag.id) }
    }

    private fun addTag(tag: UiNoteTag.Template) {
        if (tag.textState.text.isNotBlank()) {
            _state.updateState<PageUiState.Success> { currentState ->
                currentState.addTag(
                    tag = tag.toRegular(isInEditMode),
                    addTemplate = isInEditMode,
                )
            }
            launch { tagsRepository.upsert(noteId, tag.toLocalNoteTag()) }
        }
    }

    private fun addSecondTagTemplate() {
        _state.updateState<PageUiState.Success> { currentState ->
            currentState.copy(tags = currentState.tags.addSecondTagTemplate())
        }
    }

    private fun tryToRemoveSecondTagTemplate() {
        val tags = (_state.value as? PageUiState.Success)?.tags ?: return
        val hasTemplateTagWithText = tags.any { tag ->
            tag is UiNoteTag.Template && tag.textState.text.isNotBlank()
        }
        if (!hasTemplateTagWithText) {
            _state.updateState<PageUiState.Success> { currentState ->
                currentState.copy(tags = currentState.tags.removeSecondTagTemplate())
            }
        }
    }

    private fun tryRequestMediaPermissions() {
        if (mediaRepository.getMediaPermissionStatus() == MediaPermissionStatus.DENIED) {
            _effect.tryEmit(RequestStoragePermissions)
        } else {
            _effect.tryEmit(OpenMediaSelector)
        }
    }

    private fun tryOpenMediaSelector() {
        if (mediaRepository.getMediaPermissionStatus() == MediaPermissionStatus.DENIED) {
            _effect.tryEmit(ShowPermissionsDeniedDialog)
        } else {
            _effect.tryEmit(OpenMediaSelector)
        }
    }

    private fun changeEditModeState(isEnabled: Boolean) {
        _state.updateState<PageUiState.Success> { currentState ->
            currentState.copy(
                content = with(currentState.content) {
                    if (isEnabled) addTitleTemplates() else removeTitleTemplates()
                },
                tags = with(currentState.tags) {
                    if (isEnabled) addTagTemplate() else removeTagTemplate(onlyEmpty = true)
                },
                isInEditMode = isEnabled,
            )
        }
        // TODO написать логику сохранения заметки
        val content = (_state.value as? PageUiState.Success)?.content
        if (!isEnabled && content != null) {
            saveNoteContent(content)
        }
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

        _state.update { localState ->
            when (localState) {
                is PageUiState.Empty, PageUiState.Loading -> PageUiState.Success(
                    content = note.content,
                    tags = note.tags,
                    isInEditMode = false,
                )

                is PageUiState.Success -> localState.copy(
                    content = if (isInEditMode) localState.content else note.content,
                    tags = if (isInEditMode) localState.tags else note.tags,
                )
            }
        }
    }

    private fun saveNoteContent(content: List<UiNoteContent>) = launch {
        notesRepository.updateNoteContent(
            noteId = noteId,
            content = content.map(UiNoteContent::toLocalNoteContent),
        )
    }

    private fun PageUiState.Success.addTag(
        tag: UiNoteTag.Regular,
        addTemplate: Boolean,
    ): PageUiState.Success {
        val result = tags.toPersistentList()
            .add(index = tags.lastIndexOf { it is UiNoteTag.Regular } + 1, element = tag)
            .removeTagTemplate(tag.id)
        return copy(
            tags = if (addTemplate) {
                result.addTagTemplate()
            } else {
                result
            },
        )
    }

    private fun PageUiState.Success.removeTag(tag: UiNoteTag) = copy(
        tags = tags.toPersistentList().remove(tag)
    )

    @AssistedFactory
    interface Factory {
        fun create(noteId: String): PageViewModel
    }
}
