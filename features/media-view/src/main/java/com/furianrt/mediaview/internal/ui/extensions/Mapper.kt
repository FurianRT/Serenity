package com.furianrt.mediaview.internal.ui.extensions

import com.furianrt.mediaview.internal.ui.entities.MediaItem
import com.furianrt.storage.api.entities.LocalNote

internal fun LocalNote.Content.Media.toMediaItem() = when (this) {
    is LocalNote.Content.Image -> toImageItem()
    is LocalNote.Content.Video -> toVideoItem()
}

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
