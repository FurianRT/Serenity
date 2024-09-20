package com.furianrt.noteview.internal.ui.page

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.delete
import androidx.lifecycle.ViewModel
import com.furianrt.core.lastIndexOf
import com.furianrt.core.orFalse
import com.furianrt.core.updateState
import com.furianrt.mediaselector.api.entities.MediaSelectorResult
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteContent.MediaBlock
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.notecontent.extensions.toLocalNoteContent
import com.furianrt.notecontent.extensions.toLocalNoteTag
import com.furianrt.notecontent.extensions.toRegular
import com.furianrt.noteview.internal.domain.UpdateNoteContentUseCase
import com.furianrt.noteview.internal.ui.entites.NoteViewScreenNote
import com.furianrt.noteview.internal.ui.extensions.addSecondTagTemplate
import com.furianrt.noteview.internal.ui.extensions.addTagTemplate
import com.furianrt.noteview.internal.ui.extensions.addTitleTemplates
import com.furianrt.noteview.internal.ui.extensions.removeMedia
import com.furianrt.noteview.internal.ui.extensions.removeSecondTagTemplate
import com.furianrt.noteview.internal.ui.extensions.removeTagTemplate
import com.furianrt.noteview.internal.ui.extensions.removeTitleTemplates
import com.furianrt.noteview.internal.ui.extensions.toMediaBlock
import com.furianrt.noteview.internal.ui.extensions.toNoteViewScreenNote
import com.furianrt.noteview.internal.ui.page.PageEffect.OpenMediaSelector
import com.furianrt.noteview.internal.ui.page.PageEffect.RequestStoragePermissions
import com.furianrt.noteview.internal.ui.page.PageEffect.ShowPermissionsDeniedDialog
import com.furianrt.noteview.internal.ui.page.PageEvent.*
import com.furianrt.storage.api.entities.MediaPermissionStatus
import com.furianrt.storage.api.repositories.MediaRepository
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.utils.DialogResult
import com.furianrt.uikit.utils.DialogResultCoordinator
import com.furianrt.uikit.utils.DialogResultListener
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID

private const val MEDIA_SELECTOR_DIALOG_ID = 1

