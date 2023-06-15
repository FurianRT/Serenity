package com.furianrt.notecontent.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.uikit.extensions.MeasureUnconstrainedViewWidth
import com.furianrt.uikit.theme.OnTertiaryRippleTheme
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteTags(
    tags: ImmutableList<UiNoteTag>,
    date: String?,
    onTagClick: (tag: UiNoteTag) -> Unit,
    modifier: Modifier = Modifier,
) {
    MeasureUnconstrainedViewWidth(
        viewToMeasure = {
            Text(
                text = date.orEmpty(),
                style = MaterialTheme.typography.labelSmall,
            )
        },
    ) { measuredWidth ->
        FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tags.forEach { tag ->
                when (tag) {
                    is UiNoteTag.Regular -> RegularNoteTagItem(
                        modifier = Modifier.padding(bottom = 8.dp),
                        tag = tag,
                        onClick = { onTagClick(tag) },
                    )

                    is UiNoteTag.Editable -> EditableNoteTagItem(
                        modifier = Modifier.padding(bottom = 8.dp),
                        tag = tag,
                        onClick = { onTagClick(tag) },
                    )

                    is UiNoteTag.Template -> TemplateNoteTagItem(
                        modifier = Modifier.padding(bottom = 8.dp),
                        tag = tag,
                        onClick = { onTagClick(tag) },
                    )
                }
            }

            if (date != null) {
                NoteDateItem(
                    modifier = Modifier.weight(1f),
                    text = date,
                    minWidth = measuredWidth,
                    topPadding = if (tags.isEmpty()) 12.dp else 0.dp,
                    contentAlignment = Alignment.CenterEnd,
                )
            }
        }
    }
}

@Composable
fun RegularNoteTagItem(
    tag: UiNoteTag.Regular,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    CompositionLocalProvider(LocalRippleTheme provides OnTertiaryRippleTheme) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(size = 8.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                text = tag.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
fun EditableNoteTagItem(
    tag: UiNoteTag.Editable,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    CompositionLocalProvider(LocalRippleTheme provides OnTertiaryRippleTheme) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(size = 8.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                text = tag.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
fun TemplateNoteTagItem(
    tag: UiNoteTag.Template,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    CompositionLocalProvider(LocalRippleTheme provides OnTertiaryRippleTheme) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(size = 8.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                text = tag.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
fun NoteDateItem(
    text: String,
    topPadding: Dp,
    minWidth: Dp,
    contentAlignment: Alignment,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {
        Text(
            modifier = Modifier
                .padding(end = 8.dp, bottom = 8.dp, top = topPadding)
                .widthIn(min = minWidth)
                .alpha(0.6f),
            text = text,
            textAlign = TextAlign.End,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Preview
@Composable
private fun NoteTagsPreview() {
    SerenityTheme {
        NoteTags(
            tags = generatePreviewTags(),
            date = "Sat 9:12 PM",
            onTagClick = {},
        )
    }
}

private fun generatePreviewTags(): ImmutableList<UiNoteTag> = buildSet {
    add(UiNoteTag.Regular(id = "0", title = "Programming"))
    add(UiNoteTag.Editable(id = "1", title = "Android"))
    add(UiNoteTag.Template(id = "2", title = "Kotlin"))
}.toImmutableList()
