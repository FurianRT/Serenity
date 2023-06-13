package com.furianrt.notecontent.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.uikit.theme.OnTertiaryRippleTheme
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteTags(
    tags: ImmutableSet<UiNoteTag>,
    modifier: Modifier = Modifier,
    date: String? = "Sat 9:12 PM",
) {
    FlowRow(
        modifier = modifier.padding(top = if (tags.isEmpty()) 0.dp else 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        tags.forEach { tag ->
            NoteTagItem(
                modifier = Modifier.padding(bottom = 8.dp),
                tag = tag,
                onClick = {},
            )
        }

        if (date != null) {
            Text(
                modifier = Modifier
                    .padding(end = 12.dp, bottom = 8.dp, top = 12.dp)
                    .weight(1f)
                    .alpha(0.6f),
                text = date,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
fun NoteTagItem(
    tag: UiNoteTag,
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
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                text = tag.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview
@Composable
private fun NoteTagsPreview() {
    SerenityTheme {
        NoteTags(tags = generatePreviewTags())
    }
}

private fun generatePreviewTags(): ImmutableSet<UiNoteTag> = buildSet {
    for (i in 0 until 3) {
        add(UiNoteTag(id = i.toString(), title = "Programming"))
    }
}.toImmutableSet()
