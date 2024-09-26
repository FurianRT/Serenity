package com.furianrt.notepage.internal.ui.extensions

import com.furianrt.core.mapImmutable
import com.furianrt.mediaselector.api.entities.MediaSelectorResult
import com.furianrt.notepage.internal.ui.entities.NoteItem
import com.furianrt.domain.entities.LocalNote
import com.furianrt.notecontent.entities.UiNoteContent.MediaBlock
import com.furianrt.notecontent.extensions.toRegularUiNoteTag
import com.furianrt.notecontent.extensions.toUiNoteContent
import java.util.UUID

internal fun LocalNote.toNoteItem() = NoteItem(
    id = id,
    tags = tags.mapImmutable { it.toRegularUiNoteTag(isRemovable = false) },
    content = content.mapImmutable(LocalNote.Content::toUiNoteContent),
)

internal fun MediaSelectorResult.toMediaBlock() = MediaBlock(
    id = UUID.randomUUID().toString(),
    media = media.mapImmutable(MediaSelectorResult.Media::toMediaBlockMedia),
)

private fun MediaSelectorResult.Media.toMediaBlockMedia(): MediaBlock.Media = when (this) {
    is MediaSelectorResult.Media.Image -> MediaBlock.Image(
        name = UUID.randomUUID().toString() + name,
        uri = uri,
        ratio = ratio,
        addedTime = System.currentTimeMillis(),
    )

    is MediaSelectorResult.Media.Video -> MediaBlock.Video(
        name = UUID.randomUUID().toString() + name,
        uri = uri,
        ratio = ratio,
        addedTime = System.currentTimeMillis(),
        duration = duration,
    )
}
