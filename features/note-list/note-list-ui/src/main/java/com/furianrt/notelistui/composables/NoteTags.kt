package com.furianrt.notelistui.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.furianrt.core.buildImmutableList
import com.furianrt.notelistui.R
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.uikit.components.OneTimeEffect
import com.furianrt.uikit.components.TagItem
import com.furianrt.uikit.extensions.animatePlacementInScope
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.bringIntoView
import com.furianrt.uikit.extensions.dashedRoundedRectBorder
import com.furianrt.uikit.extensions.rememberKeyboardOffsetState
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@Composable
fun NoteTags(
    tags: ImmutableList<UiNoteTag>,
    modifier: Modifier = Modifier,
    date: String? = null,
    showStub: Boolean = false,
    isEditable: Boolean = false,
    animateItemsPlacement: Boolean = false,
    onTagClick: ((tag: UiNoteTag.Regular) -> Unit)? = null,
    onTagRemoveClick: (tag: UiNoteTag.Regular) -> Unit = {},
    onDoneEditing: (tag: UiNoteTag.Template) -> Unit = {},
    onTextEntered: () -> Unit = {},
    onTextCleared: () -> Unit = {},
    onFocusChanged: () -> Unit = {},
) {

    if (tags.isEmpty() && showStub) {
        TemplateNoteTagItem(
            modifier = modifier.alpha(0f),
            tag = UiNoteTag.Template(),
            enabled = false,
            onDoneEditing = {},
            onTextEntered = {},
            onTextCleared = {},
            onFocusChanged = {},
        )
        return
    }

    val focusManager = LocalFocusManager.current
    FlowRow(
        modifier = modifier.animateContentSize(
            animationSpec = spring(stiffness = Spring.StiffnessHigh),
        )
    ) {
        LookaheadScope {
            tags.forEach { tag ->
                key(tag.id) {
                    when (tag) {
                        is UiNoteTag.Regular -> TagItem(
                            modifier = Modifier.applyIf(animateItemsPlacement) {
                                Modifier.animatePlacementInScope(this@LookaheadScope)
                            },
                            title = tag.title,
                            isRemovable = isEditable,
                            onClick = onTagClick?.let { { onTagClick(tag) } },
                            onRemoveClick = { onTagRemoveClick(tag) },
                        )

                        is UiNoteTag.Template -> {
                            LaunchedEffect(isEditable) {
                                if (!isEditable) {
                                    focusManager.clearFocus()
                                }
                            }
                            TemplateNoteTagItem(
                                modifier = Modifier.applyIf(animateItemsPlacement) {
                                    Modifier.animatePlacementInScope(this@LookaheadScope)
                                },
                                tag = tag,
                                onDoneEditing = onDoneEditing,
                                onTextEntered = onTextEntered,
                                onTextCleared = onTextCleared,
                                onFocusChanged = onFocusChanged,
                            )
                        }
                    }
                }
            }

            if (date != null) {
                NoteDateItem(
                    modifier = Modifier.weight(1f),
                    text = date,
                    topPadding = if (tags.isEmpty()) 12.dp else 10.dp,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, FlowPreview::class)
@Composable
private fun TemplateNoteTagItem(
    tag: UiNoteTag.Template,
    onTextEntered: () -> Unit,
    onTextCleared: () -> Unit,
    onDoneEditing: (tag: UiNoteTag.Template) -> Unit,
    onFocusChanged: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val focusManager = LocalFocusManager.current

    var hasFocus by remember { mutableStateOf(false) }
    var layoutResult: TextLayoutResult? by remember { mutableStateOf(null) }

    val focusMargin = with(LocalDensity.current) { 108.dp.toPx().toInt() }
    val keyboardOffset by rememberKeyboardOffsetState()
    val imeTarget = WindowInsets.imeAnimationTarget.getBottom(LocalDensity.current)

    val onTextEnteredState by rememberUpdatedState(onTextEntered)
    val onTextClearedState by rememberUpdatedState(onTextCleared)

    val alpha = remember { Animatable(0f) }
    OneTimeEffect {
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 250))
    }

    LaunchedEffect(imeTarget, hasFocus) {
        snapshotFlow { keyboardOffset }
            .debounce(50)
            .collect { offset ->
                if (imeTarget != 0 && hasFocus) {
                    bringIntoViewRequester.bringIntoView(
                        textResult = layoutResult,
                        selection = tag.textState.selection,
                        additionalTopOffset = 0f,
                        additionalBottomOffset = offset + focusMargin.toFloat(),
                    )
                }
            }
    }
    LaunchedEffect(hasFocus) {
        snapshotFlow { tag.textState.selection }
            .collect { selection ->
                if (hasFocus) {
                    bringIntoViewRequester.bringIntoView(
                        textResult = layoutResult,
                        selection = selection,
                        additionalTopOffset = 0f,
                        additionalBottomOffset = keyboardOffset + focusMargin.toFloat(),
                    )
                }
            }
    }

    val hasText by remember { derivedStateOf { tag.textState.text.isNotBlank() } }
    LaunchedEffect(hasText) {
        if (hasText) onTextEnteredState() else onTextClearedState()
    }

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val hintStyle = MaterialTheme.typography.labelSmall.copy(fontStyle = FontStyle.Italic)
    val hintText = stringResource(R.string.note_content_add_tag_hint)
    val hintWidth = remember {
        density.run {
            textMeasurer.measure(text = hintText, style = hintStyle, maxLines = 1).size.width.toDp()
        }
    }

    val strokeColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    Box(modifier = modifier.graphicsLayer { this.alpha = alpha.value }) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .dashedRoundedRectBorder(color = strokeColor),
            contentAlignment = Alignment.Center,
        ) {
            BasicTextField(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .widthIn(min = hintWidth)
                    .width(IntrinsicSize.Min)
                    .onFocusChanged { focusState ->
                        onFocusChanged()
                        hasFocus = focusState.hasFocus
                        if (!focusState.hasFocus) {
                            onDoneEditing(tag)
                        }
                    },
                state = tag.textState,
                enabled = enabled,
                textStyle = MaterialTheme.typography.labelSmall,
                lineLimits = TextFieldLineLimits.SingleLine,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
                onTextLayout = { layoutResult = it() },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                    showKeyboardOnFocus = false,
                ),
                onKeyboardAction = { focusManager.clearFocus() },
                decorator = { innerTextField ->
                    if (tag.textState.text.isEmpty()) {
                        Text(
                            modifier = Modifier.alpha(0.5f),
                            text = hintText,
                            style = hintStyle,
                            maxLines = 1,
                        )
                    }
                    innerTextField()
                },
            )
        }
    }
}

