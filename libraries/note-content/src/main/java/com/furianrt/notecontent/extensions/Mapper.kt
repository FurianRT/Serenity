package com.furianrt.notecontent.extensions

import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.storage.api.entities.LocalNote

fun LocalNote.Content.toUiNoteContent() = when (this) {
    is LocalNote.Content.Title -> toUiNoteContentTitle()
    is LocalNote.Content.Image -> toUiNoteContentImage()
}

fun LocalNote.Content.Title.toUiNoteContentTitle() = UiNoteContent.Title(
    id = id,
    text = text,
)

fun LocalNote.Content.Image.toUiNoteContentImage() = UiNoteContent.Image(
    id = id,
    uri = uri,
)

fun LocalNote.Tag.toUiNoteTag() = UiNoteTag(
    id = id,
    title = title,
)
