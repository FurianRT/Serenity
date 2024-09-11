package com.furianrt.storage.internal.database.notes.mappers

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVideo

internal fun EntryNoteImage.toNoteContentImage() = LocalNote.Content.Image(
    id = id,
    uri = uri,
    ratio = ratio,
    addedTime = addedTime,
)

internal fun EntryNoteVideo.toNoteContentVideo() = LocalNote.Content.Video(
    id = id,
    uri = uri,
    ratio = ratio,
    duration = duration,
    addedTime = addedTime,
)

internal fun LocalNote.Content.Image.toEntryImage(noteId: String) = EntryNoteImage(
    id = id,
    noteId = noteId,
    uri = uri,
    ratio = ratio,
    addedTime = addedTime,
    isSaved = false,
)

internal fun LocalNote.Content.Video.toEntryVideo(noteId: String) = EntryNoteVideo(
    id = id,
    noteId = noteId,
    uri = uri,
    ratio = ratio,
    duration = duration,
    addedTime = addedTime,
    isSaved = false,
)
