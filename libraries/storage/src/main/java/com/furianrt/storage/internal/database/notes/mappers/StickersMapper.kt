package com.furianrt.storage.internal.database.notes.mappers

import com.furianrt.domain.entities.LocalNote
import com.furianrt.storage.internal.database.notes.entities.EntryNoteSticker
import com.furianrt.storage.internal.database.notes.entities.PartStickerId
import com.furianrt.storage.internal.database.notes.entities.PartStickerTransformations

internal fun EntryNoteSticker.toNoteContentSticker() = LocalNote.Sticker(
    id = id,
    typeId = typeId,
    scale = scale,
    rotation = rotation,
    anchors = anchors.map(EntryNoteSticker.Anchor::toLocalNoteAnchor),
)

internal fun LocalNote.Sticker.toEntryNoteToSticker(noteId: String) = EntryNoteSticker(
    id = id,
    noteId = noteId,
    typeId = typeId,
    scale = scale,
    rotation = rotation,
    anchors = anchors.map(LocalNote.Sticker.Anchor::toEntryNoteStickerAnchor),
)

internal fun LocalNote.Sticker.toEntryTransformationsPart() = PartStickerTransformations(
    id = id,
    scale = scale,
    rotation = rotation,
    anchors = anchors.map(LocalNote.Sticker.Anchor::toEntryNoteStickerAnchor),
)

internal fun LocalNote.Sticker.toEntryIdPart() = PartStickerId(
    id = id,
)

private fun EntryNoteSticker.Anchor.toLocalNoteAnchor() = LocalNote.Sticker.Anchor(
    id = id,
    biasX = biasX,
    biasY = biasY,
)

private fun LocalNote.Sticker.Anchor.toEntryNoteStickerAnchor() = EntryNoteSticker.Anchor(
    id = id,
    biasX = biasX,
    biasY = biasY,
)
