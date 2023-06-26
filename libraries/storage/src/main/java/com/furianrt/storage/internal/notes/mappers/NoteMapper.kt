package com.furianrt.storage.internal.notes.mappers

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.entities.LocalSimpleNote
import com.furianrt.storage.internal.notes.entities.EntryNote
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

internal fun LocalNote.toEntryNote() = EntryNote(
    id = id,
    timestamp = timestamp,
)

internal fun LocalSimpleNote.toEntryNote() = EntryNote(
    id = id,
    timestamp = timestamp,
)
