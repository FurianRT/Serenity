package com.furianrt.notepage.internal.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.delete
import androidx.lifecycle.ViewModel
import com.furianrt.core.hasItem
import com.furianrt.core.lastIndexOf
import com.furianrt.core.orFalse
import com.furianrt.core.updateState
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteContent.MediaBlock
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.notelistui.extensions.toLocalNoteContent
import com.furianrt.notelistui.extensions.toLocalNoteTag
import com.furianrt.notelistui.extensions.toNoteFontColor
import com.furianrt.notelistui.extensions.toNoteFontFamily
import com.furianrt.notelistui.extensions.toRegular
import com.furianrt.notepage.internal.domian.UpdateNoteContentUseCase
import com.furianrt.notepage.internal.ui.PageEffect.OpenMediaSelector
import com.furianrt.notepage.internal.ui.PageEffect.RequestStoragePermissions
import com.furianrt.notepage.internal.ui.PageEffect.ShowPermissionsDeniedDialog
import com.furianrt.notepage.internal.ui.PageEvent.OnEditModeStateChange
import com.furianrt.notepage.internal.ui.PageEvent.OnFontColorSelected
import com.furianrt.notepage.internal.ui.PageEvent.OnFontFamilySelected
import com.furianrt.notepage.internal.ui.PageEvent.OnMediaClick
import com.furianrt.notepage.internal.ui.PageEvent.OnMediaPermissionsSelected
import com.furianrt.notepage.internal.ui.PageEvent.OnMediaRemoveClick
import com.furianrt.notepage.internal.ui.PageEvent.OnMediaSelected
import com.furianrt.notepage.internal.ui.PageEvent.OnMediaShareClick
import com.furianrt.notepage.internal.ui.PageEvent.OnOnSaveContentRequest
import com.furianrt.notepage.internal.ui.PageEvent.OnOpenMediaViewerRequest
import com.furianrt.notepage.internal.ui.PageEvent.OnSelectMediaClick
import com.furianrt.notepage.internal.ui.PageEvent.OnTagDoneEditing
import com.furianrt.notepage.internal.ui.PageEvent.OnTagRemoveClick
import com.furianrt.notepage.internal.ui.PageEvent.OnTagTextCleared
import com.furianrt.notepage.internal.ui.PageEvent.OnTagTextEntered
import com.furianrt.notepage.internal.ui.PageEvent.OnTitleFocusChange
import com.furianrt.notepage.internal.ui.PageEvent.OnTitleTextChange
import com.furianrt.notepage.internal.ui.entities.NoteItem
import com.furianrt.notepage.internal.ui.extensions.addSecondTagTemplate
import com.furianrt.notepage.internal.ui.extensions.addTagTemplate
import com.furianrt.notepage.internal.ui.extensions.addTitleTemplates
import com.furianrt.notepage.internal.ui.extensions.removeMedia
import com.furianrt.notepage.internal.ui.extensions.removeSecondTagTemplate
import com.furianrt.notepage.internal.ui.extensions.removeTagTemplate
import com.furianrt.notepage.internal.ui.extensions.removeTitleTemplates
import com.furianrt.notepage.internal.ui.extensions.toMediaBlock
import com.furianrt.notepage.internal.ui.extensions.toNoteItem
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.utils.DialogIdentifier
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
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
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

private const val MEDIA_VIEW_DIALOG_ID = 1
private const val TITLE_FOCUS_DELAY = 150L

