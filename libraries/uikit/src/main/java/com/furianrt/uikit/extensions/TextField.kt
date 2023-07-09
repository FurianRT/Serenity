package com.furianrt.uikit.extensions

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange

fun TextLayoutResult.cursorCoordinates(selection: TextRange): Pair<Float, Float> {
    val currentLine = try {
        getLineForOffset(selection.end)
    } catch (ex: IllegalArgumentException) {
        getLineForOffset(selection.end - 1)
    }
    val lineTop = getLineTop(currentLine)
    val lineBottom = getLineBottom(currentLine)
    return lineTop to lineBottom
}
