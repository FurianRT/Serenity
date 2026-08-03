package com.furianrt.mediaselector.internal.ui.selector

import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.furianrt.core.updateState
import com.furianrt.domain.entities.DeviceAlbum
import com.furianrt.domain.entities.DeviceMedia
import com.furianrt.domain.managers.LockAuthorizer
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.mediaselector.R
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediaselector.internal.domain.SelectedMediaCoordinator
import com.furianrt.mediaselector.internal.ui.entities.MediaAlbumItem
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import com.furianrt.mediaselector.internal.ui.extensions.toMediaAlbumItem
import com.furianrt.mediaselector.internal.ui.extensions.toMediaItem
import com.furianrt.mediaselector.internal.ui.extensions.toMediaItems
import com.furianrt.mediaselector.internal.ui.extensions.toMediaSelectorResult
import com.furianrt.mediaselector.internal.ui.extensions.toThumbnailItem
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEffect.CloseScreen
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEffect.OpenMediaViewer
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEffect.RequestMediaPermissions
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnAlbumSelected
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnAlbumsClick
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnAlbumsDismissed
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnCameraItemClick
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnCameraNotFoundError
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnCameraPermissionSelected
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnCloseScreenRequest
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnExpanded
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnMediaClick
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnMediaPermissionsSelected
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnPartialAccessMessageClick
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnScreenResumed
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnSelectItemClick
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnSendClick
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnTakePictureResult
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.utils.DialogResult
import com.furianrt.uikit.utils.DialogResultCoordinator
import com.furianrt.uikit.utils.DialogResultListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.util.UUID
import javax.inject.Inject

private const val TAG = "MediaSelectorViewModel"
private const val MEDIA_VIEWER_DIALOG_ID = 1

