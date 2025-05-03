package com.furianrt.notelistui.extensions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.util.fastForEach
import com.furianrt.core.buildImmutableList
import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.NoteFontColor
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.entities.NoteTextSpan
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.composables.title.toSpanStyle
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.uikit.extensions.join
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

fun UiNoteContent.toLocalNoteContent() = when (this) {
    is UiNoteContent.Title -> toLocalNoteTitle()
    is UiNoteContent.MediaBlock -> toLocalMediaBlock()
    is UiNoteContent.Voice -> toLocalNoteVoice()
}

fun UiNoteContent.MediaBlock.toLocalMediaBlock() = LocalNote.Content.MediaBlock(
    id = id,
    media = media.map(UiNoteContent.MediaBlock.Media::toLocalMedia),
)

fun UiNoteContent.Title.toLocalNoteTitle() = LocalNote.Content.Title(
    id = id,
    text = state.annotatedString.text,
    spans = state.annotatedString.spanStyles.mapNotNull { it.toNoteTextSpan(titleId = id) },
)

fun UiNoteContent.Voice.toLocalNoteVoice() = LocalNote.Content.Voice(
    id = id,
    uri = uri,
    duration = duration.toInt(),
    volume = volume,
)

fun UiNoteContent.MediaBlock.Media.toLocalMedia(): LocalNote.Content.Media = when (this) {
    is UiNoteContent.MediaBlock.Image -> toLocalNoteImage()
    is UiNoteContent.MediaBlock.Video -> toLocalNoteVideo()
}

fun UiNoteContent.MediaBlock.Image.toLocalNoteImage() = LocalNote.Content.Image(
    id = id,
    name = name,
    uri = uri,
    ratio = ratio,
    addedDate = addedDate,
)

fun UiNoteContent.MediaBlock.Video.toLocalNoteVideo() = LocalNote.Content.Video(
    id = id,
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
    is LocalNote.Content.Voice -> toUiVoice()
}

fun LocalNote.Content.MediaBlock.toUiMediaBlock() = UiNoteContent.MediaBlock(
    id = id,
    media = media.mapImmutable(LocalNote.Content.Media::toUiNoteMedia),
)

fun LocalNote.Content.Voice.toUiVoice() = UiNoteContent.Voice(
    id = id,
    uri = uri,
    duration = duration.toLong(),
    volume = volume.toImmutableList(),
    progressState = UiNoteContent.Voice.ProgressState(),
)


fun LocalNote.Content.Title.toUiNoteTitle() = UiNoteContent.Title(
    id = id,
    state = NoteTitleState(initialText = getAnnotatedString()),
)

fun LocalNote.Content.Media.toUiNoteMedia(): UiNoteContent.MediaBlock.Media = when (this) {
    is LocalNote.Content.Image -> toUiNoteImage()
    is LocalNote.Content.Video -> toUiNoteVideo()
}

fun LocalNote.Content.Image.toUiNoteImage() = UiNoteContent.MediaBlock.Image(
    id = id,
    name = name,
    uri = uri,
    ratio = ratio,
    addedDate = addedDate,
)

fun LocalNote.Content.Video.toUiNoteVideo() = UiNoteContent.MediaBlock.Video(
    id = id,
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
                state = NoteTitleState(
                    initialText = this@getShortUiContent
                        .filterIsInstance<LocalNote.Content.Title>()
                        .map { it.getAnnotatedString() }
                        .join(separator = "\n")
                ),
            ),
        )
    }
    if (mediaBlock == null) {
        val voices = this@getShortUiContent
            .filterIsInstance<LocalNote.Content.Voice>()
            .take(2)
            .map(LocalNote.Content.Voice::toUiVoice)
        addAll(voices)
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
    NoteFontColor.GREY_LIGHT -> UiNoteFontColor.GREY_LIGHT
    NoteFontColor.GREY -> UiNoteFontColor.GREY
    NoteFontColor.GREY_DARK -> UiNoteFontColor.GREY_DARK
    NoteFontColor.PINK_LIGHT -> UiNoteFontColor.PINK_LIGHT
    NoteFontColor.PINK -> UiNoteFontColor.PINK
    NoteFontColor.PINK_DARK -> UiNoteFontColor.PINK_DARK
    NoteFontColor.YELLOW_LIGHT -> UiNoteFontColor.YELLOW_LIGHT
    NoteFontColor.YELLOW -> UiNoteFontColor.YELLOW
    NoteFontColor.YELLOW_DARK -> UiNoteFontColor.YELLOW_DARK
    NoteFontColor.GREEN_LIGHT -> UiNoteFontColor.GREEN_LIGHT
    NoteFontColor.GREEN -> UiNoteFontColor.GREEN
    NoteFontColor.GREEN_DARK -> UiNoteFontColor.GREEN_DARK
    NoteFontColor.BLUE_LIGHT -> UiNoteFontColor.BLUE_LIGHT
    NoteFontColor.BLUE -> UiNoteFontColor.BLUE
    NoteFontColor.BLUE_DARK -> UiNoteFontColor.BLUE_DARK
    NoteFontColor.PURPLE_LIGHT -> UiNoteFontColor.PURPLE_LIGHT
    NoteFontColor.PURPLE -> UiNoteFontColor.PURPLE
    NoteFontColor.PURPLE_DARK -> UiNoteFontColor.PURPLE_DARK
    NoteFontColor.RED_LIGHT -> UiNoteFontColor.RED_LIGHT
    NoteFontColor.RED -> UiNoteFontColor.RED
    NoteFontColor.RED_DARK -> UiNoteFontColor.RED_DARK
}

