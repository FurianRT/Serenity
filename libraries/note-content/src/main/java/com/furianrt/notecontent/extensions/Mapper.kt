package com.furianrt.notecontent.extensions

import com.furianrt.core.mapImmutable
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.storage.api.entities.LocalNote

fun LocalNote.Content.toUiNoteContent() = when (this) {
    is LocalNote.Content.Title -> toUiNoteTitle()
    is LocalNote.Content.ImagesBlock -> toUiMediaBlock()
}

fun UiNoteContent.toLocalNoteContent() = when (this) {
    is UiNoteContent.Title -> toLocalNoteTitle()
    is UiNoteContent.MediaBlock -> toLocalMediaBlock()
}

fun LocalNote.Content.ImagesBlock.toUiMediaBlock() = UiNoteContent.MediaBlock(
    id = id,
    position = position,
    media = images.mapImmutable(LocalNote.Content.Image::toUiNoteImage),
)

fun UiNoteContent.MediaBlock.toLocalMediaBlock() = LocalNote.Content.ImagesBlock(
    id = id,
    position = position,
    images = media.map(UiNoteContent.MediaBlock.Media::toLocalMedia),
)

fun LocalNote.Content.Title.toUiNoteTitle() = UiNoteContent.Title(
    id = id,
    position = position,
    text = text,
)

fun UiNoteContent.Title.toLocalNoteTitle() = LocalNote.Content.Title(
    id = id,
    position = position,
    text = text,
)

fun LocalNote.Content.Image.toUiNoteImage() = UiNoteContent.MediaBlock.Media.Image(
    id = id,
    uri = uri,
    ratio = ratio,
    position = position,
)

fun UiNoteContent.MediaBlock.Media.Image.toLocalNoteImage() = LocalNote.Content.Image(
    id = id,
    uri = uri,
    ratio = ratio,
    position = position,
)

fun UiNoteContent.MediaBlock.Media.toLocalMedia() = LocalNote.Content.Image(
    id = id,
    uri = (this as UiNoteContent.MediaBlock.Media.Image).uri,
    ratio = ratio,
    position = position,
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
