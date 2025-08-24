package com.furianrt.notelistui.composables.title

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.isEmptyTitle
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.bringIntoView
import com.furianrt.uikit.extensions.getStatusBarHeight
import com.furianrt.uikit.extensions.rememberKeyboardOffsetState
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@OptIn(
    FlowPreview::class,
    ExperimentalLayoutApi::class,
)
@Composable
fun NoteContentTitle(
    title: UiNoteContent.Title,
    modifier: Modifier = Modifier,
    hint: String? = null,
    color: Color? = null,
    fontFamily: FontFamily? = null,
    fontSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize,
    isInEditMode: Boolean = false,
    onTitleFocusChange: (id: String, focused: Boolean) -> Unit = {_, _ -> },
    onTitleTextChange: (id: String) -> Unit = {},
) {
    val readOnly = !isInEditMode && title.isEmptyTitle()
    var layoutResult: TextLayoutResult? by remember { mutableStateOf(null) }
    var hasFocus by remember { mutableStateOf(false) }

    val onTitleTextChangeState by rememberUpdatedState(onTitleTextChange)

    val view = LocalView.current
    val topFocusMargin = with(LocalDensity.current) {
        (ToolbarConstants.toolbarHeight.toPx() + view.getStatusBarHeight() + 64.dp.toPx()).toInt()
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
                        title.bringIntoViewRequester.bringIntoView(
                            textResult = layoutResult,
                            selection = title.state.selection,
                            additionalTopOffset = topFocusMargin.toFloat(),
                            additionalBottomOffset = offset + bottomFocusMargin.toFloat(),
                        )
                    }
                }
        }
        LaunchedEffect(hasFocus) {
            snapshotFlow { title.state.selection }
                .debounce(50)
                .collect { selection ->
                    title.bringIntoViewRequester.bringIntoView(
                        textResult = layoutResult,
                        selection = selection,
                        additionalTopOffset = topFocusMargin.toFloat(),
                        additionalBottomOffset = keyboardOffset + bottomFocusMargin.toFloat(),
                    )
                }
        }
    }

    val focusManager = LocalFocusManager.current
    LaunchedEffect(isInEditMode) {
        if (!isInEditMode) {
            focusManager.clearFocus()
        }
    }

    var titleText by remember { mutableStateOf(title.state.annotatedString) }
    LaunchedEffect(Unit) {
        snapshotFlow { title.state.annotatedString }
            .collect { annotatedString ->
                if (titleText != annotatedString) {
                    onTitleTextChangeState(title.id)
                    titleText = annotatedString
                }
            }
    }

    val isTextEmpty by remember { derivedStateOf { title.state.textValue.text.isEmpty() } }
    val textStyle = if (isTextEmpty) {
        MaterialTheme.typography.labelMedium
    } else {
        MaterialTheme.typography.bodyMedium
    }

    val adjustedStyle = textStyle.copy(
        color = color ?: MaterialTheme.colorScheme.onSurface,
        fontFamily = fontFamily ?: textStyle.fontFamily,
        fontSize = fontSize,
        lineHeight = textStyle.lineHeight * (fontSize.value / textStyle.fontSize.value),
    )

    BasicTextField(
        modifier = modifier
            .bringIntoViewRequester(title.bringIntoViewRequester)
            .focusRequester(title.focusRequester)
            .onFocusChanged { focusState ->
                if (focusState.hasFocus != hasFocus) {
                    hasFocus = focusState.hasFocus
                    onTitleFocusChange(title.id, focusState.hasFocus)
                }
            },
        value = title.state.textValue,
        onTextLayout = { layoutResult = it },
        onValueChange = { title.state.updateValue(it) },
        textStyle = adjustedStyle,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.surfaceContainer),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            showKeyboardOnFocus = true,
            autoCorrectEnabled = true,
        ),
        decorationBox = { innerTextField ->
            AnimatedVisibility(
                visible = hint != null && title.state.annotatedString.isEmpty() && !readOnly,
                enter = fadeIn(tween(200)),
                exit = ExitTransition.None,
            ) {
                Placeholder(
                    style = adjustedStyle,
                    hint = hint.orEmpty(),
                )
            }
            innerTextField()
        },
    )
}

@Composable
private fun Placeholder(
    style: TextStyle,
    hint: String,
) {
    Text(
        modifier = Modifier.alpha(0.5f),
        text = hint,
        style = style,
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
                    fontFamily = UiNoteFontFamily.NotoSans,
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
