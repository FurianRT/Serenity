package com.furianrt.uikit.extensions

import androidx.compose.ui.text.AnnotatedString

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