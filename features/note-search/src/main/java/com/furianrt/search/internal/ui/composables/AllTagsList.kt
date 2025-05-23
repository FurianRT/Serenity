package com.furianrt.search.internal.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.core.buildImmutableList
import com.furianrt.notesearch.R
import com.furianrt.search.internal.ui.entities.SearchListItem.TagsList
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.components.FlowRowWithLimit
import com.furianrt.uikit.components.TagItem
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.ImmutableList

private const val MAX_INITIAL_ROWS_COUNT = 3

@Composable
internal fun AllTagsList(
    tags: ImmutableList<TagsList.Tag>,
    modifier: Modifier = Modifier,
    onTagClick: (tag: TagsList.Tag) -> Unit = {},
) {
    var maxRowsCount by rememberSaveable { mutableIntStateOf(MAX_INITIAL_ROWS_COUNT) }
    FlowRowWithLimit(
        modifier = modifier.fillMaxWidth(),
        maxRowsCount = maxRowsCount,
        verticalSpacing = 8.dp,
        horizontalSpacing = 4.dp,
        massage = {
            TagItem(
                title = stringResource(R.string.notes_search_all_tags_title),
                isRemovable = false,
                onClick = { maxRowsCount = Int.MAX_VALUE },
                icon = {
                    Icon(
                        modifier = Modifier.alpha(0.5f),
                        painter = painterResource(uiR.drawable.ic_search_small),
                        tint = Color.Unspecified,
                        contentDescription = null,
                    )
                }
            )
        }
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
            tags = buildImmutableList {
                repeat(20) { index ->
                    add(TagsList.Tag(title = "Title", count = index + 1))
                }
            },
        )
    }
}
