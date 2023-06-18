package com.furianrt.notecontent.extensions

import com.furianrt.core.mapImmutable
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.storage.api.entities.LocalNote

fun LocalNote.Content.toUiNoteContent() = when (this) {
    is LocalNote.Content.Title -> toUiNoteContentTitle()
    is LocalNote.Content.ImagesBlock -> toUiMediaBlock()
}

fun LocalNote.Content.ImagesBlock.toUiMediaBlock() = UiNoteContent.MediaBlock(
    id = id,
    position = position,
    images = titles.mapImmutable(LocalNote.Content.Image::toUiNoteContentImage),
)

fun LocalNote.Content.Title.toUiNoteContentTitle() = UiNoteContent.Title(
    id = id,
    position = position,
    text = text,
)

fun LocalNote.Content.Image.toUiNoteContentImage() = UiNoteContent.Image(
    id = id,
    uri = uri,
)

fun LocalNote.Tag.toRegularUiNoteTag(isRemovable: Boolean) = UiNoteTag.Regular(
    id = id,
    title = title,
    isRemovable = isRemovable,
)

fun LocalNote.Tag.toTemplateUiNoteTag() = UiNoteTag.Template(
    id = id,
    title = title,
)
