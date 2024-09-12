package com.furianrt.mediaselector.internal.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.furianrt.core.hasItem
import com.furianrt.core.mapImmutable
import com.furianrt.core.updateState
import com.furianrt.mediaselector.internal.ui.MediaSelectorEffect.*
import com.furianrt.mediaselector.internal.ui.MediaSelectorEvent.*
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import com.furianrt.mediaselector.internal.ui.extensions.toMediaItem
import com.furianrt.mediaselector.internal.ui.extensions.toMediaItems
import com.furianrt.mediaselector.internal.ui.extensions.toMediaSelectorResult
import com.furianrt.storage.api.entities.DeviceMedia
import com.furianrt.storage.api.repositories.MediaRepository
import com.furianrt.storage.api.repositories.hasPartialMediaAccess
import com.furianrt.storage.api.repositories.mediaAccessDenied
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
internal class MediaSelectorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val dialogResultCoordinator: DialogResultCoordinator,
    private val mediaRepository: MediaRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<MediaSelectorUiState>(MediaSelectorUiState.Loading)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaSelectorEffect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    private val dialogIdentifier by lazy(LazyThreadSafetyMode.NONE) {
        DialogIdentifier(
            requestId = savedStateHandle["requestId"]!!,
            dialogId = savedStateHandle["dialogId"]!!,
        )
    }

    private val selectedItems = mutableListOf<MediaItem>()

    init {
        loadMediaItems()
    }

    fun onEvent(event: MediaSelectorEvent) {
        when (event) {
            is OnPartialAccessMessageClick -> _effect.tryEmit(RequestMediaPermissions)
            is OnMediaPermissionsSelected -> loadMediaItems()
            is OnSelectItemClick -> toggleItemSelection(event.item)
            is OnSendClick -> saveMedia()
        }
    }

    private fun loadMediaItems() {
        if (mediaRepository.mediaAccessDenied()) {
            _effect.tryEmit(CloseScreen)
            return
        }
        launch {
            val media = mediaRepository.getDeviceMediaList()
            _state.update { currentState ->
                when {
                    media.isEmpty() -> MediaSelectorUiState.Empty(
                        showPartialAccessMessage = mediaRepository.hasPartialMediaAccess(),
                    )

                    currentState is MediaSelectorUiState.Success -> {
                        selectedItems.removeAll { !media.hasItem { item -> item.id == it.id } }
                        MediaSelectorUiState.Success(
                            items = media.toMediaItems(
                                state = { id ->
                                    val selectedIndex = selectedItems.indexOfFirst { it.id == id }
                                    if (selectedIndex != -1) {
                                        SelectionState.Selected(order = selectedIndex + 1)
                                    } else {
                                        SelectionState.Default
                                    }
                                }
                            ),
                            selectedCount = selectedItems.count(),
                            showPartialAccessMessage = mediaRepository.hasPartialMediaAccess(),
                        )
                    }

                    else -> MediaSelectorUiState.Success(
                        items = media.mapImmutable(DeviceMedia::toMediaItem),
                        selectedCount = 0,
                        showPartialAccessMessage = mediaRepository.hasPartialMediaAccess(),
                    )
                }
            }
        }
    }

    private fun toggleItemSelection(item: MediaItem) {
        when (item.state) {
            is SelectionState.Default -> selectedItems.add(item)
            is SelectionState.Selected -> selectedItems.removeAll { it.id == item.id }
        }
        _state.updateState<MediaSelectorUiState.Success> { currentState ->
            currentState.setSelectedItems(selectedItems)
        }
    }

    private fun saveMedia() {
        dialogResultCoordinator.onDialogResult(
            dialogIdentifier = dialogIdentifier,
            code = DialogResult.Ok(data = selectedItems.toMediaSelectorResult()),
        )
        _effect.tryEmit(CloseScreen)
    }
}