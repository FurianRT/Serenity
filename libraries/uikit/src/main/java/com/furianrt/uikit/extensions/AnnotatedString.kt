package com.furianrt.uikit.extensions

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.AnnotatedString.Range
import androidx.compose.ui.text.SpanStyle

fun Iterable<AnnotatedString>.join(separator: String = ""): AnnotatedString {
    var result = AnnotatedString("")
    forEachIndexed { index, annotatedString ->
        result += annotatedString
        if (index != count() - 1) {
            result += AnnotatedString(separator)
        }
    }
    return result
}

fun AnnotatedString.getSpansStyles(
    start: Int,
    end: Int,
): List<Range<SpanStyle>> = spanStyles.filter { start >= it.start && end <= it.end }
