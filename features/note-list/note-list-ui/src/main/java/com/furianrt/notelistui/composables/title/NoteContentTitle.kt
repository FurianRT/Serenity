package com.furianrt.notelistui.composables.title

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.bringIntoView
import com.furianrt.uikit.extensions.getStatusBarHeight
import com.furianrt.uikit.extensions.rememberKeyboardOffsetState
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class, ExperimentalLayoutApi::class)
@Composable
fun NoteContentTitle(
    title: UiNoteContent.Title,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = FocusRequester(),
    hint: String? = null,
    color: Color = MaterialTheme.typography.bodyMedium.color,
    fontFamily: FontFamily? = MaterialTheme.typography.bodyMedium.fontFamily,
    fontSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize,
    isInEditMode: Boolean = false,
    onTitleFocused: (id: String) -> Unit = {},
    onTitleTextChange: (id: String) -> Unit = {},
) {
    var layoutResult: TextLayoutResult? by remember { mutableStateOf(null) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    var hasFocus by remember { mutableStateOf(false) }

    val view = LocalView.current
    val topFocusMargin = with(LocalDensity.current) {
        (ToolbarConstants.toolbarHeight.toPx() + view.getStatusBarHeight()).toInt()
    }
    val bottomFocusMargin = with(LocalDensity.current) { 64.dp.toPx().toInt() }
    if (hasFocus) {
        val keyboardOffset by rememberKeyboardOffsetState()
        val imeTarget = WindowInsets.imeAnimationTarget.getBottom(LocalDensity.current)
        LaunchedEffect(imeTarget) {
            snapshotFlow { keyboardOffset }
                .debounce(50)
                .collect { offset ->
                    if (imeTarget != 0) {
                        bringIntoViewRequester.bringIntoView(
                            textResult = layoutResult,
                            selection = title.state.selection,
                            additionalTopOffset = topFocusMargin,
                            additionalBottomOffset = offset + bottomFocusMargin,
                        )
                    }
                }
        }
        LaunchedEffect(title.state.selection, hasFocus) {
            delay(50)
            bringIntoViewRequester.bringIntoView(
                textResult = layoutResult,
                selection = title.state.selection,
                additionalTopOffset = topFocusMargin,
                additionalBottomOffset = keyboardOffset + bottomFocusMargin,
            )
        }
    }

    val focusManager = LocalFocusManager.current
    LaunchedEffect(isInEditMode) {
        if (!isInEditMode) {
            focusManager.clearFocus()
        }
    }

    var titleText by remember { mutableStateOf(title.state.annotatedString) }
    LaunchedEffect(title.state.annotatedString) {
        if (titleText != title.state.annotatedString) {
            onTitleTextChange(title.id)
            titleText = title.state.annotatedString
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
        value = title.state.textValue,
        onTextLayout = { layoutResult = it },
        onValueChange = { title.state.updateValue(it) },
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = color,
            fontFamily = fontFamily,
            fontSize = fontSize,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight *
                    (fontSize.value / MaterialTheme.typography.bodyMedium.fontSize.value),
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            showKeyboardOnFocus = true,
        ),
        decorationBox = { innerTextField ->
            if (hint != null && title.state.annotatedString.isEmpty()) {
                Placeholder(
                    hint = hint,
                    color = color,
                    fontFamily = fontFamily,
                    fontSize = fontSize,
                )
            }
            innerTextField()
        },
    )
}

@Composable
private fun Placeholder(
    hint: String,
    color: Color,
    fontFamily: FontFamily?,
    fontSize: TextUnit,
) {
    Text(
        modifier = Modifier.alpha(0.5f),
        text = hint,
        style = MaterialTheme.typography.labelMedium.copy(
            color = color,
            fontFamily = fontFamily,
            fontSize = fontSize,
            lineHeight = MaterialTheme.typography.labelMedium.lineHeight *
                    (fontSize.value / MaterialTheme.typography.labelMedium.fontSize.value)
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
                state = NoteTitleState(
                    initialText = AnnotatedString(
                        text = "Kotlin is a modern programming language with a " +
                                "lot more syntactic sugar compared to Java, and as such " +
                                "there is equally more black magic",
                    ),
                ),
            ),
            hint = "Text",
        )
    }
}
