package com.furianrt.storage.internal.database.notes.mappers

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.internal.database.notes.entities.EntryNoteTitle

internal fun EntryNoteTitle.toNoteContentTitle() = LocalNote.Content.Title(
    id = id,
    position = position,
    text = text,
)

internal fun LocalNote.Content.Title.toEntryNoteTitle(noteId: String) = EntryNoteTitle(
    id = id,
    noteId = noteId,
    position = position,
    text = text,
)
