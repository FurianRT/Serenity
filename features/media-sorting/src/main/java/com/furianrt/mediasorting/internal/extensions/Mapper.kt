package com.furianrt.mediasorting.internal.extensions

import com.furianrt.domain.entities.LocalNote
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediasorting.internal.ui.entities.MediaItem
import java.time.ZonedDateTime
import java.util.UUID

internal fun LocalNote.Content.Media.toMediaItem() = when (this) {
    is LocalNote.Content.Image -> toImageItem()
    is LocalNote.Content.Video -> toVideoItem()
}

internal fun MediaItem.toLocalNoteMedia(): LocalNote.Content.Media = when (this) {
    is MediaItem.Image -> toLocalNoteImage()
    is MediaItem.Video -> toLocalNoteVideo()
}

internal fun MediaResult.toMediaItems(): List<MediaItem> = media.map { result ->
    when (result) {
        is MediaResult.Media.Image -> result.toImageItem()
        is MediaResult.Media.Video -> result.toVideoItem()
    }
}

private fun MediaItem.Image.toLocalNoteImage() = LocalNote.Content.Image(
    id = id,
    name = name,
    uri = uri,
    ratio = ratio,
    addedDate = addedDate,
)

private fun MediaItem.Video.toLocalNoteVideo() = LocalNote.Content.Video(
    id = id,
    name = name,
    uri = uri,
    ratio = ratio,
    addedDate = addedDate,
    duration = duration,
)

private fun MediaResult.Media.Image.toImageItem() = MediaItem.Image(
    id = UUID.randomUUID().toString(),
    name = name,
    uri = uri,
    ratio = ratio,
    addedDate = ZonedDateTime.now(),
)

private fun MediaResult.Media.Video.toVideoItem() = MediaItem.Video(
    id = UUID.randomUUID().toString(),
    name = name,
    uri = uri,
    ratio = ratio,
    duration = duration,
    addedDate = ZonedDateTime.now(),
)

private fun LocalNote.Content.Image.toImageItem() = MediaItem.Image(
    id = id,
    name = name,
    uri = uri,
    ratio = ratio,
    addedDate = addedDate,
)

private fun LocalNote.Content.Video.toVideoItem() = MediaItem.Video(
    id = id,
    name = name,
    uri = uri,
    ratio = ratio,
    duration = duration,
    addedDate = addedDate,
)
