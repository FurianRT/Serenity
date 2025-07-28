package com.furianrt.uikit.extensions

import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

fun TextLayoutResult.cursorCoordinates(selection: TextRange): Pair<Float, Float>? {
    val minLine = try {
        getLineForOffset(selection.min)
    } catch (ex: IllegalArgumentException) {
        getLineForOffset(selection.min - 1)
    }

    val maxLine = try {
        getLineForOffset(selection.max)
    } catch (ex: IllegalArgumentException) {
        getLineForOffset(selection.max - 1)
    }

    return if(minLine != maxLine) {
        null
    } else {
        getLineTop(maxLine) to getLineBottom(maxLine)
    }
}

suspend fun BringIntoViewRequester.bringIntoView(
    textResult: TextLayoutResult?,
    selection: TextRange,
    additionalTopOffset: Float,
    additionalBottomOffset: Float,
) {
    val (top, bottom) = textResult?.cursorCoordinates(selection) ?: return
    bringIntoView(
        additionalTopOffset = top - additionalTopOffset,
        additionalBottomOffset = bottom + additionalBottomOffset,
    )
}

suspend fun BringIntoViewRequester.bringIntoView(
    additionalTopOffset: Float,
    additionalBottomOffset: Float,
) {
    bringIntoView(
        Rect(
            left = 0f,
            top = additionalTopOffset,
            right = 0f,
            bottom = additionalBottomOffset,
        ),
    )
}


// Костыль для фикса annotatedString https://issuetracker.google.com/issues/135556699
fun TextFieldValue.differSpans(newValue: TextFieldValue): TextFieldValue {
    val lengthDifference = newValue.text.length - text.length
    val oldSpanStyles = annotatedString.spanStyles.mapMoving(
        selectionStart = selection.min,
        selectionEnd = selection.max,
        delta = lengthDifference,
        textLength = newValue.text.length,
    )
    val oldParagraphStyles = annotatedString.paragraphStyles.mapMoving(
        selectionStart = selection.min,
        selectionEnd = selection.max,
        delta = lengthDifference,
        textLength = newValue.text.length,
    )
    val newSpanStyles = newValue.annotatedString.spanStyles
    val newParagraphStyles = newValue.annotatedString.paragraphStyles
    return newValue.copy(
        annotatedString = AnnotatedString(
            text = newValue.text,
            spanStyles = (oldSpanStyles + newSpanStyles).distinct(),
            paragraphStyles = (oldParagraphStyles + newParagraphStyles).distinct(),
        ),
    )
}

private fun <T> List<AnnotatedString.Range<T>>.mapMoving(
    selectionStart: Int,
    selectionEnd: Int,
    delta: Int,
    textLength: Int,
): List<AnnotatedString.Range<T>> = mapNotNull { span ->
    when {
        delta == 0 -> span

        // If the change is before the range, shift the range by the delta
        selectionEnd <= span.start -> span.copy(
            start = span.start + delta,
            end = span.end + delta,
        )
        // If the change is after the range, do not affect the range
        selectionStart > span.end -> {
            span
        }
        // if the change is on the end of the range and it is adding a new char
        selectionStart == span.end && delta > 0 -> {
            span
        }
        // If the change is within the range, adjust the end of the range
        selectionStart in span.start..span.end && selectionEnd in span.start..span.end -> {
            span.copy(
                end = span.end + delta,
            )
        }
        // If the change overlaps the start of the range, adjust the start
        selectionStart < span.start && selectionEnd < span.end -> span.copy(
            start = selectionEnd + delta,
            end = span.end + delta,
        )
        // If the change overlaps the end of the range, adjust the end
        selectionStart > span.start && selectionStart < span.end -> span.copy(
            end = selectionStart,
        )
        // If the change completely overlaps the range, remove the range
        selectionStart < span.start && selectionEnd >= span.end -> {
            null
        }

        else -> {
            span
        }
    }
}.filter { span ->
    span.start <= span.end
}.map { span ->
    span.copy(
        start = span.start.coerceIn(0, textLength),
        end = span.end.coerceIn(0, textLength),
    )
}
