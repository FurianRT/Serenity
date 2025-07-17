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
import androidx.compose.ui.text.input.TextFieldValue
import com.furianrt.core.indexOfLastOrNull
import com.furianrt.notelistui.composables.title.NoteTitleState.SpanType
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.extensions.toSpanStyle
import com.furianrt.notelistui.extensions.toSpanType
import com.furianrt.uikit.extensions.differSpans
import com.furianrt.uikit.extensions.getSpansStyles

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

    sealed class BulletListType(val bullet: String) {
        data object Dots : BulletListType("●${Typography.nbsp}${Typography.nbsp}")
        data object Done : BulletListType("✓${Typography.nbsp}${Typography.nbsp}")
        data object Cross : BulletListType("✗${Typography.nbsp}${Typography.nbsp}")
        data object Star : BulletListType("\u2605${Typography.nbsp}${Typography.nbsp}")
        data object Hart : BulletListType("\u2665${Typography.nbsp}${Typography.nbsp}")
        data object Flower : BulletListType("\uD83C\uDF38${Typography.nbsp}${Typography.nbsp}")
        data object Knife : BulletListType("\uD83D\uDDE1\uFE0F${Typography.nbsp}${Typography.nbsp}")
        data object Scroll : BulletListType("\uD83D\uDCDC${Typography.nbsp}${Typography.nbsp}")
        data object Pencil : BulletListType("\uD83D\uDD8B\uFE0F${Typography.nbsp}${Typography.nbsp}")
        data object Sun : BulletListType("\u2600\uFE0F${Typography.nbsp}${Typography.nbsp}")
        data object Moon : BulletListType("\uD83C\uDF11${Typography.nbsp}${Typography.nbsp}")
        data object Candle : BulletListType("\uD83D\uDD6F\uFE0F${Typography.nbsp}${Typography.nbsp}")

        companion object {
            fun getAllBullets(): Set<String> = setOf(
                Dots.bullet,
                Done.bullet,
                Cross.bullet,
                Star.bullet,
                Hart.bullet,
                Flower.bullet,
                Knife.bullet,
                Scroll.bullet,
                Pencil.bullet,
                Sun.bullet,
                Moon.bullet,
                Candle.bullet,
            )
        }
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

    fun addBulletList(
        position: Int,
        bulletList: BulletListType,
    ) {
        removeAnyBulletList(position)

        val startPart = annotatedString.substring(
            startIndex = 0,
            endIndex = position.coerceAtMost(annotatedString.length),
        )

        val breakIndex = startPart.indexOfLast { it == '\n' } + 1

        val newAnnotatedString = buildAnnotatedString {
            append(annotatedString.subSequence(0, breakIndex))
            append(bulletList.bullet)
            append(annotatedString.subSequence(breakIndex, annotatedString.length))
        }

        val result = textValueState.copy(
            annotatedString = newAnnotatedString,
            selection = TextRange(
                index = (textValueState.selection.min + bulletList.bullet.length)
                    .coerceAtMost(newAnnotatedString.length),
            ),
        )

        recordUndo(oldValue = textValueState, newValue = result)
        textValueState = result
    }

    fun removeBulletList(
        position: Int,
        bulletList: BulletListType,
    ) {
        if (!hasBulletList(position, bulletList)) {
            return
        }

        val startPart = annotatedString.substring(
            startIndex = 0,
            endIndex = position,
        )

        val bulletIndex = startPart.indexOfLast { it == '\n' } + 1

        val newAnnotatedString = buildAnnotatedString {
            append(annotatedString.subSequence(0, bulletIndex))
            append(
                annotatedString.subSequence(
                    startIndex = bulletIndex + bulletList.bullet.length,
                    endIndex = annotatedString.length,
                )
            )
        }

        val result = textValueState.copy(
            annotatedString = newAnnotatedString,
            selection = TextRange(
                (textValueState.selection.min - bulletList.bullet.length)
                    .coerceAtLeast(0)
            ),
        )

        recordUndo(oldValue = textValueState, newValue = result)
        textValueState = result
    }

    fun hasBulletList(
        position: Int,
        bulletList: BulletListType,
    ): Boolean {
        val startPart = annotatedString.substring(
            startIndex = 0,
            endIndex = position,
        )
        return startPart
            .substring(
                startIndex = startPart.indexOfLastOrNull { it == '\n' }?.plus(1) ?: 0,
                endIndex = position,
            )
            .startsWith(bulletList.bullet)
    }

    fun undo() {
        val entry = undoRedoManager.undo() ?: return
        textValueState = textValueState.copy(
            annotatedString = entry.annotatedString.updateBoldFontFamily(fontFamily.bold),
            selection = TextRange(
                entry.selection.max.coerceIn(0, entry.annotatedString.length),
                entry.selection.max.coerceIn(0, entry.annotatedString.length),
            ),
        )
    }

    fun redo() {
        val entry = undoRedoManager.redo() ?: return
        textValueState = textValueState.copy(
            annotatedString = entry.annotatedString.updateBoldFontFamily(fontFamily.bold),
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

    private fun getBullet(position: Int): String? {
        val startPart = annotatedString.substring(
            startIndex = 0,
            endIndex = position,
        )
        val paragraph = startPart.substring(
            startIndex = startPart.indexOfLastOrNull { it == '\n' }?.plus(1) ?: 0,
            endIndex = position,
        )
        return BulletListType.getAllBullets().find(paragraph::startsWith)
    }

    private fun removeAnyBulletList(position: Int) {
        val bullet = getBullet(position) ?: return
        val startPart = annotatedString.substring(
            startIndex = 0,
            endIndex = position,
        )

        val bulletIndex = startPart.indexOfLast { it == '\n' } + 1

        val newAnnotatedString = buildAnnotatedString {
            append(annotatedString.subSequence(0, bulletIndex))
            append(
                annotatedString.subSequence(
                    startIndex = bulletIndex + bullet.length,
                    endIndex = annotatedString.length,
                )
            )
        }

        textValueState = textValueState.copy(
            annotatedString = newAnnotatedString,
        )
    }

    internal fun updateValue(value: TextFieldValue) {
        val withMergedSpans = textValueState.differSpans(value)
        val updatedValue = handleValueUpdate(oldValue = textValueState, newValue = withMergedSpans)
        val withAdjustedSelection = updatedValue.ignoreBulletListSelection()
        recordUndo(oldValue = textValueState, newValue = withAdjustedSelection)
        textValueState = withAdjustedSelection
    }

    private fun TextFieldValue.ignoreBulletListSelection(): TextFieldValue {
        var newSelectionMin = selection.min
        var newSelectionMax = selection.max

        while (
            BulletListType.getAllBullets().any { bullet ->
                val char = text.getOrNull(newSelectionMin) ?: return@any false
                bullet.any { it == char }
            }
        ) {
            newSelectionMin++
        }
        while (
            BulletListType.getAllBullets().any { bullet ->
                val char = text.getOrNull(newSelectionMax) ?: return@any false
                bullet.any { it == char }
            }
        ) {
            newSelectionMax++
        }
        return copy(
            selection = TextRange(
                start = newSelectionMin.coerceAtLeast(0),
                end = newSelectionMax.coerceAtMost(text.length),
            )
        )
    }

    private fun handleValueUpdate(
        oldValue: TextFieldValue,
        newValue: TextFieldValue,
    ): TextFieldValue = when {
        oldValue.annotatedString.length < newValue.annotatedString.length -> {
            handleAddCharacters(oldValue, newValue)
        }

        oldValue.annotatedString.length > newValue.annotatedString.length -> {
            handleRemoveCharacters(oldValue, newValue)
        }

        else -> newValue
    }

    private fun handleAddCharacters(
        oldValue: TextFieldValue,
        newValue: TextFieldValue,
    ): TextFieldValue {
        val typedCharsCount = newValue.text.length - oldValue.text.length
        val startTypeIndex = oldValue.selection.min

        val typedText = newValue.annotatedString.subSequence(
            startIndex = startTypeIndex,
            endIndex = startTypeIndex + typedCharsCount,
        )

        if (typedText.text != "\n") {
            return newValue
        }

        val bullet = getBullet(startTypeIndex) ?: return newValue

        val newAnnotatedString = buildAnnotatedString {
            append(
                newValue.annotatedString.subSequence(
                    startIndex = 0,
                    endIndex = newValue.selection.min,
                )
            )
            append(bullet)
            append(
                newValue.annotatedString.subSequence(
                    startIndex = newValue.selection.min,
                    endIndex = newValue.annotatedString.length,
                )
            )
        }

        return newValue.copy(
            annotatedString = newAnnotatedString,
            selection = TextRange(
                (newValue.selection.min + bullet.length)
                    .coerceAtMost(newAnnotatedString.text.length)
            ),
        )
    }

    private fun handleRemoveCharacters(
        oldValue: TextFieldValue,
        newValue: TextFieldValue,
    ): TextFieldValue {
        val removedCharsCount = oldValue.text.length - newValue.text.length
        val endTypeIndex = newValue.selection.min

        val removedText = oldValue.annotatedString.subSequence(
            startIndex = endTypeIndex,
            endIndex = endTypeIndex + removedCharsCount,
        )

        if (removedText.text != Typography.nbsp.toString()) {
            return newValue
        }

        val bullet = getBullet(oldValue.selection.min) ?: return newValue

        val newAnnotatedString = buildAnnotatedString {
            append(
                oldValue.annotatedString.subSequence(
                    startIndex = 0,
                    endIndex = oldValue.selection.min - bullet.length,
                )
            )
            append(
                oldValue.annotatedString.subSequence(
                    startIndex = oldValue.selection.min,
                    endIndex = oldValue.annotatedString.length,
                )
            )
        }

        return oldValue.copy(
            annotatedString = newAnnotatedString,
            selection = TextRange(
                (oldValue.selection.min - bullet.length)
                    .coerceAtLeast(0)
            ),
        )
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

private fun AnnotatedString.hasSpans(
    start: Int,
    end: Int,
    spanType: SpanType,
): Boolean = getSpansStyles(start = start, end = end)
    .any { it.item.toSpanType() == spanType }

private fun AnnotatedString.updateBoldFontFamily(
    fontFamily: FontFamily,
): AnnotatedString = flatMapAnnotations { span ->
    val spanStyle = span.item

    if (spanStyle !is SpanStyle) {
        return@flatMapAnnotations listOf(span)
    }

    val item = (spanStyle as? SpanStyle)?.toSpanType()

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