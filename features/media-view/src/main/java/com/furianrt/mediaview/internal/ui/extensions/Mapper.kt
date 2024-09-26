package com.furianrt.mediaview.internal.ui.extensions

import com.furianrt.domain.entities.LocalNote
import com.furianrt.mediaview.internal.ui.entities.MediaItem

internal fun LocalNote.Content.Media.toMediaItem() = when (this) {
    is LocalNote.Content.Image -> toImageItem()
    is LocalNote.Content.Video -> toVideoItem()
}

internal fun MediaItem.toLocalNoteMedia() = when (this) {
    is MediaItem.Image -> toLocalNoteImage()
    is MediaItem.Video -> toLocalNoteVideo()
}

private fun LocalNote.Content.Image.toImageItem() = MediaItem.Image(
    name = name,
    uri = uri,
    ratio = ratio,
    addedTime = addedTime,
)

private fun LocalNote.Content.Video.toVideoItem() = MediaItem.Video(
    name = name,
    uri = uri,
    ratio = ratio,
    duration = duration,
    addedTime = addedTime,
)

private fun MediaItem.Image.toLocalNoteImage() = LocalNote.Content.Image(
    name = name,
    uri = uri,
    ratio = ratio,
    addedTime = addedTime,
)

private fun MediaItem.Video.toLocalNoteVideo() = LocalNote.Content.Video(
    name = name,
    uri = uri,
    ratio = ratio,
    addedTime = addedTime,
    duration = duration,
)
