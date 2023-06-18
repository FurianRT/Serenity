package com.furianrt.storage.internal.notes.extensions

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.entities.LocalSimpleNote
import com.furianrt.storage.internal.notes.entities.EntryNote
import com.furianrt.storage.internal.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.notes.entities.EntryNoteTitle
import com.furianrt.storage.internal.notes.entities.LinkedContentBlock
import com.furianrt.storage.internal.notes.entities.LinkedNote

internal fun EntryNote.toLocalSimpleNote() = LocalSimpleNote(
    id = id,
    timestamp = timestamp,
)

internal fun LinkedNote.toLocalNote() = LocalNote(
    id = note.id,
    timestamp = note.timestamp,
    tags = tags.map(EntryNoteTag::toNoteContentTag),
    content = buildList {
        addAll(titles.map(EntryNoteTitle::toNoteContentTitle))
        addAll(contentBlocks.map(LinkedContentBlock::toLocalNoteContent))
        sortBy(LocalNote.Content::position)
    },
)

internal fun EntryNoteTitle.toNoteContentTitle() = LocalNote.Content.Title(
    id = id,
    position = position,
    text = text,
)

internal fun EntryNoteImage.toNoteContentImage() = LocalNote.Content.Image(
    id = id,
    uri = uri,
)

internal fun EntryNoteTag.toNoteContentTag() = LocalNote.Tag(
    id = id,
    title = title,
)

internal fun LinkedContentBlock.toLocalNoteContent(): LocalNote.Content = when {
    images.isNotEmpty() -> toImagesBlock()
    else -> throw IllegalStateException("Block should not be empty")
}

private fun LinkedContentBlock.toImagesBlock() = LocalNote.Content.ImagesBlock(
    id = block.id,
    position = block.position,
    titles = images.map(EntryNoteImage::toNoteContentImage),
)
