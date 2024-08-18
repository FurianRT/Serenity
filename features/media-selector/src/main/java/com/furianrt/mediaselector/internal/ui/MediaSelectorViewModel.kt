package com.furianrt.mediaselector.internal.ui

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.furianrt.core.mapImmutable
import com.furianrt.mediaselector.internal.ui.extensions.toMediaItem
import com.furianrt.storage.api.entities.DeviceMedia
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

    private val _state = MutableStateFlow<MediaSelectorUiState>(MediaSelectorUiState.Loading)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaSelectorEffect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() = launch {
        val items = deviceMediaRepository.getMediaList().mapImmutable(DeviceMedia::toMediaItem)
        if (items.isEmpty()) {
            _state.update { MediaSelectorUiState.Empty }
        } else {
            _state.update { MediaSelectorUiState.Success(items, LazyGridState()) }
        }
    }
}