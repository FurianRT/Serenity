package com.furianrt.storage.internal.database.notes.mappers

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.internal.database.notes.entities.EntryContentBlock
import com.furianrt.storage.internal.database.notes.entities.LinkedContentBlock

internal fun LinkedContentBlock.toLocalNoteContent(): LocalNote.Content = when {
    images.isNotEmpty() -> toMediaBlock()
    else -> throw IllegalStateException("Block should not be empty")
}

internal fun LocalNote.Content.MediaBlock.toEntryContentBlock(noteId: String) = EntryContentBlock(
    id = id,
    noteId = noteId,
    position = position,
)
