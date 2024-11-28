package com.furianrt.mediaview.internal.ui.extensions

import com.furianrt.domain.entities.LocalMedia
import com.furianrt.domain.entities.LocalNote
import com.furianrt.mediaview.internal.ui.entities.MediaItem

internal fun LocalNote.Content.Media.toMediaItem() = when (this) {
    is LocalNote.Content.Image -> toImageItem()
    is LocalNote.Content.Video -> toVideoItem()
}

internal fun MediaItem.toLocalMedia() = LocalMedia(
    uri = uri,
    name = name,
    type = when (this) {
        is MediaItem.Image -> LocalMedia.Type.IMAGE
        is MediaItem.Video -> LocalMedia.Type.VIDEO
    }
)

private fun LocalNote.Content.Image.toImageItem() = MediaItem.Image(
    name = name,
    uri = uri,
    ratio = ratio,
)

private fun LocalNote.Content.Video.toVideoItem() = MediaItem.Video(
    name = name,
    uri = uri,
    ratio = ratio,
    duration = duration,
)
