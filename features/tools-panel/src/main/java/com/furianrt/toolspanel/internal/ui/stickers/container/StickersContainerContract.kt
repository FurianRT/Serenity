package com.furianrt.toolspanel.internal.ui.stickers.container

import androidx.annotation.DrawableRes
import androidx.compose.foundation.pager.PagerState
import com.furianrt.mediaselector.api.MediaSelectorState
import com.furianrt.toolspanel.api.entities.Sticker

internal data class StickersContainerUiState(
    val noteId: String,
    val packs: List<Pack>,
    val pagerState: PagerState,
) {
    sealed class Pack(
        open val id: String,
        @param:DrawableRes open val icon: Int,
    ) {
        data class Regular(
            override val id: String,
            @param:DrawableRes override val icon: Int,
        ) : Pack(id, icon)

        data class Custom(
            @param:DrawableRes override val icon: Int,
        ) : Pack("custom", icon)
    }
}

internal sealed interface StickersContainerEvent {
    data object OnCloseClick : StickersContainerEvent
    data class OnTitleStickerPackClick(val index: Int) : StickersContainerEvent
    data object OnKeyboardClick : StickersContainerEvent
    data class OnStickerSelected(val sticker: Sticker) : StickersContainerEvent
    data class OnOpenMediaSelectorRequest(
        val params: MediaSelectorState.Params,
    ) : StickersContainerEvent
}

internal sealed interface StickersContainerEffect {
    data object ClosePanel : StickersContainerEffect
    data class ScrollContentToIndex(val index: Int) : StickersContainerEffect
    data object ShowKeyboard : StickersContainerEffect
    data class SelectSticker(val sticker: Sticker) : StickersContainerEffect
    data class OpenMediaSelector(val params: MediaSelectorState.Params) : StickersContainerEffect
}
