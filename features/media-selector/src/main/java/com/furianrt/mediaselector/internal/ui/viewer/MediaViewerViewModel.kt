package com.furianrt.mediaselector.internal.ui.viewer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.core.mapImmutable
import com.furianrt.core.updateState
import com.furianrt.domain.entities.DeviceMedia
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.mediaselector.internal.domain.SelectedMediaCoordinator
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import com.furianrt.mediaselector.internal.ui.extensions.toMediaItem
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.DialogResult
import com.furianrt.uikit.utils.DialogResultCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class MediaViewerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mediaRepository: MediaRepository,
    private val dialogResultCoordinator: DialogResultCoordinator,
    private val mediaCoordinator: SelectedMediaCoordinator,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<MediaViewerRoute>()

    private val _state = MutableStateFlow<MediaViewerUiState>(MediaViewerUiState.Loading)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaViewerEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    private var hasSelectionChanged = false

    init {
        loadMedia()
    }

    override fun onCleared() {
        if (hasSelectionChanged) {
            dialogResultCoordinator.onDialogResult(
                dialogIdentifier = DialogIdentifier(
                    requestId = route.requestId,
                    dialogId = route.dialogId,
                ),
                code = DialogResult.Ok(data = Unit),
            )
        }
    }

    fun onEvent(event: MediaViewerEvent) {
        when (event) {
            is MediaViewerEvent.OnButtonBackClick -> {
                _effect.tryEmit(MediaViewerEffect.CloseScreen)
            }

            is MediaViewerEvent.OnMediaSelectionToggle -> {
                toggleItemSelection(event.media)
            }
        }
    }

    private fun loadMedia() = launch {
        val media = mediaRepository.getDeviceMediaList()
        _state.update {
            MediaViewerUiState.Success(
                media = media.mapImmutable(DeviceMedia::toMediaItem),
                initialMediaIndex = media.indexOfFirstOrNull { it.id == route.mediaId } ?: 0,
            ).setSelectedItems(mediaCoordinator.getSelectedMedia())
        }
    }

    private fun toggleItemSelection(item: MediaItem) {
        hasSelectionChanged = true
        when (item.state) {
            is SelectionState.Default -> mediaCoordinator.selectMedia(item)
            is SelectionState.Selected -> mediaCoordinator.unselectMedia(item)
        }
        _state.updateState<MediaViewerUiState.Success> { currentState ->
            currentState.setSelectedItems(mediaCoordinator.getSelectedMedia())
        }
    }
}
