package com.furianrt.toolspanel.internal.ui.stickers.extensions

import com.furianrt.domain.entities.CustomSticker
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.toolspanel.api.entities.Sticker
import com.furianrt.toolspanel.internal.entities.StickerPack
import com.furianrt.toolspanel.internal.ui.stickers.container.StickersContainerUiState
import java.time.ZonedDateTime
import java.util.UUID

internal fun StickerPack.toContainerPack() = StickersContainerUiState.Pack.Regular(
    id = id,
    icon = icon,
)

internal fun CustomSticker.toSticker() = Sticker(
    id = id,
    icon = Sticker.Icon.Uri(uri),
    ratio = ratio,
)

internal fun MediaResult.Media.Image.toCustomSticker() = CustomSticker(
    id = UUID.randomUUID().toString(),
    name = name,
    uri = uri,
    ratio = ratio,
    addedDate = ZonedDateTime.now(),
    isHidden = false,
)
