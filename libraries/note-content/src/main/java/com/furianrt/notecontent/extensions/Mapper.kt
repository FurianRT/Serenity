package com.furianrt.notecontent.extensions

import androidx.compose.foundation.text.input.TextFieldState
import com.furianrt.core.mapImmutable
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.storage.api.entities.LocalNote

fun UiNoteContent.toLocalNoteContent() = when (this) {
    is UiNoteContent.Title -> toLocalNoteTitle()
    is UiNoteContent.MediaBlock -> toLocalMediaBlock()
}

fun UiNoteContent.MediaBlock.toLocalMediaBlock() = LocalNote.Content.MediaBlock(
    id = id,
    media = media.map(UiNoteContent.MediaBlock.Media::toLocalMedia),
)

fun UiNoteContent.Title.toLocalNoteTitle() = LocalNote.Content.Title(
    id = id,
    text = state.text.toString(),
)

fun UiNoteContent.MediaBlock.Media.toLocalMedia(): LocalNote.Content.Media = when (this) {
    is UiNoteContent.MediaBlock.Image -> toLocalNoteImage()
    is UiNoteContent.MediaBlock.Video -> toLocalNoteVideo()
}

fun UiNoteContent.MediaBlock.Image.toLocalNoteImage() = LocalNote.Content.Image(
    id = id,
    uri = uri,
    ratio = ratio,
    date = date,
)

fun UiNoteContent.MediaBlock.Video.toLocalNoteVideo() = LocalNote.Content.Video(
    id = id,
    uri = uri,
    ratio = ratio,
    date = date,
    duration = duration,
)

fun UiNoteTag.Template.toLocalNoteTag() = LocalNote.Tag(
    id = id,
    title = textState.text.trim().toString(),
)

fun UiNoteTag.Template.toRegular(isRemovable: Boolean) = UiNoteTag.Regular(
    id = id,
    title = textState.text.trim().toString(),
    isRemovable = isRemovable,
)

fun LocalNote.Content.toUiNoteContent() = when (this) {
    is LocalNote.Content.Title -> toUiNoteTitle()
    is LocalNote.Content.MediaBlock -> toUiMediaBlock()
}

fun LocalNote.Content.MediaBlock.toUiMediaBlock() = UiNoteContent.MediaBlock(
    id = id,
    media = media.mapImmutable(LocalNote.Content.Media::toUiNoteMedia),
)


fun LocalNote.Content.Title.toUiNoteTitle() = UiNoteContent.Title(
    id = id,
    state = TextFieldState(initialText = text),
)

fun LocalNote.Content.Media.toUiNoteMedia(): UiNoteContent.MediaBlock.Media = when (this) {
    is LocalNote.Content.Image -> toUiNoteImage()
    is LocalNote.Content.Video -> toUiNoteVideo()
}

fun LocalNote.Content.Image.toUiNoteImage() = UiNoteContent.MediaBlock.Image(
    id = id,
    uri = uri,
    ratio = ratio,
    date = date,
)

fun LocalNote.Content.Video.toUiNoteVideo() = UiNoteContent.MediaBlock.Video(
    id = id,
    uri = uri,
    ratio = ratio,
    duration = duration,
    date = date,
)

fun LocalNote.Tag.toRegularUiNoteTag(isRemovable: Boolean) = UiNoteTag.Regular(
    id = id,
    title = title,
    isRemovable = isRemovable,
)
