package com.furianrt.notelistui.composables

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.cursorCoordinates
import com.furianrt.uikit.extensions.getStatusBarHeight
import com.furianrt.uikit.extensions.rememberKeyboardOffsetState
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.coroutines.delay

@Composable
fun NoteContentTitle(
    title: UiNoteContent.Title,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = FocusRequester(),
    scrollState: ScrollState = rememberScrollState(),
    hint: String? = null,
    color: Color = MaterialTheme.typography.bodyMedium.color,
    fontFamily: FontFamily? = MaterialTheme.typography.bodyMedium.fontFamily,
    isInEditMode: Boolean = false,
    onTitleFocused: (id: String) -> Unit = {},
    onTitleTextChange: (id: String) -> Unit = {},
) {
    var layoutResult: TextLayoutResult? by remember { mutableStateOf(null) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    var hasFocus by remember { mutableStateOf(false) }

    val view = LocalView.current
    val keyboardOffset by rememberKeyboardOffsetState(minOffset = 300)
    val topFocusMargin = with(LocalDensity.current) {
        (ToolbarConstants.toolbarHeight.toPx() + view.getStatusBarHeight()).toInt()
    }
    val bottomFocusMargin = with(LocalDensity.current) { 64.dp.toPx().toInt() }
    LaunchedEffect(title.state.selection, keyboardOffset, hasFocus) {
        if (hasFocus) {
            delay(50)
            bringIntoViewRequester.bringIntoView(
                textResult = layoutResult,
                selection = title.state.selection,
                additionalTopOffset = topFocusMargin,
                additionalBottomOffset = bottomFocusMargin,
            )
        }
    }

    val focusManager = LocalFocusManager.current
    LaunchedEffect(isInEditMode) {
        if (!isInEditMode) {
            focusManager.clearFocus()
        }
    }

    var titleText by remember { mutableStateOf(title.state.text) }
    LaunchedEffect(title.state.text) {
        if (titleText != title.state.text) {
            onTitleTextChange(title.id)
            titleText = title.state.text
        }
    }

    BasicTextField(
        modifier = modifier
            .bringIntoViewRequester(bringIntoViewRequester)
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                hasFocus = focusState.hasFocus
                if (focusState.hasFocus) {
                    onTitleFocused(title.id)
                }
            },
        onTextLayout = { layoutResult = it() },
        state = title.state,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = color ,
            fontFamily = fontFamily,
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            showKeyboardOnFocus = true,
        ),
        decorator = { innerTextField ->
            if (hint != null && title.state.text.isEmpty()) {
                Placeholder(hint = hint, color = color, fontFamily = fontFamily)
            }
            innerTextField()
        },
        scrollState = scrollState,
    )
}

suspend fun BringIntoViewRequester.bringIntoView(
    textResult: TextLayoutResult?,
    selection: TextRange,
    additionalTopOffset: Int,
    additionalBottomOffset: Int,
) {
    val (top, bottom) = textResult?.cursorCoordinates(selection) ?: return
    bringIntoView(
        Rect(
            left = 0f,
            top = top - additionalTopOffset,
            right = 0f,
            bottom = bottom + additionalBottomOffset,
        ),
    )
}

@Composable
private fun Placeholder(
    hint: String,
    color: Color,
    fontFamily: FontFamily?,
) {
    Text(
        modifier = Modifier.alpha(0.5f),
        text = hint,
        style = MaterialTheme.typography.labelMedium.copy(
            color = color,
            fontFamily = fontFamily,
        ),
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
