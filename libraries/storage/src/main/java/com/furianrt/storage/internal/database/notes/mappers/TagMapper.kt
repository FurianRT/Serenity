package com.furianrt.storage.internal.database.notes.mappers

import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.LocalTag
import com.furianrt.storage.internal.database.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.database.notes.entities.EntryNoteToTag
import com.furianrt.storage.internal.database.notes.entities.TagWithRelatedNoteIds

internal fun EntryNoteTag.toNoteContentTag() = LocalNote.Tag(
    title = title,
)

internal fun LocalNote.Tag.toEntryNoteTag() = EntryNoteTag(
    title = title,
)

internal fun LocalNote.Tag.toEntryNoteToTag(noteId: String) = EntryNoteToTag(
    noteId = noteId,
    tagTitle = title,
)

internal fun TagWithRelatedNoteIds.toLocalTag() = LocalTag(
    title = tag.title,
    noteIds = notesIds.map(EntryNoteToTag::noteId).toSet()
)
