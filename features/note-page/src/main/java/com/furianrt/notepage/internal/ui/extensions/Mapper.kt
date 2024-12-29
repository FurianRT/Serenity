package com.furianrt.notepage.internal.ui.extensions

import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.LocalNote
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.notelistui.entities.UiNoteContent.MediaBlock
import com.furianrt.notelistui.extensions.toRegularUiNoteTag
import com.furianrt.notelistui.extensions.toUiNoteContent
import com.furianrt.notelistui.extensions.toUiNoteFontColor
import com.furianrt.notelistui.extensions.toUiNoteFontFamily
import com.furianrt.notepage.internal.ui.entities.NoteItem
import java.time.ZonedDateTime
import java.util.UUID

internal fun LocalNote.toNoteItem() = NoteItem(
    id = id,
    tags = tags.mapImmutable { it.toRegularUiNoteTag(isRemovable = false) },
    content = content.mapImmutable(LocalNote.Content::toUiNoteContent),
    fontColor = fontColor.toUiNoteFontColor(),
    fontFamily = fontFamily.toUiNoteFontFamily(),
    fontSize = fontSize,
)

internal fun MediaResult.toMediaBlock() = MediaBlock(
    id = UUID.randomUUID().toString(),
    media = media.mapImmutable(MediaResult.Media::toMediaBlockMedia),
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
