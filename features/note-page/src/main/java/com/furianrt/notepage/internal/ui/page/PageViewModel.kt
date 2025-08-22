package com.furianrt.notepage.internal.ui.page

import android.net.Uri
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.lifecycle.ViewModel
import com.furianrt.core.DispatchersProvider
import com.furianrt.core.doWithState
import com.furianrt.core.getState
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.core.mapImmutable
import com.furianrt.core.orFalse
import com.furianrt.core.updateState
import com.furianrt.domain.entities.MediaSortingResult
import com.furianrt.domain.managers.LockAuthorizer
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.domain.managers.SyncManager
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.domain.repositories.StickersRepository
import com.furianrt.domain.usecase.UpdateNoteContentUseCase
import com.furianrt.domain.voice.AudioPlayer
import com.furianrt.domain.voice.AudioPlayerListener
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.notelistui.extensions.toLocalNoteContent
import com.furianrt.notelistui.extensions.toLocalNoteTag
import com.furianrt.notelistui.extensions.toNoteFontColor
import com.furianrt.notelistui.extensions.toNoteFontFamily
import com.furianrt.notelistui.extensions.toRegular
import com.furianrt.notelistui.extensions.toUiNoteFontFamily
import com.furianrt.notelistui.extensions.toUiNoteMedia
import com.furianrt.notepage.R
import com.furianrt.notepage.internal.ui.extensions.addSecondTagTemplate
import com.furianrt.notepage.internal.ui.extensions.addTagTemplate
import com.furianrt.notepage.internal.ui.extensions.refreshTitleTemplates
import com.furianrt.notepage.internal.ui.extensions.removeMedia
import com.furianrt.notepage.internal.ui.extensions.removeMediaBlock
import com.furianrt.notepage.internal.ui.extensions.removeSecondTagTemplate
import com.furianrt.notepage.internal.ui.extensions.removeTagTemplate
import com.furianrt.notepage.internal.ui.extensions.removeVoice
import com.furianrt.notepage.internal.ui.extensions.toLocalNoteSticker
import com.furianrt.notepage.internal.ui.extensions.toMediaBlock
import com.furianrt.notepage.internal.ui.extensions.toNoteItem
import com.furianrt.notepage.internal.ui.extensions.toUiVoice
import com.furianrt.notepage.internal.ui.page.PageEffect.OpenMediaSelector
import com.furianrt.notepage.internal.ui.page.PageEffect.RequestCameraPermission
import com.furianrt.notepage.internal.ui.page.PageEffect.RequestStoragePermissions
import com.furianrt.notepage.internal.ui.page.PageEffect.ShowCameraPermissionsDeniedDialog
import com.furianrt.notepage.internal.ui.page.PageEffect.ShowStoragePermissionsDeniedDialog
import com.furianrt.notepage.internal.ui.page.PageEffect.TakePicture
import com.furianrt.notepage.internal.ui.page.PageEvent.OnBackgroundSelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnBackgroundsClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnCameraNotFoundError
import com.furianrt.notepage.internal.ui.page.PageEvent.OnCameraPermissionSelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnClickOutside
import com.furianrt.notepage.internal.ui.page.PageEvent.OnEditModeStateChange
import com.furianrt.notepage.internal.ui.page.PageEvent.OnFocusedTitleSelectionChange
import com.furianrt.notepage.internal.ui.page.PageEvent.OnFontColorSelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnFontFamilySelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnFontSizeSelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnIsSelectedChange
import com.furianrt.notepage.internal.ui.page.PageEvent.OnMediaClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnMediaPermissionsSelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnMediaRemoveClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnMediaSelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnMediaSortingClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnMoodClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnMoodSelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnNoPositionError
import com.furianrt.notepage.internal.ui.page.PageEvent.OnOpenMediaViewerRequest
import com.furianrt.notepage.internal.ui.page.PageEvent.OnRemoveStickerClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnScreenStopped
import com.furianrt.notepage.internal.ui.page.PageEvent.OnSelectBulletListClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnSelectFontClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnSelectMediaClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnSelectStickersClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnStickerChanged
import com.furianrt.notepage.internal.ui.page.PageEvent.OnStickerClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnStickerSelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnTagDoneEditing
import com.furianrt.notepage.internal.ui.page.PageEvent.OnTagFocusChanged
import com.furianrt.notepage.internal.ui.page.PageEvent.OnTagRemoveClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnTagTextCleared
import com.furianrt.notepage.internal.ui.page.PageEvent.OnTagTextEntered
import com.furianrt.notepage.internal.ui.page.PageEvent.OnTakePictureClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnTakePictureResult
import com.furianrt.notepage.internal.ui.page.PageEvent.OnTitleFocusChange
import com.furianrt.notepage.internal.ui.page.PageEvent.OnTitleTextChange
import com.furianrt.notepage.internal.ui.page.PageEvent.OnVoicePlayClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnVoiceProgressSelected
import com.furianrt.notepage.internal.ui.page.PageEvent.OnVoiceRecorded
import com.furianrt.notepage.internal.ui.page.PageEvent.OnVoiceRemoveClick
import com.furianrt.notepage.internal.ui.page.PageEvent.OnVoiceStarted
import com.furianrt.notepage.internal.ui.page.entities.NoteItem
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.toolspanel.api.NoteBackgroundProvider
import com.furianrt.toolspanel.api.StickerIconProvider
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
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.time.ZonedDateTime
import java.util.UUID
import com.furianrt.uikit.R as uiR

