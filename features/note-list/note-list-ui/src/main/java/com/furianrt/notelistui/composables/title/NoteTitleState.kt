package com.furianrt.notelistui.composables.title

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.AnnotatedString.Range
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import com.furianrt.notelistui.composables.title.NoteTitleState.SpanType
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.uikit.extensions.differSpans

@Stable
class NoteTitleState(
    private var fontFamily: UiNoteFontFamily,
    initialText: AnnotatedString = AnnotatedString(""),
    initialSelection: TextRange = TextRange(initialText.length),
) {
    sealed interface SpanType {
        data object Bold : SpanType
        data object Italic : SpanType
        data object Underline : SpanType
        data object Strikethrough : SpanType
        data class FontColor(val color: Color = Color.Unspecified) : SpanType
        data class FillColor(val color: Color = Color.Unspecified) : SpanType
    }

    private val undoRedoManager = UndoRedoManager()

    private var textValueState by mutableStateOf(TextFieldValue(initialText, initialSelection))

    internal val textValue: TextFieldValue
        get() = textValueState

    var annotatedString: AnnotatedString
        get() = textValueState.annotatedString
        set(value) {
            undoRedoManager.clearHistory()
            textValueState = textValueState.copy(annotatedString = value)
        }

    val text: String
        get() = annotatedString.text

    var selection: TextRange
        get() = textValueState.selection
        set(value) {
            textValueState = textValueState.copy(selection = value)
        }

    val canUndo: Boolean
        get() = undoRedoManager.canUndo

    val canRedo: Boolean
        get() = undoRedoManager.canRedo

    fun addSpan(start: Int, end: Int, spanType: SpanType) {
        val result = textValueState.copy(
            annotatedString = buildAnnotatedString {
                append(
                    textValueState.annotatedString.removeSpansFromSelection(
                        start = start,
                        end = end,
                        spanType = when (spanType) {
                            is SpanType.FillColor -> spanType.copy(color = Color.Unspecified)
                            is SpanType.FontColor -> spanType.copy(color = Color.Unspecified)
                            else -> spanType
                        },
                    )
                )
                addStyle(
                    style = spanType.toSpanStyle(fontFamily),
                    start = start,
                    end = end,
                )
            }
        )
        recordUndo(oldValue = textValueState, newValue = result)
        textValueState = result
    }

    fun removeSpan(start: Int, end: Int, spanType: SpanType) {
        val result = textValueState.copy(
            annotatedString = buildAnnotatedString {
                append(
                    textValueState.annotatedString.removeSpansFromSelection(
                        start = start,
                        end = end,
                        spanType = spanType,
                    )
                )
            }
        )
        recordUndo(oldValue = textValueState, newValue = result)
        textValueState = result
    }

    fun hasSpan(start: Int, end: Int, spanType: SpanType): Boolean {
        return annotatedString.hasSpans(start, end, spanType)
    }

    fun hasAnyFontColorSpan(start: Int, end: Int): Boolean = annotatedString
        .getSpansStyles(start = start, end = end)
        .any { it.item.toSpanType() is SpanType.FontColor }

    fun hasAnyFillColorSpan(start: Int, end: Int): Boolean = annotatedString
        .getSpansStyles(start = start, end = end)
        .any { it.item.toSpanType() is SpanType.FillColor }

    fun undo() {
        val entry = undoRedoManager.undo() ?: return
        textValueState = textValueState.copy(
            annotatedString = entry.annotatedString,
            selection = TextRange(
                entry.selection.max.coerceIn(0, entry.annotatedString.length),
                entry.selection.max.coerceIn(0, entry.annotatedString.length),
            ),
        )
    }

    fun redo() {
        val entry = undoRedoManager.redo() ?: return
        textValueState = textValueState.copy(
            annotatedString = entry.annotatedString,
            selection = TextRange(
                entry.selection.max.coerceIn(0, entry.annotatedString.length),
                entry.selection.max.coerceIn(0, entry.annotatedString.length),
            ),
        )
    }

    fun getSpans(
        start: Int,
        end: Int,
        spanType: SpanType,
    ): List<SpanType> = textValueState.annotatedString.getSpansStyles(start, end)
        .filter { span ->
            val item = span.item.toSpanType() ?: return@filter false
            item.isSameSpan(spanType)
        }
        .mapNotNull { it.item.toSpanType() }

    fun updateFontFamily(fontFamily: UiNoteFontFamily) {
        this.fontFamily = fontFamily
        textValueState = textValueState.copy(
            annotatedString = textValueState.annotatedString.updateBoldFontFamily(fontFamily.bold),
        )
    }

    internal fun updateValue(value: TextFieldValue) {
        val result = textValueState.differSpans(value)
        recordUndo(oldValue = textValueState, newValue = result)
        textValueState = result
    }

    private fun recordUndo(oldValue: TextFieldValue, newValue: TextFieldValue) {
        if (oldValue.annotatedString != newValue.annotatedString) {
            val operation = UndoRedoOperation(
                preText = oldValue.annotatedString,
                postText = newValue.annotatedString,
                preSelection = oldValue.selection,
                postSelection = newValue.selection,
            )
            undoRedoManager.record(operation)
        }
    }
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

private fun AnnotatedString.updateBoldFontFamily(
    fontFamily: FontFamily,
): AnnotatedString = flatMapAnnotations { span ->
    val spanStyle: AnnotatedString.Annotation = span.item

    if (spanStyle !is SpanStyle) {
        return@flatMapAnnotations listOf(span)
    }


    val item = (spanStyle as? SpanStyle)?.toSpanType() ?: return@flatMapAnnotations listOf(span)

    if (item !is SpanType.Bold) {
        return@flatMapAnnotations listOf(span)
    }

    return@flatMapAnnotations listOf(
        Range(
            item = spanStyle.copy(fontFamily = fontFamily),
            start = span.start,
            end = span.end,
        )
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
    val spanStyle = span.item

    if (spanStyle !is SpanStyle) {
        return@flatMapAnnotations listOf(span)
    }

    val item = spanStyle.toSpanType() ?: return@flatMapAnnotations listOf(span)

    if (!item.isSameSpan(spanType)) {
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

private fun SpanType.isSameSpan(span: SpanType) = when (span) {
    is SpanType.FontColor -> if (span.color == Color.Unspecified) {
        this::class == span::class
    } else {
        this == span
    }

    is SpanType.FillColor -> if (span.color == Color.Unspecified) {
        this::class == span::class
    } else {
        this == span
    }

    else -> this == span
}
