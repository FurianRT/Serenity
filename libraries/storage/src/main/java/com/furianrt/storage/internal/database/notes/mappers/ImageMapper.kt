package com.furianrt.storage.internal.database.notes.mappers

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.LinkedContentBlock

internal fun LinkedContentBlock.toImagesBlock() = LocalNote.Content.ImagesBlock(
    id = block.id,
    position = block.position,
    images = images.sortedBy(EntryNoteImage::position).map(EntryNoteImage::toNoteContentImage),
)

internal fun EntryNoteImage.toNoteContentImage() = LocalNote.Content.Image(
    id = id,
    uri = uri,
    ratio = ratio,
    position = position,
)

internal fun LocalNote.Content.Image.toEntryNoteImage(blockId: String) = EntryNoteImage(
    id = id,
    blockId = blockId,
    uri = uri,
    ratio = ratio,
    position = position,
)
