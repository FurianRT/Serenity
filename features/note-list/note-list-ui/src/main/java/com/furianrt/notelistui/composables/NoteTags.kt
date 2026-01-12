package com.furianrt.notelistui.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.furianrt.notelistui.R
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.uikit.components.TagItem
import com.furianrt.uikit.extensions.animatePlacementInScope
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.bringIntoView
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.dashedRoundedRectBorder
import com.furianrt.uikit.extensions.pxToDp
import com.furianrt.uikit.extensions.rememberKeyboardOffsetState
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@Composable
fun NoteTags(
    tags: List<UiNoteTag>,
    popupHazeState: HazeState?,
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
    val focusManager = LocalFocusManager.current

    if (tags.isEmpty() && showStub) {
        TemplateNoteTagItem(
            modifier = modifier.alpha(0f),
            tag = UiNoteTag.Template(suggestsProvider = null),
            hazeState = popupHazeState,
            enabled = false,
            onDoneEditing = {},
            onTextEntered = {},
            onTextCleared = {},
            onFocusChanged = {},
        )
    } else {
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
                                    hazeState = popupHazeState,
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
}

@OptIn(ExperimentalLayoutApi::class, FlowPreview::class, ExperimentalMaterial3Api::class)
@Composable
private fun TemplateNoteTagItem(
    tag: UiNoteTag.Template,
    hazeState: HazeState?,
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

    var tagsSuggests by remember { mutableStateOf(emptyList<String>()) }
    var showTagSuggests by remember { mutableStateOf(false)}

    if (tag.suggestsProvider != null) {
        LaunchedEffect(tag.suggestsProvider) {
            snapshotFlow { tag.textState.text }
                .debounce(100)
                .collectLatest { tagText ->
                    if (tagText.isNotBlank()) {
                        val result = tag.suggestsProvider(tagText.toString())
                        if (result.isNotEmpty()) {
                            tagsSuggests = result
                            showTagSuggests = true
                        } else {
                            showTagSuggests = false
                        }
                    } else {
                        showTagSuggests = false
                    }
                }
        }
    }

    val textMeasurer = rememberTextMeasurer()
    val hintStyle = MaterialTheme.typography.labelSmall.copy(fontStyle = FontStyle.Italic)
    val hintText = stringResource(R.string.note_content_add_tag_hint)
    val hintWidth = rememberSaveable {
        textMeasurer.measure(text = hintText, style = hintStyle, maxLines = 1).size.width
    }

    val strokeColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = showTagSuggests,
        onExpandedChange = { expanded ->
            if (!expanded) {
                showTagSuggests = false
            }
        },
    ) {
        BasicTextField(
            modifier = Modifier
                .bringIntoViewRequester(bringIntoViewRequester)
                .padding(4.dp)
                .dashedRoundedRectBorder(color = strokeColor)
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .widthIn(min = hintWidth.pxToDp())
                .width(IntrinsicSize.Min)
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryEditable
                )
                .onFocusChanged { focusState ->
                    onFocusChanged()
                    hasFocus = focusState.hasFocus
                    if (!focusState.hasFocus) {
                        onDoneEditing(tag)
                    }
                },
            state = tag.textState,
            enabled = enabled,
            textStyle = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            lineLimits = TextFieldLineLimits.SingleLine,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.surfaceContainer),
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
        ExposedDropdownMenu(
            modifier = Modifier
                .widthIn(min = 64.dp, max = 200.dp)
                .hazeEffect(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        blurRadius = 12.dp,
                    )
                )
                .background(MaterialTheme.colorScheme.background),
            expanded = showTagSuggests,
            containerColor = Color.Transparent,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 2.dp,
            tonalElevation = 0.dp,
            matchAnchorWidth = false,
            onDismissRequest = { showTagSuggests = false },
        ) {
            tagsSuggests.forEach { suggest ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableNoRipple {
                            tag.textState.edit { 
                                delete(0, length)
                                append(suggest)
                            }
                            showTagSuggests = false
                        }
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    text = suggest,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall
                )
            }
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
            popupHazeState = HazeState(),
            date = "Sat 9:12 PM",
            onTagRemoveClick = {},
            onTagClick = {},
        )
    }
}

private fun generatePreviewTags() = buildList {
    add(UiNoteTag.Regular(title = "Programming", isRemovable = false))
    add(UiNoteTag.Regular(title = "Programming", isRemovable = false))
    add(UiNoteTag.Regular(title = "Programming", isRemovable = false))
    add(UiNoteTag.Regular(title = "Android", isRemovable = true))
    add(UiNoteTag.Regular(title = "Android", isRemovable = true))
    add(UiNoteTag.Regular(title = "Android", isRemovable = true))
    add(UiNoteTag.Regular(title = "Kotlin", isRemovable = true))
    add(UiNoteTag.Template(suggestsProvider = null))
}
