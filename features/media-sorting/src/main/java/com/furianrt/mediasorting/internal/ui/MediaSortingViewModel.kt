package com.furianrt.mediasorting.internal.ui

import android.net.Uri
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.MediaSortingResult
import com.furianrt.domain.managers.LockAuthorizer
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediasorting.api.MediaSortingRoute
import com.furianrt.mediasorting.internal.domain.GetNoteMediaUseCase
import com.furianrt.mediasorting.internal.extensions.toLocalNoteMedia
import com.furianrt.mediasorting.internal.extensions.toMediaItem
import com.furianrt.mediasorting.internal.extensions.toMediaItems
import com.furianrt.mediasorting.internal.ui.entities.MediaItem
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.DialogResult
import com.furianrt.uikit.utils.DialogResultCoordinator
import com.furianrt.uikit.utils.DialogResultListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.io.File
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

private const val MEDIA_VIEW_DIALOG_ID = 0

@HiltViewModel
internal class MediaSortingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNoteMediaUseCase: GetNoteMediaUseCase,
    private val permissionsUtils: PermissionsUtils,
    private val dialogResultCoordinator: DialogResultCoordinator,
    private val notesRepository: NotesRepository,
    private val resourcesManager: ResourcesManager,
    private val lockAuthorizer: LockAuthorizer,
    private val mediaRepository: MediaRepository,
) : ViewModel(), DialogResultListener {

    private val route = savedStateHandle.toRoute<MediaSortingRoute>()

    private val _state = MutableStateFlow(buildInitialState())
    val state: StateFlow<MediaSortingUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaSortingEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    private var cachePhoto: MediaItem.Image? = null
    private var cachedPhotoFile: File? = null

    init {
        dialogResultCoordinator.addDialogResultListener(requestId = route.noteId, listener = this)
    }

    override fun onCleared() {
        dialogResultCoordinator.removeDialogResultListener(
            requestId = route.noteId,
            listener = this
        )
    }

    fun onEvent(event: MediaSortingEvent) {
        when (event) {
            is MediaSortingEvent.OnAddMediaClick -> tryRequestMediaPermissions()
            is MediaSortingEvent.OnTakePhotoClick -> tryRequestCameraPermissions()
            is MediaSortingEvent.OnMediaPermissionsSelected -> tryOpenMediaSelector()
            is MediaSortingEvent.OnButtonBackClick -> checkCloseScreen()
            is MediaSortingEvent.OnButtonDoneClick -> {
                sendResult()
                _effect.tryEmit(MediaSortingEffect.CloseScreen)
            }

            is MediaSortingEvent.OnConfirmCloseClick -> {
                _effect.tryEmit(MediaSortingEffect.CloseScreen)
            }

            is MediaSortingEvent.OnMediaClick -> launch {
                openMediaViewScreen(event.media.id)
            }

            is MediaSortingEvent.OnMediaItemMoved -> changeMediaOrder(event.from, event.to)
            is MediaSortingEvent.OnMediaSelected -> addMedia(event.result)
            is MediaSortingEvent.OnOpenMediaViewerRequest -> {
                _effect.tryEmit(MediaSortingEffect.OpenMediaViewer(event.route))
            }

            is MediaSortingEvent.OnRemoveMediaClick -> removeMedia(setOf(event.media.id))
            is MediaSortingEvent.OnCameraNotFoundError -> _effect.tryEmit(
                MediaSortingEffect.ShowMessage(
                    message = resourcesManager.getString(uiR.string.error_camera_not_found),
                )
            )

            is MediaSortingEvent.OnCameraPermissionSelected -> tryOpenCamera()
            is MediaSortingEvent.OnTakePictureResult -> onTakePictureResult(event.isSuccess)
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

    private fun sendResult() {
        if (state.value.hasContentChanged) {
            dialogResultCoordinator.onDialogResult(
                dialogIdentifier = DialogIdentifier(
                    requestId = route.requestId,
                    dialogId = route.dialogId,
                ),
                code = DialogResult.Ok(
                    data = MediaSortingResult(
                        noteId = route.noteId,
                        mediaBlockId = route.mediaBlockId,
                        media = state.value.media.map(MediaItem::toLocalNoteMedia),
                    )
                ),
            )
        }
    }

    private fun checkCloseScreen() {
        if (state.value.hasContentChanged) {
            _effect.tryEmit(MediaSortingEffect.ShowConfirmCloseDialog)
        } else {
            _effect.tryEmit(MediaSortingEffect.CloseScreen)
        }
    }

    private fun changeMediaOrder(from: LazyGridItemInfo, to: LazyGridItemInfo) {
        _state.update { currentState ->
            currentState.copy(
                media = currentState.media.toMutableList().apply {
                    add(to.index, removeAt(from.index))
                },
                hasContentChanged = true,
            )
        }
    }

    private fun addMedia(result: MediaResult) {
        addMedia(result.toMediaItems())
    }

    private fun addMedia(media: List<MediaItem>) {
        _state.update { currentState ->
            currentState.copy(
                media = currentState.media.toMutableList().apply { addAll(media) },
                hasContentChanged = true
            )
        }
    }

    private fun removeMedia(mediaIds: Set<String>) {
        _state.update { currentState ->
            currentState.copy(
                media = currentState.media.toMutableList().apply {
                    removeAll { mediaIds.contains(it.id) }
                },
                hasContentChanged = true,
            )
        }
    }

    private fun tryRequestMediaPermissions() {
        if (permissionsUtils.mediaAccessDenied()) {
            _effect.tryEmit(MediaSortingEffect.RequestStoragePermissions)
        } else {
            _effect.tryEmit(MediaSortingEffect.OpenMediaSelector)
        }
    }

    private fun tryOpenMediaSelector() {
        if (permissionsUtils.mediaAccessDenied()) {
            _effect.tryEmit(MediaSortingEffect.ShowPermissionsDeniedDialog)
        } else {
            _effect.tryEmit(MediaSortingEffect.OpenMediaSelector)
        }
    }

    private suspend fun openMediaViewScreen(mediaId: String) {
        val cachedNoteContent = notesRepository.getNote(route.noteId).first()?.content ?: return
        notesRepository.cacheNoteContent(
            noteId = route.noteId,
            content = cachedNoteContent.map { content ->
                if (content is LocalNote.Content.MediaBlock && content.id == route.mediaBlockId) {
                    content.copy(media = state.value.media.map(MediaItem::toLocalNoteMedia))
                } else {
                    content
                }
            },
        )
        _effect.tryEmit(
            MediaSortingEffect.OpenMediaViewScreen(
                noteId = route.noteId,
                mediaBlockId = route.mediaBlockId,
                mediaId = mediaId,
                identifier = DialogIdentifier(
                    dialogId = MEDIA_VIEW_DIALOG_ID,
                    requestId = route.noteId,
                ),
            ),
        )
    }

    private fun tryRequestCameraPermissions() {
        if (permissionsUtils.hasCameraPermission()) {
            launch { takePicture() }
        } else {
            _effect.tryEmit(MediaSortingEffect.RequestCameraPermission)
        }
    }

    private fun onTakePictureResult(isSuccess: Boolean) = launch {
        lockAuthorizer.cancelSkipNextLock()
        val image = cachePhoto
        val file = cachedPhotoFile
        if (isSuccess && image != null && file != null) {
            addMedia(
                media = listOf(
                    image.copy(
                        ratio = mediaRepository.getAspectRatio(file),
                        addedDate = ZonedDateTime.now(),
                    )
                )
            )
        } else {
            deletePhotoFile()
        }
        cachePhoto = null
        cachedPhotoFile = null
    }

    private fun tryOpenCamera() {
        if (permissionsUtils.hasCameraPermission()) {
            launch { takePicture() }
        } else {
            _effect.tryEmit(MediaSortingEffect.ShowCameraPermissionsDeniedDialog)
        }
    }

    private suspend fun takePicture() {
        val uri = createPhotoFile()
        if (uri != null) {
            lockAuthorizer.skipNextLock()
            _effect.tryEmit(MediaSortingEffect.TakePicture(uri))
        } else {
            _effect.tryEmit(
                MediaSortingEffect.ShowMessage(resourcesManager.getString(uiR.string.general_error)),
            )
        }
    }

    private suspend fun createPhotoFile(): Uri? {
        val mediaId = UUID.randomUUID().toString()
        val file = mediaRepository.createMediaDestinationFile(
            noteId = route.noteId,
            mediaId = mediaId,
            mediaName = MediaRepository.CAMERA_PICTURE_NAME,
        )
        return if (file != null) {
            val uri = mediaRepository.getRelativeUri(file)
            val image = MediaItem.Image(
                id = mediaId,
                name = MediaRepository.CAMERA_PICTURE_NAME,
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

    private fun buildInitialState() = MediaSortingUiState(
        media = getNoteMediaUseCase(route.noteId, route.mediaBlockId)
            .map(LocalNote.Content.Media::toMediaItem),
        hasContentChanged = false,
    )
}