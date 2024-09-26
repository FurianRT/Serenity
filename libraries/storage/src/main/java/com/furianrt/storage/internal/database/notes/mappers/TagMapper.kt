package com.furianrt.storage.internal.database.notes.mappers

import com.furianrt.domain.entities.LocalNote
import com.furianrt.storage.internal.database.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.database.notes.entities.EntryNoteToTag

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
