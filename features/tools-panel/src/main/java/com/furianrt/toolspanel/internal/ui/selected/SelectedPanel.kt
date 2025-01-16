package com.furianrt.toolspanel.internal.ui.selected

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.AnnotatedString.Range
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.furianrt.notelistui.composables.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.toolspanel.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.toImmutableList

private enum class SpanType {
    BOLD, ITALIC, UNDERLINE, STRIKETHROUGH, FONT_COLOR, FILL_COLOR,
}

private enum class PanelState {
    DEFAULT, FONT_COLOR, FILL_COLOR,
}

private fun SpanStyle.toSpanType(): SpanType? = when {
    fontWeight != null -> SpanType.BOLD
    fontStyle == FontStyle.Italic -> SpanType.ITALIC
    textDecoration == TextDecoration.Underline -> SpanType.UNDERLINE
    textDecoration == TextDecoration.LineThrough -> SpanType.STRIKETHROUGH
    color != Color.Unspecified -> SpanType.FONT_COLOR
    background != Color.Unspecified -> SpanType.FILL_COLOR
    else -> null
}

@Composable
internal fun SelectedPanel(
    titleState: NoteTitleState = NoteTitleState(),
    modifier: Modifier = Modifier,
) {
    var panelState by remember { mutableStateOf(PanelState.DEFAULT) }
    AnimatedContent(
        targetState = panelState,
        transitionSpec = {
            (fadeIn(animationSpec = tween(durationMillis = 220, delayMillis = 90)))
                .togetherWith(fadeOut(animationSpec = tween(durationMillis = 90)))
        },
        label = "ContentAnim",
    ) { targetState ->
        when (targetState) {
            PanelState.DEFAULT -> SelectedStateContent(
                modifier = modifier,
                titleState = titleState,
                onFontColorsClick = { panelState = PanelState.FONT_COLOR },
                onFillColorsClick = { panelState = PanelState.FILL_COLOR },
            )

            PanelState.FONT_COLOR -> FontColorsStateContent(
                modifier = modifier,
                titleState = titleState,
                onCloseClick = { panelState = PanelState.DEFAULT },
            )

            PanelState.FILL_COLOR -> FillColorsStateContent(
                modifier = modifier,
                titleState = titleState,
                onCloseClick = { panelState = PanelState.DEFAULT },
            )
        }
    }
}

