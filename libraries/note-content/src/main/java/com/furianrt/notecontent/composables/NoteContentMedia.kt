package com.furianrt.notecontent.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.core.buildImmutableList
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.collections.immutable.ImmutableList

@Composable
fun NoteContentMedia(
    media: ImmutableList<UiNoteContent.MediaBlock.Image>,
    modifier: Modifier = Modifier,
    isEditable: Boolean = false,
) {
    if (media.count() == 1) {
        ImageItem(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp)),
            image = media.first(),
            isRemovable = isEditable,
        )
        return
    }

    if (media.count() == 2) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(120.dp),
        ) {
            ImageItem(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                isRemovable = isEditable,
                image = media[0],
            )
            ImageItem(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                isRemovable = isEditable,
                image = media[1],
            )
        }
        return
    }

    LazyRow(
        modifier = modifier,
        state = rememberLazyListState(),
    ) {
        items(
            count = media.count(),
            key = { media[it].id },
        ) { index ->
            ImageItem(
                modifier = Modifier
                    .size(width = 140.dp, height = 120.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = if (index == 0) 8.dp else 0.dp,
                            bottomStart = if (index == 0) 8.dp else 0.dp,
                            topEnd = if (index == media.lastIndex) 8.dp else 0.dp,
                            bottomEnd = if (index == media.lastIndex) 8.dp else 0.dp,
                        ),
                    ),
                isRemovable = isEditable,
                image = media[index],
            )
        }
    }
}

@Composable
private fun ImageItem(
    image: UiNoteContent.MediaBlock.Image,
    modifier: Modifier = Modifier,
    isRemovable: Boolean = false,
) {
    Box(
        modifier = modifier
            .background(Color(image.uri.toInt())),
    ) {
    }
}

@Preview
@Composable
private fun NoteContentMediaPreview() {
    SerenityTheme {
        NoteContentMedia(
            media = buildImmutableList {
                add(
                    UiNoteContent.MediaBlock.Image(
                        id = "0",
                        uri = Color.Blue.toArgb().toString(),
                        position = 0,
                    ),
                )
            },
        )
    }
}
