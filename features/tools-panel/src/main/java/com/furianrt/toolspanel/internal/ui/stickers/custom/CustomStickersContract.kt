package com.furianrt.toolspanel.internal.ui.stickers.custom

import com.furianrt.mediaselector.api.MediaSelectorState
import com.furianrt.toolspanel.api.entities.Sticker

internal sealed interface CustomStickersUiState {
    data object Loading : CustomStickersUiState
    data object Empty : CustomStickersUiState
    data class Content(
        val stickers: List<Sticker>,
    ) : CustomStickersUiState
}

internal sealed interface CustomStickersEvent {
    data class OnStickerSelected(val sticker: Sticker) : CustomStickersEvent
    data object OnSelectImageClick : CustomStickersEvent
    data class OnDeleteStickerClick(val sticker: Sticker) : CustomStickersEvent
}

internal sealed interface CustomStickersEffect {
    data class SelectSticker(val sticker: Sticker) : CustomStickersEffect
    data class OpenMediaSelector(val params: MediaSelectorState.Params) : CustomStickersEffect
}