@HiltViewModel
internal class MediaSelectorViewModel @Inject constructor(
    private val dialogResultCoordinator: DialogResultCoordinator,
    private val mediaRepository: MediaRepository,
    private val permissionsUtils: PermissionsUtils,
    private val mediaCoordinator: SelectedMediaCoordinator,
    private val resourcesManager: ResourcesManager,
    private val lockAuthorizer: LockAuthorizer,
) : ViewModel(),
    DialogResultListener,
    DefaultLifecycleObserver {

    private val _state = MutableStateFlow<MediaSelectorUiState>(MediaSelectorUiState.Loading)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaSelectorEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    private var isDataLoaded = false
    private var allowVideo = true
    private var isSingleChoice = false
    private var onMediaSelected: suspend (result: MediaResult) -> Unit = {}
    private var sendResultOnStart = false

    private var cachePhoto: MediaItem.Image? = null
    private var cachedPhotoFile: File? = null

    init {
        dialogResultCoordinator.addDialogResultListener(requestId = TAG, listener = this)
    }

    override fun onResume(owner: LifecycleOwner) {
        if (sendResultOnStart) {
            sendResultOnStart = false
            launch { onSendClick() }
        }
    }

    override fun onCleared() {
        dialogResultCoordinator.removeDialogResultListener(requestId = TAG, listener = this)
    }

    override fun onDialogResult(dialogId: Int, result: DialogResult) {
        when (dialogId) {
            MEDIA_VIEWER_DIALOG_ID -> when (result) {
                is DialogResult.Ok<*> -> sendResultOnStart = true
                is DialogResult.Cancel -> {
                    _state.updateState<MediaSelectorUiState.Success> { currentState ->
                        currentState.setSelectedItems(
                            selectedItems = mediaCoordinator.getSelectedMedia(),
                            useCounter = !isSingleChoice,
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: MediaSelectorEvent) {
        when (event) {
            is OnPartialAccessMessageClick -> _effect.tryEmit(RequestMediaPermissions)
            is OnMediaPermissionsSelected -> loadMediaItems(
                selectedAlbum = null,
            )

            is OnSelectItemClick -> toggleItemSelection(event.item)
            is OnMediaClick -> _effect.tryEmit(
                OpenMediaViewer(
                    dialogId = MEDIA_VIEWER_DIALOG_ID,
                    requestId = TAG,
                    mediaId = event.id,
                    albumId = (_state.value as? MediaSelectorUiState.Success)
                        ?.selectedAlbum?.id
                        ?.takeUnless { it == MediaAlbumItem.ALL_MEDIA_ALBUM_ID },
                    allowVideo = allowVideo,
                    singleChoice = isSingleChoice,
                )
            )

            is OnSendClick -> launch { onSendClick() }
            is OnCloseScreenRequest -> {
                _effect.tryEmit(CloseScreen)
                mediaCoordinator.unselectAllMedia()
                _state.updateState<MediaSelectorUiState.Success> { currentState ->
                    currentState.setSelectedItems(
                        selectedItems = emptyList(),
                        useCounter = !isSingleChoice,
                    )
                }
                isDataLoaded = false
            }

            is OnExpanded -> {
                onMediaSelected = event.params?.onMediaSelected ?: onMediaSelected
                val allowVideoTemp = event.params?.allowVideo ?: allowVideo
                val isSingleChoiceTemp = event.params?.singleChoice ?: isSingleChoice
                if (
                    !isDataLoaded ||
                    allowVideo != allowVideoTemp ||
                    isSingleChoice != isSingleChoiceTemp
                ) {
                    allowVideo = allowVideoTemp
                    isSingleChoice = isSingleChoiceTemp
                    loadMediaItems(selectedAlbum = _state.value.selectedAlbum)
                }
            }

            is OnScreenResumed -> if (isDataLoaded) {
                loadMediaItems(
                    selectedAlbum = _state.value.selectedAlbum,
                )
            }

            is OnAlbumsClick -> launch {
                val allMedia = mediaRepository.getDeviceMediaList(allowVideo)
                val allMediaAlbum = MediaAlbumItem(
                    id = MediaAlbumItem.ALL_MEDIA_ALBUM_ID,
                    name = resourcesManager.getString(R.string.media_selector_all_media),
                    thumbnail = allMedia.firstOrNull()?.toThumbnailItem(),
                    mediaCount = allMedia.size,
                )
                val albumsItems = mediaRepository.getDeviceAlbumsList(allowVideo)
                    .map(DeviceAlbum::toMediaAlbumItem)
                    .toMutableList()
                    .apply { add(0, allMediaAlbum) }
                _effect.tryEmit(MediaSelectorEffect.ShowAlbumsList(albumsItems))
            }

            is OnAlbumSelected -> when (val currentState = _state.value) {
                is MediaSelectorUiState.Loading -> Unit
                is MediaSelectorUiState.Success -> {
                    if (currentState.selectedAlbum?.id != event.album.id) {
                        loadMediaItems(
                            selectedAlbum = event.album,
                        )
                    }
                }
            }

            is OnAlbumsDismissed -> _effect.tryEmit(MediaSelectorEffect.HideAlbumsList)
            is OnCameraItemClick -> onCameraItemClick()
            is OnCameraPermissionSelected -> tryOpenCamera()
            is OnTakePictureResult -> onTakePictureResult(event.isSuccess)
            is OnCameraNotFoundError -> _effect.tryEmit(
                MediaSelectorEffect.ShowMessage(
                    text = resourcesManager.getString(uiR.string.error_camera_not_found),
                )
            )
        }
    }

    private suspend fun onSendClick() {
        onMediaSelected(mediaCoordinator.getSelectedMedia().toMediaSelectorResult())
        _effect.tryEmit(CloseScreen)
        mediaCoordinator.unselectAllMedia()
        _state.updateState<MediaSelectorUiState.Success> { currentState ->
            currentState.setSelectedItems(
                selectedItems = emptyList(),
                useCounter = !isSingleChoice,
            )
        }
    }

    private fun loadMediaItems(
        selectedAlbum: MediaAlbumItem?,
    ) {
        if (permissionsUtils.mediaAccessDenied()) {
            _effect.tryEmit(CloseScreen)
            mediaCoordinator.unselectAllMedia()
            _state.updateState<MediaSelectorUiState.Success> { currentState ->
                currentState.setSelectedItems(
                    selectedItems = emptyList(),
                    useCounter = !isSingleChoice,
                )
            }
            return
        }
        launch {
            _state.update { currentState ->
                val media = mediaRepository.getDeviceMediaList(
                    albumId = selectedAlbum?.id
                        ?.takeUnless { it == MediaAlbumItem.ALL_MEDIA_ALBUM_ID },
                    allowVideo = allowVideo,
                )
                when {
                    currentState is MediaSelectorUiState.Success -> {
                        val selectedMedia = mediaCoordinator.getSelectedMedia()
                        val cameraItemIndex = selectedMedia.indexOfFirst { it.isCameraItem }
                        currentState.copy(
                            items = media.toMediaItems(
                                state = { id ->
                                    val selectedIndex = selectedMedia.indexOfFirst { it.id == id }
                                    when {
                                        selectedIndex != -1 && isSingleChoice -> {
                                            SelectionState.Single
                                        }

                                        selectedIndex != -1 && !isSingleChoice -> {
                                            SelectionState.Counter(order = selectedIndex + 1)
                                        }

                                        else -> SelectionState.Default
                                    }
                                },
                            ),
                            cameraState = when {
                                cameraItemIndex == -1 -> SelectionState.Default
                                isSingleChoice -> SelectionState.Single
                                else -> SelectionState.Counter(cameraItemIndex + 1)
                            },
                            selectedAlbum = selectedAlbum,
                            selectedCount = selectedMedia.count(),
                            showPartialAccessMessage = permissionsUtils.hasPartialMediaAccess(),
                        )
                    }

                    else -> MediaSelectorUiState.Success(
                        items = media.map(DeviceMedia::toMediaItem),
                        selectedCount = 0,
                        showPartialAccessMessage = permissionsUtils.hasPartialMediaAccess(),
                        selectedAlbum = selectedAlbum,
                        cameraState = SelectionState.Default,
                    )
                }
            }
            isDataLoaded = true
        }
    }

    private fun toggleItemSelection(item: MediaItem) {
        if (isSingleChoice) {
            mediaCoordinator.unselectAllMedia()
            if (item.state is SelectionState.Default) {
                mediaCoordinator.selectMedia(item)
            }
        } else {
            when (item.state) {
                is SelectionState.Default -> mediaCoordinator.selectMedia(item)
                is SelectionState.Counter -> mediaCoordinator.unselectMedia(item)
                is SelectionState.Single -> mediaCoordinator.unselectMedia(item)
            }
        }
        updateSelectedItems()
    }

    private fun updateSelectedItems() {
        _state.updateState<MediaSelectorUiState.Success> { currentState ->
            currentState.setSelectedItems(
                selectedItems = mediaCoordinator.getSelectedMedia(),
                useCounter = !isSingleChoice,
            )
        }
    }

    private fun onCameraItemClick() {
        val cameraItem = mediaCoordinator.getSelectedMedia().find { it.isCameraItem }
        if (cameraItem != null) {
            mediaCoordinator.unselectMedia(cameraItem)
            updateSelectedItems()
        } else {
            tryRequestCameraPermissions()
        }
    }

    private fun tryRequestCameraPermissions() {
        if (permissionsUtils.hasCameraPermission()) {
            launch { takePicture() }
        } else {
            _effect.tryEmit(MediaSelectorEffect.RequestCameraPermission)
        }
    }

    private fun tryOpenCamera() {
        if (permissionsUtils.hasCameraPermission()) {
            launch { takePicture() }
        } else {
            _effect.tryEmit(MediaSelectorEffect.ShowCameraPermissionsDeniedDialog)
        }
    }

    private suspend fun takePicture() {
        val uri = createPhotoFile()
        if (uri != null) {
            lockAuthorizer.skipNextLock()
            _effect.tryEmit(MediaSelectorEffect.TakePicture(uri))
        } else {
            _effect.tryEmit(
                MediaSelectorEffect.ShowMessage(
                    resourcesManager.getString(uiR.string.general_error)
                ),
            )
        }
    }

    private suspend fun createPhotoFile(): Uri? {
        val mediaId = UUID.randomUUID().leastSignificantBits
        val file = mediaRepository.createTempMediaFile(
            name = "$mediaId${MediaRepository.CAMERA_PICTURE_NAME}",
        )
        return if (file != null) {
            val uri = mediaRepository.getRelativeUri(file)
            val image = MediaItem.Image(
                id = mediaId,
                name = MediaRepository.CAMERA_PICTURE_NAME,
                uri = uri,
                ratio = 1f,
                album = null,
                state = SelectionState.Default,
                isCameraItem = true,
            )
            cachePhoto = image
            cachedPhotoFile = file
            uri
        } else {
            null
        }
    }

    private fun onTakePictureResult(isSuccess: Boolean) = launch {
        lockAuthorizer.cancelSkipNextLock()
        val image = cachePhoto
        val file = cachedPhotoFile
        if (isSuccess && image != null && file != null) {
            val imageWithRatio = image.copy(ratio = file.getImageRatio())
            cachePhoto = imageWithRatio
            toggleItemSelection(imageWithRatio)
            if (isSingleChoice) {
                onSendClick()
            }
        } else {
            deletePhotoFile()
        }
        cachePhoto = null
        cachedPhotoFile = null
    }

    private suspend fun deletePhotoFile() {
        cachePhoto?.let { mediaRepository.deleteTempMediaFile(name = "${it.id}${it.name}") }
    }

    private fun File.getImageRatio(): Float {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeFile(absolutePath, options)

        val exif = ExifInterface(absolutePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL,
        )

        val (width, height) = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90,
            ExifInterface.ORIENTATION_ROTATE_270,
            ExifInterface.ORIENTATION_TRANSPOSE,
            ExifInterface.ORIENTATION_TRANSVERSE,
                -> options.outHeight to options.outWidth

            else -> options.outWidth to options.outHeight
        }

        return width.toFloat() / height
    }
}