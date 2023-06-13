package com.furianrt.storage.internal.notes.extensions

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.internal.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.notes.entities.EntryNoteTitle
import com.furianrt.storage.internal.notes.entities.LinkedNote

internal fun LinkedNote.toLocalNote() = LocalNote(
    id = note.id,
    timestamp = note.timestamp,
    tags = tags.map(EntryNoteTag::toNoteContentTag),
    content = buildList {
        val posToContent = mutableMapOf<Int, LocalNote.Content>()
        titles.forEach { posToContent[it.blockPosition] = it.toNoteContentTitle() }
        images.forEach { posToContent[it.blockPosition] = it.toNoteContentImage() }
        addAll(posToContent.values)
    },
)

private fun EntryNoteTitle.toNoteContentTitle() = LocalNote.Content.Title(
    id = id,
    text = text,
)

private fun EntryNoteImage.toNoteContentImage() = LocalNote.Content.Image(
    id = id,
    uri = uri,
)

private fun EntryNoteTag.toNoteContentTag() = LocalNote.Tag(
    id = id,
    title = title,
)
