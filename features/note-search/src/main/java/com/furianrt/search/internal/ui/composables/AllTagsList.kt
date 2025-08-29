package com.furianrt.search.internal.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.furianrt.search.internal.ui.entities.SearchListItem.TagsList
import com.furianrt.uikit.components.TagItem
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun AllTagsList(
    tags: List<TagsList.Tag>,
    modifier: Modifier = Modifier,
    onTagClick: (tag: TagsList.Tag) -> Unit = {},
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tags.forEach { tag ->
            TagItem(
                title = tag.title,
                isRemovable = false,
                onClick = { onTagClick(tag) },
                icon = {
                    Text(
                        modifier = Modifier.alpha(0.5f),
                        text = tag.count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            )
        }
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        AllTagsList(
            tags = buildList {
                repeat(20) { index ->
                    add(TagsList.Tag(title = "Title", count = index + 1))
                }
            },
        )
    }
}
