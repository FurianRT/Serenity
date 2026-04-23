package com.furianrt.storage.internal.database.notes.mappers

import com.furianrt.domain.entities.CustomSticker
import com.furianrt.domain.entities.LocalNote
import com.furianrt.storage.internal.database.notes.entities.EntryCustomSticker
import com.furianrt.storage.internal.database.notes.entities.EntryNoteSticker
import com.furianrt.storage.internal.database.notes.entities.PartStickerId
import com.furianrt.storage.internal.database.notes.entities.PartStickerTransformations

internal fun EntryNoteSticker.toNoteContentSticker() = LocalNote.Sticker(
    id = id,
    typeId = typeId,
    scale = scale,
    rotation = rotation,
    isFlipped = isFlipped,
    biasX = biasX,
    dpOffsetY = dpOffsetY,
    editTime = editTime,
)

internal fun LocalNote.Sticker.toEntryNoteToSticker(noteId: String) = EntryNoteSticker(
    id = id,
    noteId = noteId,
    typeId = typeId,
    scale = scale,
    rotation = rotation,
    isFlipped = isFlipped,
    biasX = biasX,
    dpOffsetY = dpOffsetY,
    editTime = editTime,
)

internal fun LocalNote.Sticker.toEntryTransformationsPart() = PartStickerTransformations(
    id = id,
    scale = scale,
    rotation = rotation,
    isFlipped = isFlipped,
    biasX = biasX,
    dpOffsetY = dpOffsetY,
    editTime = editTime,
)

internal fun LocalNote.Sticker.toEntryIdPart() = PartStickerId(
    id = id,
)

internal fun EntryCustomSticker.toCustomSticker() = CustomSticker(
    id = id,
    name = name,
    uri = uri,
    ratio = ratio,
    addedDate = addedDate,
    isHidden = isHidden,
)

internal fun CustomSticker.toEntryCustomSticker(isSaved: Boolean) = EntryCustomSticker(
    id = id,
    name = name,
    uri = uri,
    ratio = ratio,
    addedDate = addedDate,
    isSaved = isSaved,
    isHidden = isHidden,
)
