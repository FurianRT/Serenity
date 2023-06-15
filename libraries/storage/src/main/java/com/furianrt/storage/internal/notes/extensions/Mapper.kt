package com.furianrt.storage.internal.notes.extensions

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.entities.LocalSimpleNote
import com.furianrt.storage.internal.notes.entities.EntryNote
import com.furianrt.storage.internal.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.notes.entities.EntryNoteTitle
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
        addAll(titles.toTitlesBlocks())
        addAll(images.toImagesBlocks())
        sortBy(LocalNote.Content::position)
    },
)

private fun List<EntryNoteTitle>.toTitlesBlocks() = groupBy(EntryNoteTitle::blockPosition)
    .map { entry ->
        LocalNote.Content.TitlesBlock(
            position = entry.key,
            titles = entry.value.map(EntryNoteTitle::toNoteContentTitle),
        )
    }

private fun List<EntryNoteImage>.toImagesBlocks() = groupBy(EntryNoteImage::blockPosition)
    .map { entry ->
        LocalNote.Content.ImagesBlock(
            position = entry.key,
            titles = entry.value.map(EntryNoteImage::toNoteContentImage),
        )
    }

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
