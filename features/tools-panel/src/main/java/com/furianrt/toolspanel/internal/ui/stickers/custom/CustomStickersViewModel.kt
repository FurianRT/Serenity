package com.furianrt.toolspanel.internal.ui.stickers.custom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.entities.CustomSticker
import com.furianrt.domain.repositories.StickersRepository
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediaselector.api.MediaSelectorState
import com.furianrt.toolspanel.api.entities.Sticker
import com.furianrt.toolspanel.internal.ui.stickers.extensions.toCustomSticker
import com.furianrt.toolspanel.internal.ui.stickers.extensions.toSticker
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class CustomStickersViewModel @Inject constructor(
    private val stickersRepository: StickersRepository,
) : ViewModel() {

    val state: StateFlow<CustomStickersUiState> = stickersRepository.getNotHiddenCustomStickers()
        .map { buildState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CustomStickersUiState.Loading,
        )

    private val _effect = MutableSharedFlow<CustomStickersEffect>(extraBufferCapacity = 5)
    val effect = _effect.asSharedFlow()

    fun onEvent(event: CustomStickersEvent) {
        when (event) {
            is CustomStickersEvent.OnStickerSelected -> onStickerSelected(event.sticker)
            is CustomStickersEvent.OnSelectImageClick -> onSelectImageClick()
            is CustomStickersEvent.OnDeleteStickerClick -> onDeleteStickerClick(event.sticker)
        }
    }

    private fun onStickerSelected(sticker: Sticker) {
        _effect.tryEmit(CustomStickersEffect.SelectSticker(sticker))
    }

    private fun onSelectImageClick() {
        val params = MediaSelectorState.Params(
            allowVideo = false,
            onMediaSelected = ::onMediaSelected,
        )
        _effect.tryEmit(CustomStickersEffect.OpenMediaSelector(params))
    }

    private fun onDeleteStickerClick(sticker: Sticker) = launch {
        val customSticker = stickersRepository.getNotHiddenCustomStickers().first()
            .find { it.id == sticker.id }
        if (customSticker != null) {
            stickersRepository.hideCustomSticker(customSticker)
        }
    }

    private suspend fun onMediaSelected(result: MediaResult) {
        stickersRepository.upsertCustomStickers(
            stickers = result.media
                .filterIsInstance<MediaResult.Media.Image>()
                .map(MediaResult.Media.Image::toCustomSticker),
        )
    }

    private fun buildState(
        stickers: List<CustomSticker>,
    ): CustomStickersUiState = if (stickers.isEmpty()) {
        CustomStickersUiState.Empty
    } else {
        CustomStickersUiState.Content(
            stickers = stickers.map(CustomSticker::toSticker),
        )
    }
}