fun UiNoteFontColor.toNoteFontColor(): NoteFontColor = when (this) {
    UiNoteFontColor.WHITE -> NoteFontColor.WHITE
    UiNoteFontColor.GREY_LIGHT -> NoteFontColor.GREY_LIGHT
    UiNoteFontColor.GREY -> NoteFontColor.GREY
    UiNoteFontColor.GREY_DARK -> NoteFontColor.GREY_DARK
    UiNoteFontColor.PINK_LIGHT -> NoteFontColor.PINK_LIGHT
    UiNoteFontColor.PINK -> NoteFontColor.PINK
    UiNoteFontColor.PINK_DARK -> NoteFontColor.PINK_DARK
    UiNoteFontColor.YELLOW_LIGHT -> NoteFontColor.YELLOW_LIGHT
    UiNoteFontColor.YELLOW -> NoteFontColor.YELLOW
    UiNoteFontColor.YELLOW_DARK -> NoteFontColor.YELLOW_DARK
    UiNoteFontColor.GREEN_LIGHT -> NoteFontColor.GREEN_LIGHT
    UiNoteFontColor.GREEN -> NoteFontColor.GREEN
    UiNoteFontColor.GREEN_DARK -> NoteFontColor.GREEN_DARK
    UiNoteFontColor.BLUE_LIGHT -> NoteFontColor.BLUE_LIGHT
    UiNoteFontColor.BLUE -> NoteFontColor.BLUE
    UiNoteFontColor.BLUE_DARK -> NoteFontColor.BLUE_DARK
    UiNoteFontColor.PURPLE_LIGHT -> NoteFontColor.PURPLE_LIGHT
    UiNoteFontColor.PURPLE -> NoteFontColor.PURPLE
    UiNoteFontColor.PURPLE_DARK -> NoteFontColor.PURPLE_DARK
    UiNoteFontColor.RED_LIGHT -> NoteFontColor.RED_LIGHT
    UiNoteFontColor.RED -> NoteFontColor.RED
    UiNoteFontColor.RED_DARK -> NoteFontColor.RED_DARK
}

private fun AnnotatedString.Range<SpanStyle>.toNoteTextSpan(
    titleId: String,
): NoteTextSpan? = when {
    item.fontWeight != null -> NoteTextSpan.Bold(
        titleId = titleId,
        start = start,
        end = end,
    )

    item.fontStyle == FontStyle.Italic -> NoteTextSpan.Italic(
        titleId = titleId,
        start = start,
        end = end,
    )

    item.textDecoration == TextDecoration.Underline -> NoteTextSpan.Underline(
        titleId = titleId,
        start = start,
        end = end,
    )

    item.textDecoration == TextDecoration.LineThrough -> NoteTextSpan.Strikethrough(
        titleId = titleId,
        start = start,
        end = end,
    )

    item.color != Color.Unspecified -> NoteTextSpan.FontColor(
        titleId = titleId,
        color = item.color.toArgb(),
        start = start,
        end = end,
    )

    item.background != Color.Unspecified -> NoteTextSpan.FillColor(
        titleId = titleId,
        color = item.background.toArgb(),
        start = start,
        end = end,
    )

    else -> null
}

private fun LocalNote.Content.Title.getAnnotatedString() = buildAnnotatedString {
    append(text)
    spans.fastForEach { span ->
        when (span) {
            is NoteTextSpan.Bold -> addStyle(
                style = NoteTitleState.SpanType.Bold.toSpanStyle(),
                start = span.start,
                end = span.end,
            )

            is NoteTextSpan.Italic -> addStyle(
                style = NoteTitleState.SpanType.Italic.toSpanStyle(),
                start = span.start,
                end = span.end,
            )

            is NoteTextSpan.Underline -> addStyle(
                style = NoteTitleState.SpanType.Underline.toSpanStyle(),
                start = span.start,
                end = span.end,
            )

            is NoteTextSpan.Strikethrough -> addStyle(
                style = NoteTitleState.SpanType.Strikethrough.toSpanStyle(),
                start = span.start,
                end = span.end,
            )

            is NoteTextSpan.FontColor -> addStyle(
                style = NoteTitleState.SpanType.FontColor(Color(span.color)).toSpanStyle(),
                start = span.start,
                end = span.end,
            )

            is NoteTextSpan.FillColor -> addStyle(
                style = NoteTitleState.SpanType.FillColor(Color(span.color)).toSpanStyle(),
                start = span.start,
                end = span.end,
            )
        }
    }
}
