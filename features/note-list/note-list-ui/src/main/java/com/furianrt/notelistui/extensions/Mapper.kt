package com.furianrt.notelistui.extensions

import androidx.compose.foundation.text.input.TextFieldState
import com.furianrt.core.buildImmutableList
import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.NoteFontColor
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import kotlinx.collections.immutable.ImmutableList

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
    name = name,
    uri = uri,
    ratio = ratio,
    addedDate = addedDate,
)

fun UiNoteContent.MediaBlock.Video.toLocalNoteVideo() = LocalNote.Content.Video(
    name = name,
    uri = uri,
    ratio = ratio,
    addedDate = addedDate,
    duration = duration,
)

fun UiNoteTag.toLocalNoteTag() = when (this) {
    is UiNoteTag.Regular -> LocalNote.Tag(title = title)
    is UiNoteTag.Template -> LocalNote.Tag(title = textState.text.trim().toString())
}

fun UiNoteTag.Template.toRegular(isRemovable: Boolean) = UiNoteTag.Regular(
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
    name = name,
    uri = uri,
    ratio = ratio,
    addedDate = addedDate,
)

fun LocalNote.Content.Video.toUiNoteVideo() = UiNoteContent.MediaBlock.Video(
    name = name,
    uri = uri,
    ratio = ratio,
    duration = duration,
    addedDate = addedDate,
)

fun LocalNote.Tag.toRegularUiNoteTag(isRemovable: Boolean = false) = UiNoteTag.Regular(
    title = title,
    isRemovable = isRemovable,
)

fun List<LocalNote.Content>.getShortUiContent(): ImmutableList<UiNoteContent> = buildImmutableList {
    val mediaBlock = this@getShortUiContent.firstOrNull { it is LocalNote.Content.MediaBlock }
    if (mediaBlock != null) {
        add(
            UiNoteContent.MediaBlock(
                id = mediaBlock.id,
                media = this@getShortUiContent
                    .filterIsInstance<LocalNote.Content.MediaBlock>()
                    .flatMap(LocalNote.Content.MediaBlock::media)
                    .mapImmutable(LocalNote.Content.Media::toUiNoteMedia),
            ),
        )
    }
    val title = this@getShortUiContent.firstOrNull { it is LocalNote.Content.Title }
    if (title != null) {
        add(
            UiNoteContent.Title(
                id = title.id,
                state = TextFieldState(
                    initialText = this@getShortUiContent
                        .filterIsInstance<LocalNote.Content.Title>()
                        .joinToString(separator = "\n", transform = { it.text }),
                ),
            ),
        )
    }
}

fun NoteFontFamily.toUiNoteFontFamily(): UiNoteFontFamily = when (this) {
    NoteFontFamily.QUICK_SAND -> UiNoteFontFamily.QUICK_SAND
    NoteFontFamily.TEST_FONT_1 -> UiNoteFontFamily.TEST_FONT_1
    NoteFontFamily.TEST_FONT_2 -> UiNoteFontFamily.TEST_FONT_2
    NoteFontFamily.TEST_FONT_3 -> UiNoteFontFamily.TEST_FONT_3
    NoteFontFamily.TEST_FONT_4 -> UiNoteFontFamily.TEST_FONT_4
    NoteFontFamily.TEST_FONT_5 -> UiNoteFontFamily.TEST_FONT_5
    NoteFontFamily.TEST_FONT_6 -> UiNoteFontFamily.TEST_FONT_6
    NoteFontFamily.TEST_FONT_7 -> UiNoteFontFamily.TEST_FONT_7
    NoteFontFamily.TEST_FONT_8 -> UiNoteFontFamily.TEST_FONT_8
    NoteFontFamily.TEST_FONT_9 -> UiNoteFontFamily.TEST_FONT_9
    NoteFontFamily.TEST_FONT_10 -> UiNoteFontFamily.TEST_FONT_10
    NoteFontFamily.TEST_FONT_11 -> UiNoteFontFamily.TEST_FONT_11
    NoteFontFamily.TEST_FONT_12 -> UiNoteFontFamily.TEST_FONT_12
    NoteFontFamily.TEST_FONT_13 -> UiNoteFontFamily.TEST_FONT_13
    NoteFontFamily.TEST_FONT_14 -> UiNoteFontFamily.TEST_FONT_14
    NoteFontFamily.TEST_FONT_15 -> UiNoteFontFamily.TEST_FONT_15
    NoteFontFamily.TEST_FONT_16 -> UiNoteFontFamily.TEST_FONT_16
    NoteFontFamily.TEST_FONT_17 -> UiNoteFontFamily.TEST_FONT_17
    NoteFontFamily.TEST_FONT_18 -> UiNoteFontFamily.TEST_FONT_18
}

