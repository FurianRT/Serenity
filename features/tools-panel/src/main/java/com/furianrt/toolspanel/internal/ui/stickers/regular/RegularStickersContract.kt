package com.furianrt.toolspanel.internal.ui.stickers.regular

import com.furianrt.toolspanel.api.entities.Sticker
import com.furianrt.toolspanel.internal.entities.StickerPack

internal data class RegularStickersUiState(
    val pack: StickerPack,
)

internal sealed interface RegularStickersEvent {
    data class OnStickerSelected(val sticker: Sticker) : RegularStickersEvent
}

internal sealed interface RegularStickersEffect {
    data class SelectSticker(val sticker: Sticker) : RegularStickersEffect
}
