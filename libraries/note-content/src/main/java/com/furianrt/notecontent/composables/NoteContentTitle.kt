package com.furianrt.notecontent.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.uikit.extensions.cursorCoordinates
import com.furianrt.uikit.extensions.rememberKeyboardOffsetState
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteContentTitle(
    title: UiNoteContent.Title,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    toolbarHeight: Int = 0,
    hint: String? = null,
    isInEditMode: Boolean? = null,
    onTitleFocused: (id: String) -> Unit = {},
) {
    if (isInEditMode == null) {
        Text(
            modifier = modifier,
            text = title.state.text.toString(),
            style = MaterialTheme.typography.bodyMedium,
        )
        return
    }

    var layoutResult: TextLayoutResult? by remember { mutableStateOf(null) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    var hasFocus by remember { mutableStateOf(false) }

    val keyboardOffset by rememberKeyboardOffsetState(minOffset = 300)
    val focusMargin = with(LocalDensity.current) { 8.dp.toPx().toInt() }
    LaunchedEffect(title.state.selection, keyboardOffset, hasFocus) {
        if (hasFocus) {
            bringIntoViewRequester.bringIntoView(
                textResult = layoutResult,
                selection = title.state.selection,
                additionalOffset = focusMargin + toolbarHeight,
            )
        }
    }

    val focusManager = LocalFocusManager.current
    LaunchedEffect(isInEditMode) {
        if (!isInEditMode) {
            focusManager.clearFocus()
        }
    }

    BasicTextField(
        modifier = modifier
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusChanged { focusState ->
                hasFocus = focusState.hasFocus
                if (focusState.hasFocus) {
                    onTitleFocused(title.id)
                }
            },
        onTextLayout = { layoutResult = it() },
        state = title.state,
        textStyle = MaterialTheme.typography.bodyMedium,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            showKeyboardOnFocus = true,
        ),
        decorator = { innerTextField ->
            if (hint != null && title.state.text.isEmpty()) {
                Placeholder(hint = hint)
            }
            innerTextField()
        },
        scrollState = scrollState,
    )
}

@OptIn(ExperimentalFoundationApi::class)
suspend fun BringIntoViewRequester.bringIntoView(
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
        modifier = Modifier.alpha(0.5f),
        text = hint,
        style = MaterialTheme.typography.labelMedium,
        fontStyle = FontStyle.Italic,
    )
}

@PreviewWithBackground
@Composable
private fun NoteContentTitlePreview() {
    SerenityTheme {
        NoteContentTitle(
            title = UiNoteContent.Title(
                id = "1",
                position = 0,
                state = TextFieldState(
                    initialText = "Kotlin is a modern programming language with a " +
                            "lot more syntactic sugar compared to Java, and as such " +
                            "there is equally more black magic",
                )
            ),
            hint = "Text",
        )
    }
}
