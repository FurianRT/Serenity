package com.furianrt.notecontent.extensions

import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.storage.api.entities.LocalNote
import kotlinx.collections.immutable.toImmutableList

fun LocalNote.Content.toUiNoteContent() = when (this) {
    is LocalNote.Content.TitlesBlock -> toUiTitlesBlock()
    is LocalNote.Content.ImagesBlock -> toUiImagesBlock()
}

fun LocalNote.Content.TitlesBlock.toUiTitlesBlock() = UiNoteContent.TitlesBlock(
    position = position,
    titles = titles.map(LocalNote.Content.Title::toUiNoteContentTitle).toImmutableList(),
)

fun LocalNote.Content.ImagesBlock.toUiImagesBlock() = UiNoteContent.ImagesBlock(
    position = position,
    images = titles.map(LocalNote.Content.Image::toUiNoteContentImage).toImmutableList(),
)

fun LocalNote.Content.Title.toUiNoteContentTitle() = UiNoteContent.Title(
    id = id,
    text = text,
)

fun LocalNote.Content.Image.toUiNoteContentImage() = UiNoteContent.Image(
    id = id,
    uri = uri,
)

fun LocalNote.Tag.toRegularUiNoteTag() = UiNoteTag.Regular(
    id = id,
    title = title,
)

fun LocalNote.Tag.toEditableUiNoteTag() = UiNoteTag.Editable(
    id = id,
    title = title,
)

fun LocalNote.Tag.toTemplateUiNoteTag() = UiNoteTag.Template(
    id = id,
    title = title,
)
