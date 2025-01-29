package com.furianrt.storage.internal.database.notes.mappers

import com.furianrt.domain.entities.LocalNote
import com.furianrt.storage.internal.database.notes.entities.EntryNoteSticker
import com.furianrt.storage.internal.database.notes.entities.PartStickerId
import com.furianrt.storage.internal.database.notes.entities.PartStickerTransformations

internal fun EntryNoteSticker.toNoteContentSticker() = LocalNote.Sticker(
    id = id,
    type = type,
    scale = scale,
    rotation = rotation,
    anchorId = anchorId,
    biasX = biasX,
    biasY = biasY,
)

internal fun LocalNote.Sticker.toEntryNoteToSticker(noteId: String) = EntryNoteSticker(
    id = id,
    noteId = noteId,
    type = type,
    scale = scale,
    rotation = rotation,
    anchorId = anchorId,
    biasX = biasX,
    biasY = biasY,
)

internal fun LocalNote.Sticker.toEntryTransformationsPart() = PartStickerTransformations(
    id = id,
    scale = scale,
    rotation = rotation,
    anchorId = anchorId,
    biasX = biasX,
    biasY = biasY,
)

internal fun LocalNote.Sticker.toEntryIdPart() = PartStickerId(
    id = id,
)