@HiltViewModel(assistedFactory = PageViewModel.Factory::class)
internal class PageViewModel @AssistedInject constructor(
    private val updateNoteContentUseCase: UpdateNoteContentUseCase,
    private val notesRepository: NotesRepository,
    private val mediaRepository: MediaRepository,
    private val dialogResultCoordinator: DialogResultCoordinator,
    @Assisted private val noteId: String,
) : ViewModel(), DialogResultListener {

    private val _state = MutableStateFlow<PageUiState>(PageUiState.Loading)
    val state: StateFlow<PageUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PageEffect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    private val isInEditMode: Boolean
        get() = (_state.value as? PageUiState.Success)?.isInEditMode.orFalse()

    private var focusedTitleId: String? = null
    private var hasContentChanged = false
        set(value) {
            _effect.tryEmit(PageEffect.UpdateContentChangedState(value))
            field = value
        }

    init {
        dialogResultCoordinator.addDialogResultListener(requestId = noteId, listener = this)
        observeNote()
    }

    override fun onCleared() {
        dialogResultCoordinator.removeDialogResultListener(requestId = noteId, listener = this)
        super.onCleared()
    }

    fun onEvent(event: PageEvent) {
        when (event) {
            is OnEditModeStateChange -> changeEditModeState(event.isEnabled)
            is OnTagRemoveClick -> removeTag(event.tag)
            is OnTagDoneEditing -> addTag(event.tag)
            is OnTagTextCleared -> tryToRemoveSecondTagTemplate()
            is OnTagTextEntered -> {
                hasContentChanged = true
                addSecondTagTemplate()
            }

            is OnSelectMediaClick -> tryRequestMediaPermissions()
            is OnMediaPermissionsSelected -> tryOpenMediaSelector()
            is OnTitleFocusChange -> focusedTitleId = event.id
            is OnMediaClick -> {}
            is OnMediaRemoveClick -> removeMedia(event.media)
            is OnMediaShareClick -> {}
            is OnTitleTextChange -> hasContentChanged = hasContentChanged || isInEditMode
            is OnOnSaveContentRequest -> {
                val successState = _state.value as? PageUiState.Success ?: return
                launch { saveNoteContent(successState) }
            }
        }
    }

    override fun onDialogResult(dialogId: Int, result: DialogResult) {
        when (dialogId) {
            MEDIA_SELECTOR_DIALOG_ID -> if (result is DialogResult.Ok<*>) {
                handleMediaSelectorResult(result.data as MediaSelectorResult)
            }
        }
    }

    private fun handleMediaSelectorResult(result: MediaSelectorResult) {
        hasContentChanged = true
        _state.updateState<PageUiState.Success> { currentState ->
            val newMediaBlock = result.toMediaBlock()
            val newContent = buildContentWithNewMediaBlock(currentState.content, newMediaBlock)
            if (currentState.isInEditMode) {
                currentState.copy(content = newContent.addTitleTemplates())
            } else {
                currentState.copy(content = newContent)
            }
        }
    }

    private fun findTitleIndex(id: String?): Int? {
        val successState = _state.value as? PageUiState.Success ?: return null
        val index = successState.content.indexOfFirst { it.id == id }
        return index.takeIf { it != -1 }
    }

    private fun buildContentWithNewMediaBlock(
        content: List<UiNoteContent>,
        mediaBlock: MediaBlock,
    ): ImmutableList<UiNoteContent> {
        val focusedTitleIndex = findTitleIndex(focusedTitleId)
            ?: return (content + mediaBlock).toImmutableList()
        val focusedTitle = content[focusedTitleIndex] as UiNoteContent.Title
        val selection = focusedTitle.state.selection.start
        return when {
            selection == 0 -> content.toPersistentList().add(focusedTitleIndex, mediaBlock)

            selection >= focusedTitle.state.text.length -> {
                val titleFirstPartText = if (focusedTitle.state.text.endsWith('\n')) {
                    focusedTitle.state.text.dropLast(1)
                } else {
                    focusedTitle.state.text
                }
                val titleFirstPart = UiNoteContent.Title(
                    id = UUID.randomUUID().toString(),
                    state = TextFieldState(initialText = titleFirstPartText.toString())
                )
                val titleSecondPart = focusedTitle.also { it.state.clearText() }
                val result = content.toMutableList()
                result[focusedTitleIndex] = titleFirstPart
                result.add(focusedTitleIndex + 1, mediaBlock)
                result.add(focusedTitleIndex + 2, titleSecondPart)
                result.toImmutableList()
            }

            else -> {
                val firstPartText = focusedTitle.state.text.substring(
                    startIndex = 0,
                    endIndex = selection,
                )
                val titleFirstPart = UiNoteContent.Title(
                    id = UUID.randomUUID().toString(),
                    state = TextFieldState(
                        initialText = if (firstPartText.endsWith('\n')) {
                            firstPartText.dropLast(1)
                        } else {
                            firstPartText
                        },
                    )
                )
                val titleSecondPart = focusedTitle.also { title ->
                    title.state.edit {
                        delete(start = 0, end = selection)
                        placeCursorBeforeCharAt(0)
                    }
                }
                val result = content.toMutableList()
                result[focusedTitleIndex] = titleFirstPart
                result.add(focusedTitleIndex + 1, mediaBlock)
                result.add(focusedTitleIndex + 2, titleSecondPart)
                result.toImmutableList()
            }
        }
    }

    private fun removeTag(tag: UiNoteTag.Regular) {
        hasContentChanged = true
        _state.updateState<PageUiState.Success> { it.removeTag(tag) }
    }

    private fun addTag(tag: UiNoteTag.Template) {
        if (tag.textState.text.isNotBlank()) {
            hasContentChanged = true
            _state.updateState<PageUiState.Success> { currentState ->
                currentState.addTag(
                    tag = tag.toRegular(isInEditMode),
                    addTemplate = isInEditMode,
                )
            }
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
            _effect.tryEmit(
                OpenMediaSelector(
                    dialogId = MEDIA_SELECTOR_DIALOG_ID,
                    requestId = noteId,
                )
            )
        }
    }

    private fun tryOpenMediaSelector() {
        if (mediaRepository.getMediaPermissionStatus() == MediaPermissionStatus.DENIED) {
            _effect.tryEmit(ShowPermissionsDeniedDialog)
        } else {
            _effect.tryEmit(
                OpenMediaSelector(
                    dialogId = MEDIA_SELECTOR_DIALOG_ID,
                    requestId = noteId,
                )
            )
        }
    }

    private fun removeMedia(media: MediaBlock.Media) = launch {
        hasContentChanged = true
        _state.updateState<PageUiState.Success> { currentState ->
            currentState.copy(currentState.content.removeMedia(media.id)).also {
                if (!isInEditMode) {
                    saveNoteContent(it)
                }
            }
        }
    }

    private fun changeEditModeState(isEnabled: Boolean) {
        if (!isEnabled) {
            focusedTitleId = null
            hasContentChanged = false
        }
        _state.updateState<PageUiState.Success> { currentState ->
            val newState = currentState.copy(
                content = with(currentState.content) {
                    if (isEnabled) addTitleTemplates() else removeTitleTemplates()
                },
                tags = with(currentState.tags) {
                    if (isEnabled) addTagTemplate() else removeTagTemplate(onlyEmpty = true)
                },
                isInEditMode = isEnabled,
            )
            // TODO написать логику сохранения заметки
            if (!isEnabled) {
                launch {
                    saveNoteContent(newState)
                }
            }
            newState
        }
    }

    private fun observeNote() = launch {
        notesRepository.getNote(noteId)
            .map { it?.toNoteViewScreenNote() }
            .distinctUntilChanged()
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

    private suspend fun saveNoteContent(state: PageUiState.Success) {
        updateNoteContentUseCase(
            noteId = noteId,
            content = state.content.map(UiNoteContent::toLocalNoteContent),
            tags = state.tags.map(UiNoteTag::toLocalNoteTag).filter { it.title.isNotBlank() },
        )
    }

    private fun PageUiState.Success.addTag(
        tag: UiNoteTag.Regular,
        addTemplate: Boolean,
    ): PageUiState.Success {
        val result = tags.toPersistentList()
            .add(index = tags.lastIndexOf { it is UiNoteTag.Regular } + 1, element = tag)
            .removeTagTemplate(tag.id)
        return copy(tags = if (addTemplate) result.addTagTemplate() else result)
    }

    private fun PageUiState.Success.removeTag(tag: UiNoteTag) = copy(
        tags = tags.toPersistentList().remove(tag),
    )

    private fun getSuccessState() = _state.value as? PageUiState.Success

    @AssistedFactory
    interface Factory {
        fun create(noteId: String): PageViewModel
    }
}
