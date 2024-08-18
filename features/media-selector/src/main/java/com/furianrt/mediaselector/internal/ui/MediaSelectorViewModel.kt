package com.furianrt.mediaselector.internal.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.furianrt.core.mapImmutable
import com.furianrt.mediaselector.internal.ui.MediaSelectorEffect.*
import com.furianrt.mediaselector.internal.ui.MediaSelectorEvent.*
import com.furianrt.mediaselector.internal.ui.MediaSelectorUiState.ScreenState
import com.furianrt.mediaselector.internal.ui.extensions.toMediaItem
import com.furianrt.storage.api.entities.DeviceMedia
import com.furianrt.storage.api.entities.MediaPermissionStatus
import com.furianrt.storage.api.repositories.DeviceMediaRepository
import com.furianrt.uikit.extensions.launch
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
    private val deviceMediaRepository: DeviceMediaRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        MediaSelectorUiState(
            showPartialAccessMessage = deviceMediaRepository.hasPartialMediaAccess(),
            screenState = ScreenState.Loading,
        )
    )
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaSelectorEffect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    init {
        loadMediaItems()
    }

    fun onEvent(event: MediaSelectorEvent) {
        when (event) {
            is OnPartialAccessMessageClick -> _effect.tryEmit(RequestMediaPermission)
            is OnMediaPermissionsSelected -> loadMediaItems()
        }
    }

    private fun loadMediaItems() = launch {
        _state.update { it.copy(screenState = ScreenState.Loading) }
        val items = deviceMediaRepository.getMediaList().mapImmutable(DeviceMedia::toMediaItem)
        _state.update { currentState ->
            if (items.isEmpty()) {
                currentState.copy(screenState = ScreenState.Empty)
            } else {
                currentState.copy(screenState = ScreenState.Success(items))
            }
        }
    }

    private fun DeviceMediaRepository.hasPartialMediaAccess(): Boolean {
        return getMediaPermissionStatus() == MediaPermissionStatus.PARTIAL_ACCESS
    }
}