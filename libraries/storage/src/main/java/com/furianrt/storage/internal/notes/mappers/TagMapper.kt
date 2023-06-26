package com.furianrt.storage.internal.notes.mappers

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.internal.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.notes.entities.EntryNoteToTag

internal fun EntryNoteTag.toNoteContentTag() = LocalNote.Tag(
    id = id,
    title = title,
)

internal fun LocalNote.Tag.toEntryNoteTag() = EntryNoteTag(
    id = id,
    title = title,
)

internal fun LocalNote.Tag.toEntryNoteToTag(noteId: String) = EntryNoteToTag(
    noteId = noteId,
    tagId = id,
)
