package com.furianrt.storage.internal.database.notes.mappers

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVideo
import com.furianrt.storage.internal.database.notes.entities.LinkedContentBlock

internal fun LinkedContentBlock.toMediaBlock(): LocalNote.Content.MediaBlock {
    val images = images.map(EntryNoteImage::toNoteContentImage)
    val videos = videos.map(EntryNoteVideo::toNoteContentVideo)
    return LocalNote.Content.MediaBlock(
        id = block.id,
        position = block.position,
        media = (images + videos).sortedBy(LocalNote.Content.Media::position)
    )
}

internal fun EntryNoteImage.toNoteContentImage() = LocalNote.Content.Image(
    id = id,
    uri = uri,
    ratio = ratio,
    position = position,
)

internal fun EntryNoteVideo.toNoteContentVideo() = LocalNote.Content.Video(
    id = id,
    uri = uri,
    ratio = ratio,
    position = position,
    duration = duration,
)

internal fun LocalNote.Content.Image.toEntryImage(blockId: String) = EntryNoteImage(
    id = id,
    blockId = blockId,
    uri = uri,
    ratio = ratio,
    position = position,
)

internal fun LocalNote.Content.Video.toEntryVideo(blockId: String) = EntryNoteVideo(
    id = id,
    blockId = blockId,
    uri = uri,
    ratio = ratio,
    position = position,
    duration = duration,
)
