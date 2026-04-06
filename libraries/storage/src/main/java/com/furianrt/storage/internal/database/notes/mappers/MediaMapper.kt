package com.furianrt.storage.internal.database.notes.mappers

import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.NoteCustomBackground
import com.furianrt.storage.internal.database.notes.entities.EntryNoteCustomBackground
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVideo
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVoice

internal fun EntryNoteImage.toNoteContentImage() = LocalNote.Content.Image(
    id = id,
    name = name,
    uri = uri,
    ratio = ratio,
    addedDate = addedDate,
)

internal fun EntryNoteVideo.toNoteContentVideo() = LocalNote.Content.Video(
    id = id,
    name = name,
    uri = uri,
    ratio = ratio,
    duration = duration,
    addedDate = addedDate,
)

internal fun EntryNoteVoice.toNoteContentVoice() = LocalNote.Content.Voice(
    id = id,
    uri = uri,
    duration = duration,
    volume = volume,
)

internal fun LocalNote.Content.Image.toEntryImage(
    noteId: String,
    isSaved: Boolean,
) = EntryNoteImage(
    id = id,
    name = name,
    noteId = noteId,
    uri = uri,
    ratio = ratio,
    addedDate = addedDate,
    isSaved = isSaved,
)

internal fun LocalNote.Content.Video.toEntryVideo(
    noteId: String,
    isSaved: Boolean,
) = EntryNoteVideo(
    id = id,
    name = name,
    noteId = noteId,
    uri = uri,
    ratio = ratio,
    duration = duration,
    addedDate = addedDate,
    isSaved = isSaved,
)

internal fun LocalNote.Content.Voice.toEntryVoice(noteId: String) = EntryNoteVoice(
    id = id,
    noteId = noteId,
    uri = uri,
    duration = duration,
    volume = volume,
)

internal fun NoteCustomBackground.toEntry(isSaved: Boolean) = EntryNoteCustomBackground(
    id = id,
    name = name,
    uri = uri,
    addedDate = addedDate,
    isSaved = isSaved,
    primaryColor = primaryColor,
    accentColor = accentColor,
    isLight = isLight,
)

internal fun EntryNoteCustomBackground.toDomain() = NoteCustomBackground(
    id = id,
    name = name,
    uri = uri,
    addedDate = addedDate,
    primaryColor = primaryColor,
    accentColor = accentColor,
    isLight = isLight,
)