@HiltViewModel(assistedFactory = PageViewModel.Factory::class)
internal class PageViewModel @AssistedInject constructor(
    private val updateNoteContentUseCase: UpdateNoteContentUseCase,
    private val notesRepository: NotesRepository,
    private val dialogResultCoordinator: DialogResultCoordinator,
    private val permissionsUtils: PermissionsUtils,
    @Assisted private val noteId: String,
    @Assisted private val isNoteCreationMode: Boolean,
) : ViewModel(), DialogResultListener {

    private val _state = MutableStateFlow<PageUiState>(PageUiState.Loading)
    val state: StateFlow<PageUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PageEffect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    private val isInEditMode: Boolean
        get() = getSuccessState()?.isInEditMode.orFalse()

    private var focusedTitleId: String? = null
    private var hasContentChanged = false
        set(value) {
            _effect.tryEmit(PageEffect.UpdateContentChangedState(value))
            field = value
        }

    private var focusFirstTitle = isNoteCreationMode

    init {
        dialogResultCoordinator.addDialogResultListener(requestId = noteId, listener = this)
        observeNote()
    }

    override fun onCleared() {
        if (isInEditMode && hasContentChanged) {
            getSuccessState()?.let { successState ->
                launch(NonCancellable) { saveNoteContent(successState) }
            }
        }
        dialogResultCoordinator.removeDialogResultListener(requestId = noteId, listener = this)
        notesRepository.deleteNoteContentFromCache(noteId)
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
            is OnMediaClick -> openMediaViewScreen(event.media.name)
            is OnMediaRemoveClick -> removeMedia(setOf(event.media.name))
            is OnMediaShareClick -> {}
            is OnOpenMediaViewerRequest -> _effect.tryEmit(PageEffect.OpenMediaViewer(event.route))
            is OnTitleTextChange -> hasContentChanged = hasContentChanged || isInEditMode
            is OnOnSaveContentRequest -> {
                val successState = getSuccessState() ?: return
                launch(NonCancellable) { saveNoteContent(successState) }
            }

            is OnMediaSelected -> handleMediaSelectorResult(event.result)
            is OnFontFamilySelected -> updateFontFamily(event.family)
            is OnFontColorSelected -> updateFontColor(event.color)
        }
    }

    override fun onDialogResult(dialogId: Int, result: DialogResult) {
        when (dialogId) {
            MEDIA_VIEW_DIALOG_ID -> if (result is DialogResult.Ok<*>) {
                @Suppress("UNCHECKED_CAST")
                removeMedia(result.data as Set<String>)
            }
        }
    }

    private fun handleMediaSelectorResult(result: MediaResult) {
        hasContentChanged = true
        val titleIndexToFocus = findTitleIndex(focusedTitleId)?.let { it + 2 }
        _state.updateState<PageUiState.Success> { currentState ->
            val newMediaBlock = result.toMediaBlock()
            val newContent = buildContentWithNewMediaBlock(currentState.content, newMediaBlock)
            if (currentState.isInEditMode) {
                currentState.copy(content = newContent.addTitleTemplates())
            } else {
                currentState.copy(content = newContent)
            }
        }
        if (titleIndexToFocus != null) {
            launch {
                delay(TITLE_FOCUS_DELAY)
                _effect.tryEmit(PageEffect.FocusFirstTitle(titleIndexToFocus))
            }
        }
    }

    private fun findTitleIndex(id: String?): Int? {
        val successState = getSuccessState() ?: return null
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
            selection == 0 -> {
                if (focusedTitle.state.text.startsWith(' ')) {
                    focusedTitle.state.edit { delete(0, 1) }
                }
                content.toPersistentList().add(focusedTitleIndex, mediaBlock)
            }

            selection >= focusedTitle.state.text.length -> {
                val titleFirstPartText = if (focusedTitle.state.text.endsWith('\n') ||
                    focusedTitle.state.text.endsWith(' ')
                ) {
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
                        initialText = if (firstPartText.endsWith('\n') ||
                            firstPartText.endsWith(' ')
                        ) {
                            firstPartText.dropLast(1)
                        } else {
                            firstPartText
                        },
                    )
                )
                val titleSecondPart = focusedTitle.also { title ->
                    title.state.edit {
                        val text = title.state.text
                        if (text.substring(selection, text.length).isBlank()) {
                            delete(0, text.length)
                        } else {
                            delete(start = 0, end = selection)
                        }
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
        val tags = getSuccessState()?.tags ?: return
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
        if (permissionsUtils.mediaAccessDenied()) {
            _effect.tryEmit(RequestStoragePermissions)
        } else {
            _effect.tryEmit(OpenMediaSelector)
        }
    }

    private fun tryOpenMediaSelector() {
        if (permissionsUtils.mediaAccessDenied()) {
            _effect.tryEmit(ShowPermissionsDeniedDialog)
        } else {
            _effect.tryEmit(OpenMediaSelector)
        }
    }

    private fun openMediaViewScreen(mediaName: String) {
        val successState = getSuccessState() ?: return
        notesRepository.cacheNoteContent(
            noteId = noteId,
            content = successState.content.map(UiNoteContent::toLocalNoteContent)
        )
        _effect.tryEmit(
            PageEffect.OpenMediaViewScreen(
                noteId = noteId,
                mediaName = mediaName,
                identifier = DialogIdentifier(
                    dialogId = MEDIA_VIEW_DIALOG_ID,
                    requestId = noteId,
                ),
            ),
        )
    }

    private fun removeMedia(mediaNames: Set<String>) = launch(NonCancellable) {
        if (mediaNames.isEmpty()) {
            return@launch
        }
        _state.updateState<PageUiState.Success> { currentState ->
            var newContent = currentState.content
            mediaNames.forEach {
                newContent = newContent.removeMedia(it)
            }
            currentState.copy(content = newContent).also {
                if (isInEditMode) {
                    hasContentChanged = true
                } else {
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
            if (!isEnabled) {
                launch(NonCancellable) { saveNoteContent(newState) }
            }
            return@updateState newState
        }
        val successState = getSuccessState()
        if (focusFirstTitle && isEnabled && successState?.isContentEmpty == true) {
            launch {
                delay(TITLE_FOCUS_DELAY)
                _effect.tryEmit(PageEffect.FocusFirstTitle(index = 0))
                focusFirstTitle = false
            }
        }
    }

    private fun observeNote() {
        if (isNoteCreationMode) {
            handleNoteResult(NoteItem())   //TODO сделать дефолтный шрифт
        } else {
            launch {
                notesRepository.getNote(noteId)
                    .map { it?.toNoteItem() }
                    .distinctUntilChanged()
                    .collectLatest(::handleNoteResult)
            }
        }
    }

    private fun handleNoteResult(note: NoteItem?) {
        if (note == null) {
            _state.update { PageUiState.Empty }
            return
        }

        _state.update { localState ->
            when (localState) {
                is PageUiState.Empty, PageUiState.Loading -> PageUiState.Success(
                    noteId = note.id,
                    content = note.content,
                    tags = note.tags,
                    fontFamily = note.fontFamily,
                    fontColor = note.fontColor,
                    isInEditMode = false,
                )

                is PageUiState.Success -> localState
            }
        }
    }

    private suspend fun saveNoteContent(state: PageUiState.Success) {
        updateNoteContentUseCase(
            noteId = noteId,
            content = state.content.map(UiNoteContent::toLocalNoteContent),
            tags = state.tags.map(UiNoteTag::toLocalNoteTag).filter { it.title.isNotBlank() },
            fontFamily = state.fontFamily.toNoteFontFamily(),
            fontColor = state.fontColor.toNoteFontColor(),
        )
        hasContentChanged = false
    }

    private fun updateFontFamily(family: UiNoteFontFamily) {
        hasContentChanged = true
        _state.updateState<PageUiState.Success> { it.copy(fontFamily = family) }
    }

    private fun updateFontColor(color: UiNoteFontColor) {
        hasContentChanged = true
        _state.updateState<PageUiState.Success> { it.copy(fontColor = color) }
    }

    private fun PageUiState.Success.addTag(
        tag: UiNoteTag.Regular,
        addTemplate: Boolean,
    ): PageUiState.Success {
        val result = if (tags.hasItem { it.id == tag.id }) {
            tags.removeTagTemplate(tag.id)
        } else {
            tags.toPersistentList()
                .add(index = tags.lastIndexOf { it is UiNoteTag.Regular } + 1, element = tag)
                .removeTagTemplate(tag.id)
        }
        return copy(tags = if (addTemplate) result.addTagTemplate() else result)
    }

    private fun PageUiState.Success.removeTag(tag: UiNoteTag) = copy(
        tags = tags.toPersistentList().remove(tag),
    )

    private fun getSuccessState() = _state.value as? PageUiState.Success

    @AssistedFactory
    interface Factory {
        fun create(
            noteId: String?,
            isNoteCreationMode: Boolean,
        ): PageViewModel
    }
}
