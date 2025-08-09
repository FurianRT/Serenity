package com.furianrt.toolspanel.internal.ui.stickers

import androidx.compose.runtime.Immutable
import com.furianrt.toolspanel.api.entities.Sticker
import com.furianrt.toolspanel.internal.entities.StickerPack
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
internal data class StickersPanelUiState(
    val packs: ImmutableList<StickerPack> = persistentListOf(),
    val selectedPackIndex: Int = 0,
)

internal sealed interface StickersPanelEvent {
    data object OnCloseClick : StickersPanelEvent
    data class OnTitleStickerPackClick(val index: Int) : StickersPanelEvent
    data class OnStickersPageChange(val index: Int) : StickersPanelEvent
    data class OnStickerSelected(val sticker: Sticker) : StickersPanelEvent
    data object OnKeyboardClick : StickersPanelEvent
}

internal sealed interface StickersPanelEffect {
    data object ClosePanel : StickersPanelEffect
    data class ScrollContentToIndex(val index: Int) : StickersPanelEffect
    data class SelectSticker(val sticker: Sticker) : StickersPanelEffect
    data object ShowKeyboard : StickersPanelEffect
}
