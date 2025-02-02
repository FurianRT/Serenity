package com.furianrt.notepage.internal.ui.page

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.lifecycle.ViewModel
import com.furianrt.core.doWithState
import com.furianrt.core.getState
import com.furianrt.core.hasItem
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.core.lastIndexOf
import com.furianrt.core.orFalse
import com.furianrt.core.updateState
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.domain.voice.AudioPlayer
import com.furianrt.domain.voice.AudioPlayerListener
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.notelistui.extensions.toLocalNoteContent
import com.furianrt.notelistui.extensions.toLocalNoteTag
import com.furianrt.notelistui.extensions.toNoteFontColor
import com.furianrt.notelistui.extensions.toNoteFontFamily
import com.furianrt.notelistui.extensions.toRegular
import com.furianrt.notelistui.extensions.toUiNoteFontColor
import com.furianrt.notelistui.extensions.toUiNoteFontFamily
import com.furianrt.notepage.internal.domian.UpdateNoteContentUseCase
import com.furianrt.notepage.internal.ui.page.PageEffect.OpenMediaSelector
import com.furianrt.notepage.internal.ui.page.PageEffect.RequestStoragePermissions
import com.furianrt.notepage.internal.ui.page.PageEffect.ShowPermissionsDeniedDialog
import com.furianrt.notepage.internal.ui.page.PageEvent.OnEditModeStateChange
import com.furianrt.notepage.internal.ui.page.PageEvent.OnFontColorSelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnFontFamilySelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnFontSizeSelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnIsSelectedChange
import com.furianrt.notepage.internal.ui.page.PageEvent.OnMediaClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnMediaPermissionsSelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnMediaRemoveClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnMediaSelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnMediaShareClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnOnSaveContentRequest
import com.furianrt.notepage.internal.ui.page.PageEvent.OnOpenMediaViewerRequest
import com.furianrt.notepage.internal.ui.page.PageEvent.OnRemoveStickerClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnSelectMediaClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnStickerSelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnTagDoneEditing
import com.furianrt.notepage.internal.ui.page.PageEvent.OnTagRemoveClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnTagTextCleared
import com.furianrt.notepage.internal.ui.page.PageEvent.OnTagTextEntered
import com.furianrt.notepage.internal.ui.page.PageEvent.OnTitleFocusChange
import com.furianrt.notepage.internal.ui.page.PageEvent.OnTitleTextChange
import com.furianrt.notepage.internal.ui.page.PageEvent.OnVoicePlayClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnVoiceProgressSelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnVoiceRecorded
import com.furianrt.notepage.internal.ui.page.PageEvent.OnVoiceRemoveClick
import com.furianrt.notepage.internal.ui.page.entities.NoteItem
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import com.furianrt.notepage.internal.ui.extensions.addSecondTagTemplate
import com.furianrt.notepage.internal.ui.extensions.addTagTemplate
import com.furianrt.notepage.internal.ui.extensions.addTitleTemplates
import com.furianrt.notepage.internal.ui.extensions.removeMedia
import com.furianrt.notepage.internal.ui.extensions.removeSecondTagTemplate
import com.furianrt.notepage.internal.ui.extensions.removeTagTemplate
import com.furianrt.notepage.internal.ui.extensions.removeTitleTemplates
import com.furianrt.notepage.internal.ui.extensions.removeVoice
import com.furianrt.notepage.internal.ui.extensions.toLocalNoteSticker
import com.furianrt.notepage.internal.ui.extensions.toMediaBlock
import com.furianrt.notepage.internal.ui.extensions.toNoteItem
import com.furianrt.notepage.internal.ui.extensions.toUiVoice
import com.furianrt.notepage.internal.ui.extensions.updateVoiceProgress
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
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
    private val appearanceRepository: AppearanceRepository,
    private val audioPlayer: AudioPlayer,
    @Assisted private val noteId: String,
    @Assisted private val isNoteCreationMode: Boolean,
) : ViewModel(), DialogResultListener, AudioPlayerListener {

    private val _state = MutableStateFlow<PageUiState>(PageUiState.Loading)
    val state: StateFlow<PageUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PageEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    private val isInEditMode: Boolean
        get() = _state.getState<PageUiState.Success>()?.isInEditMode.orFalse()

    private var focusedTitleId: String? = null
    private var hasContentChanged = false
        set(value) {
            _effect.tryEmit(PageEffect.UpdateContentChangedState(value))
            field = value
        }

    private var focusFirstTitle = isNoteCreationMode

    init {
        dialogResultCoordinator.addDialogResultListener(requestId = noteId, listener = this)
        audioPlayer.setProgressListener(this)
        observeNote()
    }

    override fun onCleared() {
        if (isInEditMode && hasContentChanged) {
            _state.doWithState<PageUiState.Success> { successState ->
                launch(NonCancellable) { saveNoteContent(successState) }
            }
        }
        dialogResultCoordinator.removeDialogResultListener(requestId = noteId, listener = this)
        audioPlayer.clearProgressListener()
        notesRepository.deleteNoteContentFromCache(noteId)
    }

    fun onEvent(event: PageEvent) {
        when (event) {
            is OnEditModeStateChange -> launch { changeEditModeState(event.isEnabled) }
            is OnIsSelectedChange -> onIsSelectedChange(event.isSelected)
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
            is OnOnSaveContentRequest -> _state.doWithState<PageUiState.Success> { successState ->
                launch(NonCancellable) { saveNoteContent(successState) }
            }

            is OnMediaSelected -> addNewBlock(event.result.toMediaBlock())
            is OnVoiceRecorded -> addNewBlock(event.record.toUiVoice())
            is OnFontFamilySelected -> updateFontFamily(event.family)
            is OnFontColorSelected -> updateFontColor(event.color)
            is OnFontSizeSelected -> updateFontSize(event.size)
            is OnVoicePlayClick -> onVoicePlayClick(event.voice)
            is OnVoiceProgressSelected -> onVoiceProgressSelected(event.voice, event.value)
            is OnVoiceRemoveClick -> removeVoiceRecord(event.voice)
            is OnStickerSelected -> addSticker(event.sticker)
            is OnRemoveStickerClick -> removeSticker(event.sticker)
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

    override fun onAudioProgressChange(progress: Float) {
        _state.updateState<PageUiState.Success> { currentState ->
            if (currentState.playingVoiceId != null) {
                currentState.copy(
                    content = currentState.content.updateVoiceProgress(
                        id = currentState.playingVoiceId,
                        progress = progress,
                    ),
                )
            } else {
                currentState
            }
        }
    }

    override fun onAudioPlayComplete() {
        audioPlayer.stop()
        _state.updateState<PageUiState.Success> { currentState ->
            currentState.copy(
                content = if (currentState.playingVoiceId != null) {
                    currentState.content.updateVoiceProgress(
                        id = currentState.playingVoiceId,
                        progress = 0f,
                    )
                } else {
                    currentState.content
                },
                playingVoiceId = null,
            )
        }
    }

    private fun addNewBlock(newBlock: UiNoteContent) {
        hasContentChanged = true
        val titleIndexToFocus = findTitleIndex(focusedTitleId)?.let { it + 2 }
        _state.updateState<PageUiState.Success> { currentState ->
            val newContent = buildContentWithNewBlock(currentState.content, newBlock)
            if (currentState.isInEditMode) {
                currentState.copy(content = newContent.addTitleTemplates())
            } else {
                currentState.copy(content = newContent)
            }
        }
        launch {
            delay(TITLE_FOCUS_DELAY)
            if (titleIndexToFocus != null) {
                _effect.tryEmit(PageEffect.FocusFirstTitle(titleIndexToFocus))
            }
            _state.doWithState<PageUiState.Success> { successState ->
                val blockIndex = successState.content.indexOfFirstOrNull { it.id == newBlock.id }
                if (blockIndex != null) {
                    _effect.tryEmit(PageEffect.BringContentToView(blockIndex))
                }
            }
        }
    }

    private fun findTitleIndex(id: String?): Int? {
        val successState = _state.getState<PageUiState.Success>() ?: return null
        val index = successState.content.indexOfFirst { it.id == id }
        return index.takeIf { it != -1 }
    }

    private fun buildContentWithNewBlock(
        content: List<UiNoteContent>,
        newBlock: UiNoteContent,
    ): ImmutableList<UiNoteContent> {
        val focusedTitleIndex = findTitleIndex(focusedTitleId)
            ?: return (content + newBlock).toImmutableList()
        val focusedTitle = content[focusedTitleIndex] as UiNoteContent.Title
        val selection = focusedTitle.state.selection.start
        return when {
            selection == 0 -> {
                if (focusedTitle.state.annotatedString.startsWith(' ')) {
                    focusedTitle.state.annotatedString = focusedTitle.state.annotatedString
                        .subSequence(1, focusedTitle.state.annotatedString.length)
                }
                content.toPersistentList().add(focusedTitleIndex, newBlock)
            }

            selection >= focusedTitle.state.annotatedString.length -> {
                val titleFirstPartText = if (focusedTitle.state.annotatedString.endsWith('\n') ||
                    focusedTitle.state.annotatedString.endsWith(' ')
                ) {
                    focusedTitle.state.annotatedString.subSequence(
                        startIndex = 0,
                        endIndex = focusedTitle.state.annotatedString.lastIndex,
                    )
                } else {
                    focusedTitle.state.annotatedString
                }
                val titleFirstPart = UiNoteContent.Title(
                    id = UUID.randomUUID().toString(),
                    state = NoteTitleState(initialText = titleFirstPartText)
                )

                val titleSecondPart = focusedTitle.also { title ->
                    title.state.annotatedString = AnnotatedString("")
                }
                val result = content.toMutableList()
                result[focusedTitleIndex] = titleFirstPart
                result.add(focusedTitleIndex + 1, newBlock)
                result.add(focusedTitleIndex + 2, titleSecondPart)
                result.toImmutableList()
            }

            else -> {
                val firstPartText = focusedTitle.state.annotatedString.subSequence(
                    startIndex = 0,
                    endIndex = selection,
                )
                val titleFirstPart = UiNoteContent.Title(
                    id = UUID.randomUUID().toString(),
                    state = NoteTitleState(
                        initialText = if (firstPartText.endsWith('\n') ||
                            firstPartText.endsWith(' ')
                        ) {
                            firstPartText.subSequence(0, firstPartText.text.lastIndex)
                        } else {
                            firstPartText
                        },
                    )
                )
                val titleSecondPart = focusedTitle.also { title ->
                    val text = title.state.annotatedString
                    val tempPart = text.subSequence(selection, text.length)
                    title.state.annotatedString = if (tempPart.isBlank()) {
                        AnnotatedString("")
                    } else {
                        title.state.annotatedString.subSequence(
                            startIndex = selection + tempPart.indexOfFirst { it != ' ' },
                            endIndex = title.state.annotatedString.length,
                        )
                    }
                    title.state.selection = TextRange(0)
                }
                val result = content.toMutableList()
                result[focusedTitleIndex] = titleFirstPart
                result.add(focusedTitleIndex + 1, newBlock)
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
        val tags = _state.getState<PageUiState.Success>()?.tags ?: return
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
        _state.doWithState<PageUiState.Success> { successState ->
            notesRepository.cacheNoteContent(
                noteId = noteId,
                content = successState.content.map(UiNoteContent::toLocalNoteContent),
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
    }

    private fun removeMedia(mediaNames: Set<String>) {
        if (mediaNames.isEmpty()) {
            return
        }
        launch(NonCancellable) {
            _state.updateState<PageUiState.Success> { currentState ->
                var newContent = currentState.content
                mediaNames.forEach { newContent = newContent.removeMedia(it, focusedTitleId) }
                currentState.copy(content = newContent).also {
                    if (isInEditMode) {
                        hasContentChanged = true
                    } else {
                        saveNoteContent(it)
                    }
                }
            }
        }
    }

    private fun removeVoiceRecord(voice: UiNoteContent.Voice) {
        launch(NonCancellable) {
            _state.updateState<PageUiState.Success> { currentState ->
                val playingVoiceId = if (currentState.playingVoiceId == voice.id) {
                    audioPlayer.stop()
                    null
                } else {
                    currentState.playingVoiceId
                }
                currentState.copy(
                    content = currentState.content.removeVoice(voice.id, focusedTitleId),
                    playingVoiceId = playingVoiceId,
                ).also {
                    if (isInEditMode) {
                        hasContentChanged = true
                    } else {
                        saveNoteContent(it)
                    }
                }
            }
        }
    }

    private fun onVoicePlayClick(voice: UiNoteContent.Voice) {
        _state.updateState<PageUiState.Success> { currentState ->
            when (currentState.playingVoiceId) {
                voice.id -> {
                    audioPlayer.stop()
                    currentState.copy(playingVoiceId = null)
                }

                null -> {
                    audioPlayer.play(voice.uri, voice.progress)
                    currentState.copy(playingVoiceId = voice.id)
                }

                else -> {
                    audioPlayer.stop()
                    audioPlayer.play(voice.uri, voice.progress)
                    currentState.copy(playingVoiceId = voice.id)
                }
            }
        }
    }

    private fun onVoiceProgressSelected(voice: UiNoteContent.Voice, progress: Float) {
        _state.updateState<PageUiState.Success> { currentState ->
            if (voice.id == currentState.playingVoiceId) {
                audioPlayer.setProgress(progress)
            }
            currentState.copy(
                content = currentState.content.updateVoiceProgress(voice.id, progress),
            )
        }
    }

    private fun addSticker(sticker: StickerItem) {
        _state.updateState<PageUiState.Success> { currentState ->
            currentState.copy(stickers = currentState.stickers.toPersistentList().add(sticker))
        }
    }

    private fun removeSticker(sticker: StickerItem) {
        _state.updateState<PageUiState.Success> { currentState ->
            currentState.copy(
                stickers = currentState.stickers.toPersistentList()
                    .removeAll { it.id == sticker.id },
            )
        }
    }

    private fun onIsSelectedChange(isSelected: Boolean) {
        if (!isSelected) {
            audioPlayer.stop()
            _state.updateState<PageUiState.Success> { currentState ->
                currentState.copy(playingVoiceId = null)
            }
        }
    }

    private suspend fun changeEditModeState(isEnabled: Boolean) {
        ensureSuccessState()
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
        _state.doWithState<PageUiState.Success> { successState ->
            if (focusFirstTitle && isEnabled && successState.isContentEmpty) {
                delay(TITLE_FOCUS_DELAY)
                _effect.tryEmit(PageEffect.FocusFirstTitle(index = 0))
                focusFirstTitle = false
            }
        }
    }

    private suspend fun ensureSuccessState() {
        state.first { it is PageUiState.Success }
    }

    private fun observeNote() {
        launch {
            if (isNoteCreationMode) {
                val family = appearanceRepository.getDefaultNoteFont().firstOrNull()
                val color = appearanceRepository.getDefaultNoteFontColor().firstOrNull()
                val size = appearanceRepository.getDefaultNoteFontSize().firstOrNull()
                handleNoteResult(
                    NoteItem(
                        fontFamily = family?.toUiNoteFontFamily() ?: UiNoteFontFamily.QUICK_SAND,
                        fontColor = color?.toUiNoteFontColor() ?: UiNoteFontColor.WHITE,
                        fontSize = size ?: 15,
                    )
                )
            } else {
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
                    stickers = note.stickers,
                    playingVoiceId = null,
                    fontFamily = note.fontFamily,
                    fontColor = note.fontColor,
                    fontSize = note.fontSize,
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
            stickers = state.stickers.map(StickerItem::toLocalNoteSticker),
            fontFamily = state.fontFamily.toNoteFontFamily(),
            fontColor = state.fontColor.toNoteFontColor(),
            fontSize = state.fontSize,
        )
        if (isNoteCreationMode) {
            saveDefaultFontData(state)
        }
        hasContentChanged = false
    }

    private suspend fun saveDefaultFontData(state: PageUiState.Success) {
        appearanceRepository.setDefaultNoteFont(state.fontFamily.toNoteFontFamily())
        appearanceRepository.setDefaultNoteFontColor(state.fontColor.toNoteFontColor())
        appearanceRepository.setDefaultNoteFontSize(state.fontSize)
    }

    private fun updateFontFamily(family: UiNoteFontFamily) {
        hasContentChanged = true
        _state.updateState<PageUiState.Success> { it.copy(fontFamily = family) }
    }

    private fun updateFontColor(color: UiNoteFontColor) {
        hasContentChanged = true
        _state.updateState<PageUiState.Success> { it.copy(fontColor = color) }
    }

    private fun updateFontSize(size: Int) {
        hasContentChanged = true
        _state.updateState<PageUiState.Success> { it.copy(fontSize = size) }
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

    @AssistedFactory
    interface Factory {
        fun create(
            noteId: String?,
            isNoteCreationMode: Boolean,
        ): PageViewModel
    }
}
