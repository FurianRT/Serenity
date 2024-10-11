package com.furianrt.mediaselector.internal.ui.selector

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.furianrt.core.hasItem
import com.furianrt.core.mapImmutable
import com.furianrt.core.updateState
import com.furianrt.domain.entities.DeviceMedia
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.mediaselector.api.MediaSelectorRoute
import com.furianrt.mediaselector.internal.domain.SelectedMediaCoordinator
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEffect.*
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.*
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import com.furianrt.mediaselector.internal.ui.extensions.toMediaItem
import com.furianrt.mediaselector.internal.ui.extensions.toMediaItems
import com.furianrt.mediaselector.internal.ui.extensions.toMediaSelectorResult
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.DialogResult
import com.furianrt.uikit.utils.DialogResultCoordinator
import com.furianrt.uikit.utils.DialogResultListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val TAG = "MediaSelectorViewModel"
private const val MEDIA_VIEWER_DIALOG_ID = 1

@HiltViewModel
internal class MediaSelectorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dialogResultCoordinator: DialogResultCoordinator,
    private val mediaRepository: MediaRepository,
    private val permissionsUtils: PermissionsUtils,
    private val mediaCoordinator: SelectedMediaCoordinator,
) : ViewModel(), DialogResultListener {

    private val _state = MutableStateFlow<MediaSelectorUiState>(MediaSelectorUiState.Loading)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaSelectorEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    private val route = savedStateHandle.toRoute<MediaSelectorRoute>()

    init {
        dialogResultCoordinator.addDialogResultListener(requestId = TAG, listener = this)
        loadMediaItems()
    }

    override fun onCleared() {
        dialogResultCoordinator.removeDialogResultListener(requestId = TAG, listener = this)
        mediaCoordinator.close()
    }

    override fun onDialogResult(dialogId: Int, result: DialogResult) {
        when (dialogId) {
            MEDIA_VIEWER_DIALOG_ID -> if (result is DialogResult.Ok<*>) {
                _state.updateState<MediaSelectorUiState.Success> { currentState ->
                    currentState.setSelectedItems(mediaCoordinator.getSelectedMedia())
                }
            }
        }
    }

    fun onEvent(event: MediaSelectorEvent) {
        when (event) {
            is OnPartialAccessMessageClick -> _effect.tryEmit(RequestMediaPermissions)
            is OnMediaPermissionsSelected -> loadMediaItems()
            is OnSelectItemClick -> toggleItemSelection(event.item)
            is OnSendClick -> saveMedia()
            is OnMediaClick -> _effect.tryEmit(
                OpenMediaViewer(
                    dialogId = MEDIA_VIEWER_DIALOG_ID,
                    requestId = TAG,
                    mediaId = event.id,
                )
            )
        }
    }

    private fun loadMediaItems() {
        if (permissionsUtils.mediaAccessDenied()) {
            _effect.tryEmit(CloseScreen)
            return
        }
        launch {
            val media = mediaRepository.getDeviceMediaList()
            _state.update { currentState ->
                when {
                    media.isEmpty() -> MediaSelectorUiState.Empty(
                        showPartialAccessMessage = permissionsUtils.hasPartialMediaAccess(),
                    )

                    currentState is MediaSelectorUiState.Success -> {
                        mediaCoordinator.unselectMedia { !media.hasItem { item -> item.id == it.id } }
                        MediaSelectorUiState.Success(
                            items = media.toMediaItems(
                                state = { id ->
                                    val selectedIndex = mediaCoordinator.getSelectedMedia()
                                        .indexOfFirst { it.id == id }
                                    if (selectedIndex != -1) {
                                        SelectionState.Selected(order = selectedIndex + 1)
                                    } else {
                                        SelectionState.Default
                                    }
                                }
                            ),
                            selectedCount = mediaCoordinator.getSelectedMedia().count(),
                            showPartialAccessMessage = permissionsUtils.hasPartialMediaAccess(),
                        )
                    }

                    else -> MediaSelectorUiState.Success(
                        items = media.mapImmutable(DeviceMedia::toMediaItem),
                        selectedCount = 0,
                        showPartialAccessMessage = permissionsUtils.hasPartialMediaAccess(),
                    )
                }
            }
        }
    }

    private fun toggleItemSelection(item: MediaItem) {
        when (item.state) {
            is SelectionState.Default -> mediaCoordinator.selectMedia(item)
            is SelectionState.Selected -> mediaCoordinator.unselectMedia(item)
        }
        _state.updateState<MediaSelectorUiState.Success> { currentState ->
            currentState.setSelectedItems(mediaCoordinator.getSelectedMedia())
        }
    }

    private fun saveMedia() {
        dialogResultCoordinator.onDialogResult(
            dialogIdentifier = DialogIdentifier(
                dialogId = route.dialogId,
                requestId = route.requestId,
            ),
            code = DialogResult.Ok(
                data = mediaCoordinator.getSelectedMedia().toMediaSelectorResult(),
            ),
        )
        _effect.tryEmit(CloseScreen)
    }
}