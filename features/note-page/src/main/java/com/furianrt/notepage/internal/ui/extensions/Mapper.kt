package com.furianrt.notepage.internal.ui.extensions

import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.dp
import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.NoteFontFamily
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

internal suspend fun LocalNote.toNoteItem(
    appFont: NoteFontFamily,
    stickerIconProvider: suspend (typeId: String) -> Int,
) = NoteItem(
    id = id,
    tags = tags.mapImmutable(LocalNote.Tag::toRegularUiNoteTag),
    stickers = stickers.mapImmutable { it.toStickerItem(stickerIconProvider(it.typeId)) },
    content = content.mapImmutable { item ->
        item.toUiNoteContent((fontFamily ?: appFont).toUiNoteFontFamily())
    },
    fontColor = fontColor?.toUiNoteFontColor(),
    fontFamily = fontFamily?.toUiNoteFontFamily(),
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
    progressState = UiNoteContent.Voice.ProgressState(),
)

internal fun StickerItem.toLocalNoteSticker() = LocalNote.Sticker(
    id = id,
    typeId = typeId,
    scale = state.scale,
    rotation = state.rotation,
    isFlipped = state.isFlipped,
    biasX = state.biasX,
    dpOffsetY = state.dpOffsetY.value,
    editTime = state.editTime,
)

private fun LocalNote.Sticker.toStickerItem(@DrawableRes icon: Int) = StickerItem(
    id = id,
    typeId = typeId,
    icon = icon,
    state = StickerState(
        initialScale = scale,
        initialRotation = rotation,
        initialIsFlipped = isFlipped,
        initialBiasX = biasX,
        initialDpOffsetY = dpOffsetY.dp,
        initialEditTime = editTime,
        initialIsEditing = false,
    ),
)

private fun MediaResult.Media.toMediaBlockMedia(): MediaBlock.Media = when (this) {
    is MediaResult.Media.Image -> MediaBlock.Image(
        id = UUID.randomUUID().toString(),
        name = name,
        uri = uri,
        ratio = ratio,
        addedDate = ZonedDateTime.now(),
    )

    is MediaResult.Media.Video -> MediaBlock.Video(
        id = UUID.randomUUID().toString(),
        name = name,
        uri = uri,
        ratio = ratio,
        addedDate = ZonedDateTime.now(),
        duration = duration,
    )
}