fun UiNoteFontFamily.toNoteFontFamily(): NoteFontFamily = when (this) {
    UiNoteFontFamily.QUICK_SAND -> NoteFontFamily.QUICK_SAND
    UiNoteFontFamily.TEST_FONT_1 -> NoteFontFamily.TEST_FONT_1
    UiNoteFontFamily.TEST_FONT_2 -> NoteFontFamily.TEST_FONT_2
    UiNoteFontFamily.TEST_FONT_3 -> NoteFontFamily.TEST_FONT_3
    UiNoteFontFamily.TEST_FONT_4 -> NoteFontFamily.TEST_FONT_4
    UiNoteFontFamily.TEST_FONT_5 -> NoteFontFamily.TEST_FONT_5
    UiNoteFontFamily.TEST_FONT_6 -> NoteFontFamily.TEST_FONT_6
    UiNoteFontFamily.TEST_FONT_7 -> NoteFontFamily.TEST_FONT_7
    UiNoteFontFamily.TEST_FONT_8 -> NoteFontFamily.TEST_FONT_8
    UiNoteFontFamily.TEST_FONT_9 -> NoteFontFamily.TEST_FONT_9
    UiNoteFontFamily.TEST_FONT_10 -> NoteFontFamily.TEST_FONT_10
    UiNoteFontFamily.TEST_FONT_11 -> NoteFontFamily.TEST_FONT_11
    UiNoteFontFamily.TEST_FONT_12 -> NoteFontFamily.TEST_FONT_12
    UiNoteFontFamily.TEST_FONT_13 -> NoteFontFamily.TEST_FONT_13
    UiNoteFontFamily.TEST_FONT_14 -> NoteFontFamily.TEST_FONT_14
    UiNoteFontFamily.TEST_FONT_15 -> NoteFontFamily.TEST_FONT_15
    UiNoteFontFamily.TEST_FONT_16 -> NoteFontFamily.TEST_FONT_16
    UiNoteFontFamily.TEST_FONT_17 -> NoteFontFamily.TEST_FONT_17
    UiNoteFontFamily.TEST_FONT_18 -> NoteFontFamily.TEST_FONT_18
}

fun NoteFontColor.toUiNoteFontColor(): UiNoteFontColor = when (this) {
    NoteFontColor.WHITE -> UiNoteFontColor.WHITE
    NoteFontColor.ORANGE -> UiNoteFontColor.ORANGE
    NoteFontColor.GREEN -> UiNoteFontColor.GREEN
    NoteFontColor.BLUE_LIGHT -> UiNoteFontColor.BLUE_LIGHT
    NoteFontColor.BLUE -> UiNoteFontColor.BLUE
    NoteFontColor.BLUE_DARK -> UiNoteFontColor.BLUE_DARK
    NoteFontColor.PURPLE -> UiNoteFontColor.PURPLE
    NoteFontColor.PURPLE_DARK -> UiNoteFontColor.PURPLE_DARK
}

fun UiNoteFontColor.toNoteFontColor(): NoteFontColor = when (this) {
    UiNoteFontColor.WHITE -> NoteFontColor.WHITE
    UiNoteFontColor.ORANGE -> NoteFontColor.ORANGE
    UiNoteFontColor.GREEN -> NoteFontColor.GREEN
    UiNoteFontColor.BLUE_LIGHT -> NoteFontColor.BLUE_LIGHT
    UiNoteFontColor.BLUE -> NoteFontColor.BLUE
    UiNoteFontColor.BLUE_DARK -> NoteFontColor.BLUE_DARK
    UiNoteFontColor.PURPLE -> NoteFontColor.PURPLE
    UiNoteFontColor.PURPLE_DARK -> NoteFontColor.PURPLE_DARK
}
