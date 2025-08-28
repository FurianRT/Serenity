package com.furianrt.storage.internal.database.notes.mappers

import com.furianrt.domain.entities.NoteLocation
import com.furianrt.storage.internal.database.notes.entities.EntryNoteLocation

internal fun EntryNoteLocation.toNoteLocation() = NoteLocation(
    id = id,
    title = title,
    longitude = longitude,
    latitude = latitude,
)

internal fun NoteLocation.toEntryNoteLocation(noteId: String) = EntryNoteLocation(
    id = id,
    noteId = noteId,
    title = title,
    longitude = longitude,
    latitude = latitude,
)
