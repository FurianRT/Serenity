package com.furianrt.notecontent.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.uikit.extensions.cursorCoordinates
import com.furianrt.uikit.extensions.rememberKeyboardOffsetState
import com.furianrt.uikit.theme.Colors
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteContentTitle(
    title: UiNoteContent.Title,
    modifier: Modifier = Modifier,
    hint: String? = null,
    isInEditMode: Boolean? = null,
    focusOffset: () -> Int = { 0 },
    onTitleChange: (text: String) -> Unit = {},
    onTitleFocused: () -> Unit = {},
) {
    if (isInEditMode == null) {
        Text(
            modifier = modifier,
            text = title.text,
            style = MaterialTheme.typography.bodyMedium,
        )
        return
    }

    var layoutResult: TextLayoutResult? by remember { mutableStateOf(null) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    var titleText by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text = title.text))
    }

    var hasFocus by remember { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(true) }

    if (hasFocus) {
        val keyboardOffset by rememberKeyboardOffsetState(minOffset = 300)
        LaunchedEffect(titleText.selection, keyboardOffset) {
            bringIntoViewRequester.bringIntoView(layoutResult, titleText.selection, focusOffset())
        }
    }

    LaunchedEffect(isInEditMode) {
        if (!isInEditMode && hasFocus) {
            enabled = false
            delay(200)
            enabled = true
        }
    }

    BasicTextField(
        modifier = modifier
            .onFocusChanged { focusState ->
                hasFocus = focusState.hasFocus
                if (focusState.hasFocus && !isInEditMode) {
                    onTitleFocused()
                }
            }
            .bringIntoViewRequester(bringIntoViewRequester),
        onTextLayout = { layoutResult = it },
        value = titleText,
        enabled = enabled,
        onValueChange = { text ->
            titleText = text
            onTitleChange(text.text)
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        cursorBrush = SolidColor(Colors.Blue),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        decorationBox = { innerTextField ->
            if (hint != null && titleText.text.isEmpty()) {
                Placeholder(hint = hint)
            }
            innerTextField()
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
private suspend fun BringIntoViewRequester.bringIntoView(
    textResult: TextLayoutResult?,
    selection: TextRange,
    additionalOffset: Int,
) {
    val (top, bottom) = textResult?.cursorCoordinates(selection) ?: return
    bringIntoView(
        Rect(
            left = 0f,
            top = top + additionalOffset,
            right = 0f,
            bottom = bottom + additionalOffset,
        ),
    )
}

@Composable
private fun Placeholder(hint: String) {
    Text(
        modifier = Modifier.alpha(0.3f),
        text = hint,
        style = MaterialTheme.typography.labelMedium,
        fontStyle = FontStyle.Italic,
    )
}

@Preview
@Composable
private fun NoteContentTitlePreview() {
    SerenityTheme {
        NoteContentTitle(
            title = UiNoteContent.Title(
                id = "1",
                position = 0,
                text = "Kotlin is a modern programming language with a " +
                    "lot more syntactic sugar compared to Java, and as such " +
                    "there is equally more black magic",
            ),
            hint = "Text",
        )
    }
}
