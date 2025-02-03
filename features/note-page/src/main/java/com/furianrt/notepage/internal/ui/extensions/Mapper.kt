package com.furianrt.notepage.internal.ui.extensions

import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.LocalNote
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteContent.MediaBlock
import com.furianrt.notelistui.extensions.toRegularUiNoteTag
import com.furianrt.notelistui.extensions.toUiNoteContent
import com.furianrt.notelistui.extensions.toUiNoteFontColor
import com.furianrt.notelistui.extensions.toUiNoteFontFamily
import com.furianrt.notepage.internal.ui.page.entities.NoteItem
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import com.furianrt.notepage.internal.ui.stickers.StickerState
import com.furianrt.toolspanel.api.VoiceRecord
import kotlinx.collections.immutable.toImmutableList
import java.time.ZonedDateTime
import java.util.UUID

internal fun LocalNote.toNoteItem() = NoteItem(
    id = id,
    tags = tags.mapImmutable(LocalNote.Tag::toRegularUiNoteTag),
    stickers = stickers.mapImmutable(LocalNote.Sticker::toStickerItem),
    content = content.mapImmutable(LocalNote.Content::toUiNoteContent),
    fontColor = fontColor.toUiNoteFontColor(),
    fontFamily = fontFamily.toUiNoteFontFamily(),
    fontSize = fontSize,
)

internal fun MediaResult.toMediaBlock() = MediaBlock(
    id = UUID.randomUUID().toString(),
    media = media.mapImmutable(MediaResult.Media::toMediaBlockMedia),
)

internal fun VoiceRecord.toUiVoice() = UiNoteContent.Voice(
    id = id,
    uri = uri,
    duration = duration.toLong(),
    volume = volume.toImmutableList(),
    progress = 0f,
)

internal fun StickerItem.toLocalNoteSticker() = LocalNote.Sticker(
    id = id,
    type = type,
    scale = state.scale,
    rotation = state.rotation,
    anchors = state.anchors.map(StickerState.Anchor::toLocalNoteStickerAnchor),
)

private fun LocalNote.Sticker.toStickerItem() = StickerItem(
    id = id,
    type = type,
    isEditing = false,
    state = StickerState(
        initialScale = scale,
        initialRotation = rotation,
        initialAnchors = anchors.mapImmutable(LocalNote.Sticker.Anchor::toStickerStateAnchor),
    ),
)

private fun MediaResult.Media.toMediaBlockMedia(): MediaBlock.Media = when (this) {
    is MediaResult.Media.Image -> MediaBlock.Image(
        name = UUID.randomUUID().toString() + name,
        uri = uri,
        ratio = ratio,
        addedDate = ZonedDateTime.now(),
    )

    is MediaResult.Media.Video -> MediaBlock.Video(
        name = UUID.randomUUID().toString() + name,
        uri = uri,
        ratio = ratio,
        addedDate = ZonedDateTime.now(),
        duration = duration,
    )
}

private fun StickerState.Anchor.toLocalNoteStickerAnchor() = when (this) {
    is StickerState.Anchor.Item -> LocalNote.Sticker.Anchor(
        id = id,
        biasX = biasX,
        biasY = biasY,
    )

    is StickerState.Anchor.ViewPort -> LocalNote.Sticker.Anchor(
        id = null,
        biasX = biasX,
        biasY = biasY,
    )
}

private fun LocalNote.Sticker.Anchor.toStickerStateAnchor(): StickerState.Anchor {
    val anchorId = id
    return if (anchorId == null) {
        StickerState.Anchor.ViewPort(
            biasX = biasX,
            biasY = biasY,
        )
    } else {
        StickerState.Anchor.Item(
            id = anchorId,
            biasX = biasX,
            biasY = biasY,
        )
    }
}