@Composable
private fun NoteDateItem(
    text: String,
    topPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val style = MaterialTheme.typography.labelSmall
    val dateWidth = remember(text) {
        density.run {
            textMeasurer.measure(text = text, style = style, maxLines = 1).size.width.toDp()
        }
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd,
    ) {
        Text(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, bottom = 4.dp, top = topPadding)
                .widthIn(min = dateWidth)
                .alpha(0.6f),
            text = text,
            textAlign = TextAlign.End,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@PreviewWithBackground
@Composable
private fun NoteTagsPreview() {
    SerenityTheme {
        NoteTags(
            tags = generatePreviewTags(),
            date = "Sat 9:12 PM",
            onTagRemoveClick = {},
            onTagClick = {},
        )
    }
}

private fun generatePreviewTags() = buildImmutableList {
    add(UiNoteTag.Regular(title = "Programming", isRemovable = false))
    add(UiNoteTag.Regular(title = "Programming", isRemovable = false))
    add(UiNoteTag.Regular(title = "Programming", isRemovable = false))
    add(UiNoteTag.Regular(title = "Android", isRemovable = true))
    add(UiNoteTag.Regular(title = "Android", isRemovable = true))
    add(UiNoteTag.Regular(title = "Android", isRemovable = true))
    add(UiNoteTag.Regular(title = "Kotlin", isRemovable = true))
    add(UiNoteTag.Template())
}