private const val MEDIA_VIEW_DIALOG_ID = 1
private const val MEDIA_SORTING_DIALOG_ID = 2
private const val TITLE_FOCUS_DELAY = 150L
private const val MAX_STICKERS_COUNT = 50

@OptIn(DelicateCoroutinesApi::class)
@HiltViewModel(assistedFactory = PageViewModel.Factory::class)
internal class PageViewModel @AssistedInject constructor(
    private val updateNoteContentUseCase: UpdateNoteContentUseCase,
    private val stickersRepository: StickersRepository,
    private val notesRepository: NotesRepository,
    private val dialogResultCoordinator: DialogResultCoordinator,
    private val permissionsUtils: PermissionsUtils,
    private val appearanceRepository: AppearanceRepository,
    private val audioPlayer: AudioPlayer,
    private val stickerIconProvider: StickerIconProvider,
    private val backgroundProvider: NoteBackgroundProvider,
    private val dispatchers: DispatchersProvider,
    private val syncManager: SyncManager,
    private val resourcesManager: ResourcesManager,
    private val mediaRepository: MediaRepository,
    private val lockAuthorizer: LockAuthorizer,
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
            if (field != value) {
                _effect.tryEmit(PageEffect.UpdateContentChangedState(value))
                field = value
            }
        }

    private var focusFirstTitle = isNoteCreationMode

    private var cachePhoto: UiNoteContent.MediaBlock.Image? = null
    private var cachedPhotoFile: File? = null

    init {
        dialogResultCoordinator.addDialogResultListener(requestId = noteId, listener = this)
        audioPlayer.setProgressListener(this)
        observeNote()
    }

    override fun onCleared() {
        dialogResultCoordinator.removeDialogResultListener(requestId = noteId, listener = this)
        audioPlayer.clearProgressListener()
        notesRepository.deleteNoteContentFromCache(noteId)
        stopCurrentVoicePlaying()
    }

    fun onEvent(event: PageEvent) {
        when (event) {
            is OnScreenStopped -> trySaveContent()
            is OnEditModeStateChange -> {
                changeEditModeState(event.isEnabled)
            }

            is OnIsSelectedChange -> {
                resetStickersEditing()
                onIsSelectedChange(event.isSelected)
            }

            is OnTagRemoveClick -> {
                resetStickersEditing()
                removeTag(event.tag)
            }

            is OnTagDoneEditing -> addTag(event.tag)
            is OnTagTextCleared -> tryToRemoveSecondTagTemplate()
            is OnTagFocusChanged -> resetStickersEditing()
            is OnTagTextEntered -> {
                hasContentChanged = true
                resetStickersEditing()
                addSecondTagTemplate()
            }

            is OnSelectMediaClick -> {
                resetStickersEditing()
                tryRequestMediaPermissions()
            }

            is OnTakePictureClick -> {
                resetStickersEditing()
                tryRequestCameraPermissions()
            }

            is OnCameraPermissionSelected -> tryOpenCamera()
            is OnMediaPermissionsSelected -> tryOpenMediaSelector()
            is OnCameraNotFoundError -> _effect.tryEmit(
                PageEffect.ShowMessage(
                    message = resourcesManager.getString(uiR.string.error_camera_not_found),
                )
            )

            is OnTakePictureResult -> onTakePictureResult(event.isSuccess)
            is OnTitleFocusChange -> {
                if (event.focused) {
                    resetStickersEditing()
                    focusedTitleId = event.id
                }
            }

            is OnFocusedTitleSelectionChange -> resetStickersEditing()
            is OnMediaClick -> {
                resetStickersEditing()
                openMediaViewScreen(event.media.id)
            }

            is OnMediaRemoveClick -> onMediaRemoveClick(event.media.id)

            is OnMediaSortingClick -> {
                resetStickersEditing()
                onMediaSortingClick(event.mediaBlockId)
            }

            is OnOpenMediaViewerRequest -> {
                resetStickersEditing()
                _effect.tryEmit(PageEffect.OpenMediaViewer(event.route))
            }

            is OnTitleTextChange -> hasContentChanged = hasContentChanged || isInEditMode
            is OnMediaSelected -> addNewBlock(event.result.toMediaBlock())
            is OnVoiceRecorded -> addNewBlock(event.record.toUiVoice())
            is OnSelectFontClick -> resetStickersEditing()
            is OnFontFamilySelected -> updateFontFamily(event.family)
            is OnFontColorSelected -> updateFontColor(event.color)
            is OnFontSizeSelected -> updateFontSize(event.size)
            is OnVoiceStarted -> resetStickersEditing()
            is OnVoicePlayClick -> {
                resetStickersEditing()
                onVoicePlayClick(event.voice)
            }

            is OnVoiceProgressSelected -> {
                resetStickersEditing()
                onVoiceProgressSelected(event.voice, event.value)
            }

            is OnVoiceRemoveClick -> onVoiceRemoveClick(event.voice)
            is OnSelectStickersClick -> resetStickersEditing()
            is OnSelectBulletListClick -> resetStickersEditing()
            is OnStickerSelected -> addSticker(event.sticker)
            is OnRemoveStickerClick -> removeSticker(event.sticker)
            is OnStickerChanged -> updateSticker(event.sticker)
            is OnStickerClick -> changeStickerEditing(event.sticker)
            is OnClickOutside -> {
                resetStickersEditing()
                _effect.tryEmit(PageEffect.HideKeyboard)
            }

            is OnNoPositionError -> _effect.tryEmit(
                PageEffect.ShowToast(
                    message = resourcesManager.getString(R.string.note_select_text_position_message),
                ),
            )

            is OnBackgroundsClick -> resetStickersEditing()
            is OnBackgroundSelected -> updateBackground(event.item)
            is OnMoodClick -> showMoodDialog()
            is OnMoodSelected -> updateNoteMood(event.moodId)
        }
    }

    override fun onDialogResult(dialogId: Int, result: DialogResult) {
        when (dialogId) {
            MEDIA_VIEW_DIALOG_ID -> if (result is DialogResult.Ok<*>) {
                @Suppress("UNCHECKED_CAST")
                removeMedia(result.data as Set<String>)
            }

            MEDIA_SORTING_DIALOG_ID -> if (result is DialogResult.Ok<*>) {
                onMediaSortingResult(result.data as MediaSortingResult)
            }
        }
    }

    private fun onMediaSortingResult(result: MediaSortingResult) {
        launch {
            _state.updateState<PageUiState.Success> { successState ->
                val fontFamily = successState.fontFamily ?: appearanceRepository.getAppFont()
                    .first()
                    .toUiNoteFontFamily()

                val newContent = if (result.media.isEmpty()) {
                    successState.content.removeMediaBlock(result.mediaBlockId, focusedTitleId)
                } else {
                    successState.content.map { content ->
                        if (content is UiNoteContent.MediaBlock && content.id == result.mediaBlockId) {
                            content.copy(media = result.media.mapImmutable { it.toUiNoteMedia() })
                        } else {
                            content
                        }
                    }
                }
                successState.copy(
                    content = newContent.refreshTitleTemplates(
                        fontFamily = fontFamily,
                        addTopTemplate = successState.isInEditMode,
                    ),
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

    override fun onAudioProgressChange(progress: Float) {
        _state.doWithState<PageUiState.Success> { successState ->
            successState.playingVoice?.progressState?.progress?.floatValue = progress
        }
    }

    override fun onAudioPlayComplete() {
        audioPlayer.stop()
        _state.updateState<PageUiState.Success> { currentState ->
            currentState.playingVoice?.progressState?.progress?.floatValue = 0f
            currentState.copy(playingVoiceId = null)
        }
    }

    private fun trySaveContent() {
        if (isInEditMode && hasContentChanged) {
            _state.doWithState<PageUiState.Success> { successState ->
                GlobalScope.launch { saveNoteContent(successState) }
            }
        }
    }

    private fun addNewBlock(newBlock: UiNoteContent) {
        hasContentChanged = true
        launch {
            _state.updateState<PageUiState.Success> { currentState ->
                val fontFamily = currentState.fontFamily
                    ?: appearanceRepository.getAppFont().first().toUiNoteFontFamily()
                val newContent = buildContentWithNewBlock(
                    fontFamily = fontFamily,
                    content = currentState.content,
                    newBlock = newBlock,
                )
                currentState.copy(
                    content = newContent.refreshTitleTemplates(
                        fontFamily = fontFamily,
                        addTopTemplate = currentState.isInEditMode,
                    ),
                )
            }
            delay(TITLE_FOCUS_DELAY)
            _state.doWithState<PageUiState.Success> { successState ->
                val blockIndex = successState.content.indexOfFirstOrNull { it.id == newBlock.id }
                if (blockIndex != null) {
                    val title = successState.content
                        .subList(blockIndex, successState.content.size)
                        .firstOrNull { it is UiNoteContent.Title } as? UiNoteContent.Title
                    if (title != null) {
                        _effect.tryEmit(PageEffect.BringContentToView(title))
                        title.focusRequester.requestFocus()
                    }
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
        fontFamily: UiNoteFontFamily,
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
                    state = NoteTitleState(
                        fontFamily = fontFamily,
                        initialText = titleFirstPartText,
                    )
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
                        fontFamily = fontFamily,
                    )
                )
                val titleSecondPart = focusedTitle.also { title ->
                    val text = title.state.annotatedString
                    val tempPart = text.subSequence(selection, text.length)
                    title.state.annotatedString = if (tempPart.isBlank()) {
                        AnnotatedString("")
                    } else {
                        title.state.annotatedString.subSequence(
                            startIndex = selection + tempPart.indexOfFirst { it != ' ' && it != '\n' },
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

    private fun tryRequestCameraPermissions() {
        if (permissionsUtils.hasCameraPermission()) {
            launch { takePicture() }
        } else {
            _effect.tryEmit(RequestCameraPermission)
        }
    }

    private fun tryOpenCamera() {
        if (permissionsUtils.hasCameraPermission()) {
            launch { takePicture() }
        } else {
            _effect.tryEmit(ShowCameraPermissionsDeniedDialog)
        }
    }

    private suspend fun takePicture() {
        val uri = createPhotoFile()
        if (uri != null) {
            lockAuthorizer.skipNextLock()
            _effect.tryEmit(TakePicture(uri))
        } else {
            _effect.tryEmit(
                PageEffect.ShowMessage(resourcesManager.getString(uiR.string.general_error)),
            )
        }
    }

    private fun onTakePictureResult(isSuccess: Boolean) = launch {
        lockAuthorizer.cancelSkipNextLock()
        val image = cachePhoto
        val file = cachedPhotoFile
        if (isSuccess && image != null && file != null) {
            addNewBlock(
                newBlock = UiNoteContent.MediaBlock(
                    id = UUID.randomUUID().toString(),
                    media = persistentListOf(
                        image.copy(
                            ratio = mediaRepository.getRatio(file),
                            addedDate = ZonedDateTime.now(),
                        )
                    ),
                )
            )
        } else {
            deletePhotoFile()
        }
        cachePhoto = null
        cachedPhotoFile = null
    }

    private suspend fun createPhotoFile(): Uri? {
        val mediaId = UUID.randomUUID().toString()
        val mediaName = "camera_photo.jpg"
        val file = mediaRepository.createMediaDestinationFile(
            noteId = noteId,
            mediaId = mediaId,
            mediaName = mediaName,
        )
        return if (file != null) {
            val uri = mediaRepository.getRelativeUri(file)
            val image = UiNoteContent.MediaBlock.Image(
                id = mediaId,
                name = mediaName,
                uri = uri,
                ratio = 1f,
                addedDate = ZonedDateTime.now(),
            )
            cachePhoto = image
            cachedPhotoFile = file
            uri
        } else {
            null
        }
    }

    private suspend fun deletePhotoFile() {
        cachedPhotoFile?.let { mediaRepository.deleteFile(it) }
    }

    private fun tryOpenMediaSelector() {
        if (permissionsUtils.mediaAccessDenied()) {
            _effect.tryEmit(ShowStoragePermissionsDeniedDialog)
        } else {
            _effect.tryEmit(OpenMediaSelector)
        }
    }

    private fun openMediaViewScreen(mediaId: String) {
        _state.doWithState<PageUiState.Success> { successState ->
            notesRepository.cacheNoteContent(
                noteId = noteId,
                content = successState.content.map(UiNoteContent::toLocalNoteContent),
            )
            _effect.tryEmit(
                PageEffect.OpenMediaViewScreen(
                    noteId = noteId,
                    mediaId = mediaId,
                    identifier = DialogIdentifier(
                        dialogId = MEDIA_VIEW_DIALOG_ID,
                        requestId = noteId,
                    ),
                ),
            )
        }
    }

    private fun onMediaSortingClick(mediaBlockId: String) {
        when {
            syncManager.isBackupInProgress() -> _effect.tryEmit(
                PageEffect.ShowMessage(
                    message = resourcesManager.getString(uiR.string.backup_in_progress),
                ),
            )

            syncManager.isRestoreInProgress() -> _effect.tryEmit(
                PageEffect.ShowMessage(
                    message = resourcesManager.getString(uiR.string.restore_in_progress),
                ),
            )

            else -> {
                openMediaSortingScreen(mediaBlockId)
            }
        }
    }

    private fun openMediaSortingScreen(mediaBlockId: String) {
        _state.doWithState<PageUiState.Success> { successState ->
            notesRepository.cacheNoteContent(
                noteId = noteId,
                content = successState.content.map(UiNoteContent::toLocalNoteContent),
            )
            _effect.tryEmit(
                PageEffect.OpenMediaSortingScreen(
                    noteId = noteId,
                    mediaBlockId = mediaBlockId,
                    identifier = DialogIdentifier(
                        dialogId = MEDIA_SORTING_DIALOG_ID,
                        requestId = noteId,
                    ),
                ),
            )
        }
    }

    private fun onMediaRemoveClick(mediaId: String) {
        resetStickersEditing()
        when {
            syncManager.isBackupInProgress() -> _effect.tryEmit(
                PageEffect.ShowMessage(
                    message = resourcesManager.getString(uiR.string.backup_in_progress),
                ),
            )

            syncManager.isRestoreInProgress() -> _effect.tryEmit(
                PageEffect.ShowMessage(
                    message = resourcesManager.getString(uiR.string.restore_in_progress),
                ),
            )

            else -> {
                removeMedia(setOf(mediaId))
            }
        }
    }

    private fun removeMedia(mediaIds: Set<String>) {
        if (mediaIds.isEmpty()) {
            return
        }
        GlobalScope.launch {
            _state.updateState<PageUiState.Success> { currentState ->
                var newContent = currentState.content
                mediaIds.forEach { newContent = newContent.removeMedia(it, focusedTitleId) }
                val fontFamily = currentState.fontFamily
                    ?: appearanceRepository.getAppFont().first().toUiNoteFontFamily()
                currentState.copy(
                    content = newContent.refreshTitleTemplates(
                        fontFamily = fontFamily,
                        addTopTemplate = currentState.isInEditMode,
                    )
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

    private fun onVoiceRemoveClick(voice: UiNoteContent.Voice) {
        resetStickersEditing()
        when {
            syncManager.isBackupInProgress() -> _effect.tryEmit(
                PageEffect.ShowMessage(
                    message = resourcesManager.getString(uiR.string.backup_in_progress),
                ),
            )

            syncManager.isRestoreInProgress() -> _effect.tryEmit(
                PageEffect.ShowMessage(
                    message = resourcesManager.getString(uiR.string.restore_in_progress),
                ),
            )

            else -> {
                removeVoiceRecord(voice)
            }
        }
    }

    private fun removeVoiceRecord(voice: UiNoteContent.Voice) {
        GlobalScope.launch {
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
        _state.doWithState<PageUiState.Success> { currentState ->
            when (currentState.playingVoiceId) {
                voice.id -> stopCurrentVoicePlaying()
                null -> playVoice(voice)
                else -> playNextVoice(voice)
            }
        }
    }

    private fun playVoice(voice: UiNoteContent.Voice) {
        _state.updateState<PageUiState.Success> { currentState ->
            audioPlayer.play(voice.uri, voice.progressState.progress.floatValue)
            currentState.copy(playingVoiceId = voice.id)
        }
    }

    private fun playNextVoice(voice: UiNoteContent.Voice) {
        _state.updateState<PageUiState.Success> { currentState ->
            audioPlayer.stop()
            audioPlayer.play(voice.uri, voice.progressState.progress.floatValue)
            currentState.copy(playingVoiceId = voice.id)
        }
    }

    private fun stopCurrentVoicePlaying() {
        _state.updateState<PageUiState.Success> { currentState ->
            audioPlayer.stop()
            currentState.copy(playingVoiceId = null)
        }
    }

    private fun onVoiceProgressSelected(voice: UiNoteContent.Voice, progress: Float) {
        _state.doWithState<PageUiState.Success> { successState ->
            if (voice.id == successState.playingVoiceId) {
                audioPlayer.setProgress(progress)
            }
        }
    }

    private fun addSticker(sticker: StickerItem) {
        val stickersCount = _state.getState<PageUiState.Success>()?.stickers?.count() ?: 0
        if (stickersCount < MAX_STICKERS_COUNT) {
            hasContentChanged = true
            resetStickersEditing()
            _state.updateState<PageUiState.Success> { currentState ->
                currentState.copy(stickers = currentState.stickers.toPersistentList().add(sticker))
            }
        } else {
            _effect.tryEmit(
                PageEffect.ShowMessage(
                    message = resourcesManager.getString(R.string.note_sticker_limit_message),
                ),
            )
        }
    }

    private fun removeSticker(sticker: StickerItem) {
        _state.updateState<PageUiState.Success> { currentState ->
            currentState.copy(
                stickers = currentState.stickers.toPersistentList()
                    .removeAll { it.id == sticker.id },
            ).also { newState ->
                if (isInEditMode) {
                    hasContentChanged = true
                } else {
                    GlobalScope.launch { saveNoteContent(newState) }
                }
            }
        }
    }

    private fun updateSticker(sticker: StickerItem) {
        if (isInEditMode) {
            hasContentChanged = true
        } else {
            launch {
                stickersRepository.update(listOf(sticker.toLocalNoteSticker()))
            }
        }
    }

    private fun resetStickersEditing() {
        _state.getState<PageUiState.Success>()?.stickers?.forEach { sticker ->
            sticker.state.isEditing = false
        }
    }

    private fun changeStickerEditing(sticker: StickerItem) {
        _state.updateState<PageUiState.Success> { currentState ->
            currentState.stickers.forEach { item ->
                if (item.id == sticker.id) {
                    if (!item.state.isEditing) {
                        item.state.editTime = System.currentTimeMillis()
                    }
                    item.state.isEditing = !item.state.isEditing
                } else {
                    item.state.isEditing = false
                }
            }
            currentState.copy(
                stickers = currentState.stickers
                    .sortedBy { it.state.editTime }
                    .toImmutableList(),
            )
        }
        updateSticker(sticker)
    }

    private fun onIsSelectedChange(isSelected: Boolean) {
        if (!isSelected) {
            stopCurrentVoicePlaying()
            _state.updateState<PageUiState.Success> { currentState ->
                currentState.copy(playingVoiceId = null)
            }
        }
    }

    private fun changeEditModeState(isEnabled: Boolean) {
        launch {
            _state.updateState<PageUiState.Success> { currentState ->
                val fontFamily = currentState.fontFamily
                    ?: appearanceRepository.getAppFont().first().toUiNoteFontFamily()
                val newState = currentState.copy(
                    content = currentState.content.refreshTitleTemplates(
                        fontFamily = fontFamily,
                        addTopTemplate = isEnabled
                    ),
                    tags = with(currentState.tags) {
                        if (isEnabled) addTagTemplate() else removeTagTemplate(onlyEmpty = true)
                    },
                    isInEditMode = isEnabled,
                )
                if (!isEnabled && hasContentChanged) {
                    launch { saveNoteContent(newState) }
                }
                return@updateState newState
            }

            _state.doWithState<PageUiState.Success> {
                if (!isEnabled) {
                    resetStickersEditing()
                    focusedTitleId = null
                    hasContentChanged = false
                }
            }
        }
    }

    private fun observeNote() {
        launch {
            notesRepository.getNote(noteId)
                .map { note ->
                    note?.toNoteItem(
                        appFont = note.fontFamily ?: appearanceRepository.getAppFont().first(),
                        stickerIconProvider = stickerIconProvider::getIcon,
                        background = backgroundProvider.getBackground(note.backgroundId),
                    )
                }
                .distinctUntilChanged()
                .flowOn(dispatchers.default)
                .collectLatest(::handleNoteResult)
        }
    }

    private fun handleNoteResult(note: NoteItem?) {
        if (note == null) {
            _state.update { PageUiState.Empty }
            return
        }

        launch {
            _state.update { localState ->
                when (localState) {
                    is PageUiState.Empty, PageUiState.Loading -> {
                        PageUiState.Success(
                            noteId = note.id,
                            content = note.content.refreshTitleTemplates(
                                fontFamily = note.fontFamily
                                    ?: appearanceRepository.getAppFont().first()
                                        .toUiNoteFontFamily(),
                                addTopTemplate = isInEditMode
                            ),
                            tags = with(note.tags) {
                                if (isNoteCreationMode || isInEditMode) {
                                    addTagTemplate()
                                } else {
                                    removeTagTemplate(onlyEmpty = true)
                                }
                            },
                            stickers = note.stickers,
                            playingVoiceId = null,
                            fontFamily = note.fontFamily,
                            fontColor = note.fontColor,
                            fontSize = note.fontSize,
                            noteBackground = note.background,
                            moodId = note.moodId,
                            defaultMoodId = appearanceRepository.getDefaultNoteMoodId().first(),
                            isInEditMode = isNoteCreationMode,
                        ).also {
                            if (isNoteCreationMode) {
                                tryFocusFirstTitle()
                            }
                        }
                    }

                    is PageUiState.Success -> localState
                }
            }
        }
    }

    private fun tryFocusFirstTitle() = launch {
        if (focusFirstTitle) {
            focusFirstTitle = false
            delay(TITLE_FOCUS_DELAY)
            _state.doWithState<PageUiState.Success> { successState ->
                val firstTitle = successState.content
                    .filterIsInstance<UiNoteContent.Title>()
                    .firstOrNull()
                firstTitle?.focusRequester?.requestFocus()
            }
        }
    }

    private suspend fun saveNoteContent(state: PageUiState.Success) {
        val fontFamily = state.fontFamily?.toNoteFontFamily()
        val fontColor = state.fontColor?.toNoteFontColor()
        val fontSize = state.fontSize
        val note = notesRepository.getNote(noteId).first()
        if (note != null) {
            updateNoteContentUseCase(
                noteId = noteId,
                content = state.content.map(UiNoteContent::toLocalNoteContent),
                tags = state.tags.map(UiNoteTag::toLocalNoteTag).filter { it.title.isNotBlank() },
                stickers = state.stickers.map(StickerItem::toLocalNoteSticker),
                fontFamily = fontFamily,
                fontColor = fontColor,
                fontSize = fontSize,
                backgroundId = state.noteBackground?.id,
                moodId = state.moodId,
            )
            if (isNoteCreationMode) {
                saveDefaultFontData(state)
            }
        }
        hasContentChanged = false
    }

    private suspend fun saveDefaultFontData(state: PageUiState.Success) {
        appearanceRepository.setDefaultNoteFont(state.fontFamily?.toNoteFontFamily())
        appearanceRepository.setDefaultNoteFontColor(state.fontColor?.toNoteFontColor())
        appearanceRepository.setDefaultNoteFontSize(state.fontSize)
    }

    private fun updateFontFamily(family: UiNoteFontFamily?) {
        hasContentChanged = true
        launch {
            val fontFamily =
                family ?: appearanceRepository.getAppFont().first().toUiNoteFontFamily()
            _state.updateState<PageUiState.Success> { successState ->
                successState.content.forEach { content ->
                    if (content is UiNoteContent.Title) {
                        content.state.updateFontFamily(fontFamily)
                    }
                }
                successState.copy(fontFamily = family)
            }
        }
    }

    private fun updateFontColor(color: UiNoteFontColor?) {
        hasContentChanged = true
        _state.updateState<PageUiState.Success> { it.copy(fontColor = color) }
    }

    private fun updateFontSize(size: Int) {
        hasContentChanged = true
        _state.updateState<PageUiState.Success> { it.copy(fontSize = size) }
    }

    private fun updateBackground(background: UiNoteBackground?) {
        hasContentChanged = true
        _state.updateState<PageUiState.Success> { it.copy(noteBackground = background) }
    }

    private fun showMoodDialog() {
        resetStickersEditing()
        _state.doWithState<PageUiState.Success> { successState ->
            _effect.tryEmit(
                PageEffect.ShowMoodDialog(
                    moodId = successState.moodId,
                    defaultMoodId = successState.defaultMoodId,
                )
            )
        }
    }

    private fun updateNoteMood(moodId: String?) = launch {
        _state.updateState<PageUiState.Success> { successState ->
            successState.copy(
                moodId = moodId,
                defaultMoodId = moodId ?: successState.defaultMoodId,
            ).also {
                if (isInEditMode) {
                    hasContentChanged = true
                } else {
                    saveNoteContent(it)
                }
                if (moodId != null) {
                    notesRepository.updateNoteDefaultMoodId(moodId)
                }
            }
        }
    }

    private fun PageUiState.Success.addTag(
        tag: UiNoteTag.Regular,
        addTemplate: Boolean,
    ): PageUiState.Success {
        val result = if (tags.any { it.id == tag.id }) {
            tags.removeTagTemplate(tag.id)
        } else {
            tags.toPersistentList()
                .add(index = tags.indexOfLast { it is UiNoteTag.Regular } + 1, element = tag)
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
