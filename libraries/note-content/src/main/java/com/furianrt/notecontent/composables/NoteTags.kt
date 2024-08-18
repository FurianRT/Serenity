package com.furianrt.notecontent.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.furianrt.core.buildImmutableList
import com.furianrt.notecontent.R
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.uikit.extensions.animatePlacement
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.rememberKeyboardOffsetState
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.ImmutableList

private const val ANIM_EDIT_MODE_DURATION = 250

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteTags(
    tags: ImmutableList<UiNoteTag>,
    modifier: Modifier = Modifier,
    date: String? = null,
    isEditable: Boolean = false,
    toolbarHeight: Int = 0,
    onTagClick: ((tag: UiNoteTag.Regular) -> Unit)? = null,
    onTagRemoveClick: (tag: UiNoteTag.Regular) -> Unit = {},
    onDoneEditing: (tag: UiNoteTag.Template) -> Unit = {},
    onTextEntered: () -> Unit = {},
    onTextCleared: () -> Unit = {},
) {
    FlowRow(modifier = modifier) {
        tags.forEach { tag ->
            key(tag.id) {
                when (tag) {
                    is UiNoteTag.Regular -> RegularNoteTagItem(
                        modifier = Modifier.animatePlacement(),
                        tag = tag,
                        isRemovable = isEditable,
                        onClick = onTagClick?.let { { onTagClick(tag) } },
                        onRemoveClick = onTagRemoveClick,
                    )

                    is UiNoteTag.Template -> TemplateNoteTagItem(
                        modifier = Modifier.animatePlacement(),
                        tag = tag,
                        onDoneEditing = onDoneEditing,
                        onTextEntered = onTextEntered,
                        onTextCleared = onTextCleared,
                        toolbarHeight = toolbarHeight,
                    )
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

@Composable
private fun RegularNoteTagItem(
    tag: UiNoteTag.Regular,
    onClick: (() -> Unit)?,
    onRemoveClick: (tag: UiNoteTag.Regular) -> Unit,
    isRemovable: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopStart,
    ) {
        Box(
            modifier = Modifier
                .padding(all = 4.dp)
                .clip(RoundedCornerShape(size = 16.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .applyIf(onClick != null) { Modifier.clickable { onClick?.invoke() } },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                text = tag.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
            )
        }

        AnimatedVisibility(
            visible = isRemovable,
            enter = fadeIn(animationSpec = tween(durationMillis = ANIM_EDIT_MODE_DURATION)),
            exit = fadeOut(animationSpec = tween(durationMillis = ANIM_EDIT_MODE_DURATION)),
        ) {
            DeleteTagButton(onClick = { onRemoveClick(tag) })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TemplateNoteTagItem(
    tag: UiNoteTag.Template,
    modifier: Modifier = Modifier,
    onTextEntered: () -> Unit,
    onTextCleared: () -> Unit,
    onDoneEditing: (tag: UiNoteTag.Template) -> Unit,
    toolbarHeight: Int = 0,
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val focusManager = LocalFocusManager.current

    var hasFocus by remember { mutableStateOf(false) }
    var layoutResult: TextLayoutResult? by remember { mutableStateOf(null) }

    val keyboardOffset by rememberKeyboardOffsetState(minOffset = 300)
    val focusMargin = with(LocalDensity.current) { 24.dp.toPx().toInt() }
    LaunchedEffect(tag.textState.selection, keyboardOffset, hasFocus) {
        if (hasFocus) {
            bringIntoViewRequester.bringIntoView(
                textResult = layoutResult,
                selection = tag.textState.selection,
                additionalOffset = focusMargin + toolbarHeight,
            )
        }
    }

    val hasText by remember { derivedStateOf { tag.textState.text.isNotBlank() } }
    LaunchedEffect(hasText) {
        if (hasText) onTextEntered() else onTextCleared()
    }

    val strokeColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    Box(
        modifier = modifier
            .padding(all = 4.dp)
            .drawWithCache {
                val strokeInterval = 6.dp.toPx()
                val stroke = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(strokeInterval, strokeInterval),
                        phase = 0f
                    )
                )
                onDrawBehind {
                    drawRoundRect(
                        color = strokeColor,
                        style = stroke,
                        cornerRadius = CornerRadius(16.dp.toPx()),
                    )
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        BasicTextField(
            modifier = Modifier
                .bringIntoViewRequester(bringIntoViewRequester)
                .widthIn(min = 80.dp)
                .width(IntrinsicSize.Min)
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .onFocusChanged { focusState ->
                    hasFocus = focusState.hasFocus
                    if (!focusState.hasFocus) {
                        onDoneEditing(tag)
                    }
                },
            state = tag.textState,
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
                    Placeholder()
                }
                innerTextField()
            },
        )
    }
}

@Composable
private fun Placeholder() {
    Text(
        modifier = Modifier.alpha(0.5f),
        text = stringResource(id = R.string.note_content_add_tag_hint),
        style = MaterialTheme.typography.labelSmall,
        fontStyle = FontStyle.Italic,
        maxLines = 1,
    )
}

@Composable
private fun DeleteTagButton(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.errorContainer)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_close),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer,
        )
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
            onTagRemoveClick = { },
            onTagClick = {},
        )
    }
}

private fun generatePreviewTags() = buildImmutableList {
    add(UiNoteTag.Regular(id = "-1", title = "Programming", isRemovable = false))
    add(UiNoteTag.Regular(id = "466", title = "Programming", isRemovable = false))
    add(UiNoteTag.Regular(id = "4642", title = "Programming", isRemovable = false))
    add(UiNoteTag.Regular(id = "1", title = "Android", isRemovable = true))
    add(UiNoteTag.Regular(id = "2", title = "Android", isRemovable = true))
    add(UiNoteTag.Regular(id = "3", title = "Android", isRemovable = true))
    add(UiNoteTag.Regular(id = "4", title = "Kotlin", isRemovable = true))
    add(UiNoteTag.Template())
}