@Composable
private fun SelectedStateContent(
    modifier: Modifier,
    titleState: NoteTitleState,
    onFontColorsClick: () -> Unit,
    onFillColorsClick: () -> Unit,
) {
    val hasBoldStyles = remember(titleState.annotatedString, titleState.selection) {
        titleState.annotatedString.hasSpans(
            start = titleState.selection.min,
            end = titleState.selection.max,
            spanType = SpanType.BOLD
        )
    }
    val hasItalicStyles = remember(titleState.annotatedString, titleState.selection) {
        titleState.annotatedString.hasSpans(
            start = titleState.selection.min,
            end = titleState.selection.max,
            spanType = SpanType.ITALIC
        )
    }
    val hasUnderlineStyles = remember(titleState.annotatedString, titleState.selection) {
        titleState.annotatedString.hasSpans(
            start = titleState.selection.min,
            end = titleState.selection.max,
            spanType = SpanType.UNDERLINE
        )
    }
    val hasStrikeThroughStyles = remember(titleState.annotatedString, titleState.selection) {
        titleState.annotatedString.hasSpans(
            start = titleState.selection.min,
            end = titleState.selection.max,
            spanType = SpanType.STRIKETHROUGH
        )
    }
    val hasFontColorStyles = remember(titleState.annotatedString, titleState.selection) {
        titleState.annotatedString.hasSpans(
            start = titleState.selection.min,
            end = titleState.selection.max,
            spanType = SpanType.FONT_COLOR
        )
    }
    val hasFillColorStyles = remember(titleState.annotatedString, titleState.selection) {
        titleState.annotatedString.hasSpans(
            start = titleState.selection.min,
            end = titleState.selection.max,
            spanType = SpanType.FILL_COLOR
        )
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = {
                titleState.annotatedString = buildAnnotatedString {
                    append(
                        titleState.annotatedString.removeSpansFromSelection(
                            start = titleState.selection.min,
                            end = titleState.selection.max,
                            spanType = SpanType.BOLD,
                        )
                    )
                    if (!hasBoldStyles) {
                        addStyle(
                            style = SpanStyle(fontWeight = FontWeight.Black),
                            start = titleState.selection.min,
                            end = titleState.selection.max,
                        )
                    }
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_bold),
                contentDescription = null,
                tint = if (hasBoldStyles) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
        IconButton(
            onClick = {
                titleState.annotatedString = buildAnnotatedString {
                    append(
                        titleState.annotatedString.removeSpansFromSelection(
                            start = titleState.selection.min,
                            end = titleState.selection.max,
                            spanType = SpanType.ITALIC,
                        )
                    )
                    if (!hasItalicStyles) {
                        addStyle(
                            style = SpanStyle(fontStyle = FontStyle.Italic),
                            start = titleState.selection.min,
                            end = titleState.selection.max,
                        )
                    }
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_italic),
                contentDescription = null,
                tint = if (hasItalicStyles) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
        IconButton(
            onClick = {
                titleState.annotatedString = buildAnnotatedString {
                    append(
                        titleState.annotatedString.removeSpansFromSelection(
                            start = titleState.selection.min,
                            end = titleState.selection.max,
                            spanType = SpanType.UNDERLINE,
                        )
                    )
                    if (!hasUnderlineStyles) {
                        addStyle(
                            style = SpanStyle(textDecoration = TextDecoration.Underline),
                            start = titleState.selection.min,
                            end = titleState.selection.max,
                        )
                    }
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_underline),
                contentDescription = null,
                tint = if (hasUnderlineStyles) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
        IconButton(
            onClick = {
                titleState.annotatedString = buildAnnotatedString {
                    append(
                        titleState.annotatedString.removeSpansFromSelection(
                            start = titleState.selection.min,
                            end = titleState.selection.max,
                            spanType = SpanType.STRIKETHROUGH,
                        )
                    )
                    if (!hasStrikeThroughStyles) {
                        addStyle(
                            style = SpanStyle(textDecoration = TextDecoration.LineThrough),
                            start = titleState.selection.min,
                            end = titleState.selection.max,
                        )
                    }
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_strikethrough),
                contentDescription = null,
                tint = if (hasStrikeThroughStyles) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
        IconButton(
            onClick = {
                if (hasFontColorStyles) {
                    titleState.annotatedString = buildAnnotatedString {
                        append(
                            titleState.annotatedString.removeSpansFromSelection(
                                start = titleState.selection.min,
                                end = titleState.selection.max,
                                spanType = SpanType.FONT_COLOR,
                            )
                        )
                    }
                } else {
                    onFontColorsClick()
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_font_color),
                contentDescription = null,
                tint = if (hasFontColorStyles) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
        IconButton(
            onClick = {
                if (hasFillColorStyles) {
                    titleState.annotatedString = buildAnnotatedString {
                        append(
                            titleState.annotatedString.removeSpansFromSelection(
                                start = titleState.selection.min,
                                end = titleState.selection.max,
                                spanType = SpanType.FILL_COLOR,
                            )
                        )
                    }
                } else {
                    onFillColorsClick()
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_fill_color),
                contentDescription = null,
                tint = if (hasFillColorStyles) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
    }
}

@Composable
private fun FontColorsStateContent(
    modifier: Modifier,
    titleState: NoteTitleState,
    onCloseClick: () -> Unit,
) {
    val colors = remember { UiNoteFontColor.entries.toImmutableList() }
    val selectedColor = remember(titleState.annotatedString, titleState.selection) {
        titleState.annotatedString
            .getSpansStyles(
                start = titleState.selection.min,
                end = titleState.selection.max,
            )
            .filter { it.item.color != Color.Unspecified }
            .map { UiNoteFontColor.fromColor(it.item.color.toArgb()) }
            .takeIf { it.count() == 1 }
            ?.firstOrNull()
    }
    ColorsPanel(
        modifier = modifier,
        colors = colors,
        selectedColor = selectedColor,
        onCloseClick = onCloseClick,
        onColorSelected = { color ->
            titleState.annotatedString = buildAnnotatedString {
                append(
                    titleState.annotatedString.removeSpansFromSelection(
                        start = titleState.selection.min,
                        end = titleState.selection.max,
                        spanType = SpanType.FONT_COLOR,
                    )
                )
                if (color != null) {
                    addStyle(
                        style = SpanStyle(color = color.value),
                        start = titleState.selection.min,
                        end = titleState.selection.max,
                    )
                }
            }
        },
    )
}

@Composable
private fun FillColorsStateContent(
    modifier: Modifier,
    titleState: NoteTitleState,
    onCloseClick: () -> Unit,
) {
    val colors = remember { UiNoteFontColor.entries.toImmutableList() }
    val selectedColor = remember(titleState.annotatedString, titleState.selection) {
        titleState.annotatedString
            .getSpansStyles(
                start = titleState.selection.min,
                end = titleState.selection.max,
            )
            .filter { it.item.background != Color.Unspecified }
            .map { UiNoteFontColor.fromColor(it.item.background.toArgb()) }
            .takeIf { it.count() == 1 }
            ?.firstOrNull()
    }
    ColorsPanel(
        modifier = modifier,
        colors = colors,
        selectedColor = selectedColor,
        onCloseClick = onCloseClick,
        onColorSelected = { color ->
            titleState.annotatedString = buildAnnotatedString {
                append(
                    titleState.annotatedString.removeSpansFromSelection(
                        start = titleState.selection.min,
                        end = titleState.selection.max,
                        spanType = SpanType.FILL_COLOR,
                    )
                )
                if (color != null) {
                    addStyle(
                        style = SpanStyle(background = color.value),
                        start = titleState.selection.min,
                        end = titleState.selection.max,
                    )
                }
            }
        },
    )
}

private fun AnnotatedString.removeSpansFromSelection(
    start: Int,
    end: Int,
    spanType: SpanType,
): AnnotatedString = flatMapAnnotations { span ->
    if (text.isEmpty()) {
        return@flatMapAnnotations emptyList()
    }
    val item = span.item
    if (item !is SpanStyle || item.toSpanType() != spanType) {
        return@flatMapAnnotations listOf(span)
    }

    if (span.start >= span.end) {
        return@flatMapAnnotations emptyList()
    }

    if ((start..end).intersect(span.start..span.end).isEmpty()) {
        return@flatMapAnnotations listOf(span)
    }

    val adjustedSpans = when {
        // If the change is within the range, adjust the end of the range
        start in span.start..span.end && end in span.start..span.end -> {
            listOf(
                span.copy(end = start),
                span.copy(start = end),
            )
        }
        // If the change overlaps the start of the range, adjust the start
        start !in span.start..span.end && end in span.start..span.end -> {
            listOf(span.copy(start = end))
        }
        // If the change overlaps the end of the range, adjust the end
        start in span.start..span.end && end !in span.start..span.end -> {
            listOf(span.copy(end = start))
        }
        // If the change completely overlaps the range, remove the range
        start <= span.start && end >= span.end -> {
            emptyList()
        }

        else -> {
            listOf(span)
        }
    }
    return@flatMapAnnotations adjustedSpans
        .map {
            it.copy(
                start = it.start.coerceAtLeast(0),
                end = it.end.coerceAtMost(text.length),
            )
        }
        .filter { it.start >= 0 && it.start < it.end }
}

private fun AnnotatedString.getSpansStyles(
    start: Int,
    end: Int,
): List<Range<SpanStyle>> = spanStyles.filter { start >= it.start && end <= it.end }

private fun AnnotatedString.hasSpans(
    start: Int,
    end: Int,
    spanType: SpanType,
): Boolean = getSpansStyles(start = start, end = end)
    .any { it.item.toSpanType() == spanType }

@PreviewWithBackground
@Composable
private fun SelectedPanelPreview() {
    SerenityTheme {
        SelectedPanel()
    }
}
