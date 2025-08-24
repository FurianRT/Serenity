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
import com.furianrt.notelistui.composables.title.NoteTitleState.SpanType
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.uikit.extensions.join
import com.furianrt.uikit.theme.NoteFont
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

fun LocalNote.Content.toUiNoteContent(fontFamily: UiNoteFontFamily) = when (this) {
    is LocalNote.Content.Title -> toUiNoteTitle(fontFamily)
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


fun LocalNote.Content.Title.toUiNoteTitle(fontFamily: UiNoteFontFamily) = UiNoteContent.Title(
    id = id,
    state = NoteTitleState(
        initialText = getAnnotatedString(fontFamily),
        fontFamily = fontFamily,
    ),
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

fun List<LocalNote.Content>.getShortUiContent(
    fontFamily: UiNoteFontFamily,
): ImmutableList<UiNoteContent> = buildImmutableList {
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
                        .map { it.getAnnotatedString(fontFamily) }
                        .join(separator = "\n"),
                    fontFamily = fontFamily,
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
    NoteFontFamily.NOTO_SANS -> UiNoteFontFamily.NotoSans
    NoteFontFamily.NOTO_SERIF -> UiNoteFontFamily.NotoSerif
    NoteFontFamily.ROBOTO -> UiNoteFontFamily.Roboto
    NoteFontFamily.SHANTELL_SANS -> UiNoteFontFamily.ShantellSans
    NoteFontFamily.PIXELIFY_SANS -> UiNoteFontFamily.PixelifySans
    NoteFontFamily.ADVENT_PRO -> UiNoteFontFamily.AdventPro
    NoteFontFamily.CORMORANT_UNICASE -> UiNoteFontFamily.CormorantUnicase
    NoteFontFamily.MONSERRAT_ALTERNATES -> UiNoteFontFamily.MontserratAlternates
    NoteFontFamily.TEKTUR -> UiNoteFontFamily.Tektur
    NoteFontFamily.DOTO -> UiNoteFontFamily.Doto
    NoteFontFamily.PLAY_WRITE_MODERN -> UiNoteFontFamily.PlayWriteModern
    NoteFontFamily.TILLANA -> UiNoteFontFamily.Tillana
    NoteFontFamily.LIFE_SEVERS -> UiNoteFontFamily.LifeSavers
    NoteFontFamily.TEXTURINA -> UiNoteFontFamily.Texturina
}

fun UiNoteFontFamily.toNoteFontFamily(): NoteFontFamily = when (this) {
    UiNoteFontFamily.NotoSans -> NoteFontFamily.NOTO_SANS
    UiNoteFontFamily.NotoSerif -> NoteFontFamily.NOTO_SERIF
    UiNoteFontFamily.Roboto -> NoteFontFamily.ROBOTO
    UiNoteFontFamily.ShantellSans -> NoteFontFamily.SHANTELL_SANS
    UiNoteFontFamily.PixelifySans -> NoteFontFamily.PIXELIFY_SANS
    UiNoteFontFamily.AdventPro -> NoteFontFamily.ADVENT_PRO
    UiNoteFontFamily.CormorantUnicase -> NoteFontFamily.CORMORANT_UNICASE
    UiNoteFontFamily.MontserratAlternates -> NoteFontFamily.MONSERRAT_ALTERNATES
    UiNoteFontFamily.Tektur -> NoteFontFamily.TEKTUR
    UiNoteFontFamily.Doto -> NoteFontFamily.DOTO
    UiNoteFontFamily.PlayWriteModern -> NoteFontFamily.PLAY_WRITE_MODERN
    UiNoteFontFamily.Tillana -> NoteFontFamily.TILLANA
    UiNoteFontFamily.LifeSavers -> NoteFontFamily.LIFE_SEVERS
    UiNoteFontFamily.Texturina -> NoteFontFamily.TEXTURINA
}

fun NoteFontColor.toUiNoteFontColor(): UiNoteFontColor = when (this) {
    NoteFontColor.WHITE -> UiNoteFontColor.WHITE
    NoteFontColor.GREY -> UiNoteFontColor.GREY
    NoteFontColor.BLACK -> UiNoteFontColor.BLACK
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
    UiNoteFontColor.GREY -> NoteFontColor.GREY
    UiNoteFontColor.BLACK -> NoteFontColor.BLACK
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

fun SpanStyle.toSpanType(): SpanType? = when {
    fontFamily != null -> SpanType.Bold
    fontStyle == FontStyle.Italic -> SpanType.Italic
    textDecoration == TextDecoration.Underline -> SpanType.Underline
    textDecoration == TextDecoration.LineThrough -> SpanType.Strikethrough
    color != Color.Unspecified -> SpanType.FontColor(color)
    background != Color.Unspecified -> SpanType.FillColor(background)
    else -> null
}

fun SpanType.toSpanStyle(fontFamily: UiNoteFontFamily): SpanStyle = when (this) {
    is SpanType.Bold -> SpanStyle(fontFamily = fontFamily.bold)
    is SpanType.Italic -> SpanStyle(fontStyle = FontStyle.Italic)
    is SpanType.Underline -> SpanStyle(textDecoration = TextDecoration.Underline)
    is SpanType.Strikethrough -> SpanStyle(textDecoration = TextDecoration.LineThrough)
    is SpanType.FontColor -> SpanStyle(color = color)
    is SpanType.FillColor -> SpanStyle(background = color)
}

fun NoteFontFamily.toNoteFont() = when (this) {
    NoteFontFamily.NOTO_SANS -> NoteFont.NotoSans
    NoteFontFamily.NOTO_SERIF -> NoteFont.NotoSerif
    NoteFontFamily.ROBOTO -> NoteFont.Roboto
    NoteFontFamily.SHANTELL_SANS -> NoteFont.ShantellSans
    NoteFontFamily.PIXELIFY_SANS -> NoteFont.PixelifySans
    NoteFontFamily.ADVENT_PRO -> NoteFont.AdventPro
    NoteFontFamily.CORMORANT_UNICASE -> NoteFont.CormorantUnicase
    NoteFontFamily.MONSERRAT_ALTERNATES -> NoteFont.MontserratAlternates
    NoteFontFamily.TEKTUR -> NoteFont.Tektur
    NoteFontFamily.DOTO -> NoteFont.Doto
    NoteFontFamily.PLAY_WRITE_MODERN -> NoteFont.PlayWriteModern
    NoteFontFamily.TILLANA -> NoteFont.Tillana
    NoteFontFamily.LIFE_SEVERS -> NoteFont.LifeSavers
    NoteFontFamily.TEXTURINA -> NoteFont.Texturina
}

private fun AnnotatedString.Range<SpanStyle>.toNoteTextSpan(
    titleId: String,
): NoteTextSpan? = when {
    item.fontFamily != null -> NoteTextSpan.Bold(
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

private fun LocalNote.Content.Title.getAnnotatedString(
    fontFamily: UiNoteFontFamily,
) = buildAnnotatedString {
    append(text)
    spans.fastForEach { span ->
        when (span) {
            is NoteTextSpan.Bold -> addStyle(
                style = SpanType.Bold.toSpanStyle(fontFamily),
                start = span.start,
                end = span.end,
            )

            is NoteTextSpan.Italic -> addStyle(
                style = SpanType.Italic.toSpanStyle(fontFamily),
                start = span.start,
                end = span.end,
            )

            is NoteTextSpan.Underline -> addStyle(
                style = SpanType.Underline.toSpanStyle(fontFamily),
                start = span.start,
                end = span.end,
            )

            is NoteTextSpan.Strikethrough -> addStyle(
                style = SpanType.Strikethrough.toSpanStyle(fontFamily),
                start = span.start,
                end = span.end,
            )

            is NoteTextSpan.FontColor -> addStyle(
                style = SpanType.FontColor(Color(span.color)).toSpanStyle(fontFamily),
                start = span.start,
                end = span.end,
            )

            is NoteTextSpan.FillColor -> addStyle(
                style = SpanType.FillColor(Color(span.color)).toSpanStyle(fontFamily),
                start = span.start,
                end = span.end,
            )
        }
    }
}
