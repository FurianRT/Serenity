package com.furianrt.search.internal.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.furianrt.core.buildImmutableList
import com.furianrt.search.internal.ui.entities.SearchListItem.TagsList
import com.furianrt.uikit.components.TagItem
import com.furianrt.uikit.extensions.toDateString
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.ImmutableList

private const val DATE_PATTERN = "dd.MM.yy"

@Composable
internal fun SelectedTagsList(
    tags: ImmutableList<TagsList.SelectableItem>,
    modifier: Modifier = Modifier,
    onRemoveTagClick: (tag: TagsList.SelectableItem) -> Unit = {},
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(
            count = tags.count(),
            key = { tags[it].id },
            contentType = { tags[it]::class.simpleName }
        ) { index ->
            when (val tag = tags[index]) {
                is TagsList.Tag -> TagItem(
                    title = tag.title,
                    isRemovable = true,
                    onRemoveClick = { onRemoveTagClick(tag) },
                )

                is TagsList.DateRange -> TagItem(
                    title = if (tag.end != null) {
                        tag.start.toDateString(DATE_PATTERN) +
                                " - " + tag.end.toDateString(DATE_PATTERN)
                    } else {
                        tag.start.toDateString(DATE_PATTERN)
                    },
                    isRemovable = true,
                    onRemoveClick = { onRemoveTagClick(tag) },
                )
            }
        }
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        SelectedTagsList(
            tags = buildImmutableList {
                repeat(10) { index ->
                    add(
                        TagsList.Tag(
                            title = "Title $index",
                            count = index + 1,
                        )
                    )
                }
            },
        )
    }
}
