package com.furianrt.noteview.internal.ui.extensions

import com.furianrt.core.mapImmutable
import com.furianrt.mediaselector.api.entities.MediaSelectorResult
import com.furianrt.notecontent.entities.UiNoteContent.MediaBlock
import com.furianrt.notecontent.extensions.toRegularUiNoteTag
import com.furianrt.notecontent.extensions.toUiNoteContent
import com.furianrt.noteview.internal.ui.entites.ContainerScreenNote
import com.furianrt.noteview.internal.ui.entites.NoteViewScreenNote
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.uikit.extensions.toDateString
import java.util.UUID

internal fun LocalNote.toNoteViewScreenNote() = NoteViewScreenNote(
    id = id,
    timestamp = timestamp,
    tags = tags.mapImmutable { it.toRegularUiNoteTag(isRemovable = false) },
    content = content.mapImmutable(LocalNote.Content::toUiNoteContent),
)

internal fun LocalNote.toContainerScreenNote() = ContainerScreenNote(
    id = id,
    date = timestamp.toDateString(),
)

internal fun MediaSelectorResult.toMediaBlock(showLoading: Boolean = false) = MediaBlock(
    id = UUID.randomUUID().toString(),
    media = media.mapImmutable(MediaSelectorResult.Media::toMediaBlockMedia),
    showLoading = showLoading,
)

private fun MediaSelectorResult.Media.toMediaBlockMedia(): MediaBlock.Media = when (this) {
    is MediaSelectorResult.Media.Image -> MediaBlock.Image(
        id = UUID.randomUUID().toString(),
        uri = uri,
        ratio = ratio,
        addedTime = System.currentTimeMillis(),
    )

    is MediaSelectorResult.Media.Video -> MediaBlock.Video(
        id = UUID.randomUUID().toString(),
        uri = uri,
        ratio = ratio,
        addedTime = System.currentTimeMillis(),
        duration = duration,
    )
}
