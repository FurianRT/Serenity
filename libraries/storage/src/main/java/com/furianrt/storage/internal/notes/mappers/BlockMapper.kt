package com.furianrt.storage.internal.notes.mappers

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.internal.notes.entities.EntryContentBlock
import com.furianrt.storage.internal.notes.entities.LinkedContentBlock

internal fun LinkedContentBlock.toLocalNoteContent(): LocalNote.Content = when {
    images.isNotEmpty() -> toImagesBlock()
    else -> throw IllegalStateException("Block should not be empty")
}

internal fun LocalNote.Content.ImagesBlock.toEntryContentBlock(noteId: String) = EntryContentBlock(
    id = id,
    noteId = noteId,
    position = position,
)
