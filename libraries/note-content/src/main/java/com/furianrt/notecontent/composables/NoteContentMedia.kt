package com.furianrt.notecontent.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.furianrt.core.buildImmutableList
import com.furianrt.notecontent.entities.UiNoteContent.MediaBlock
import com.furianrt.uikit.theme.Colors
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.collections.immutable.ImmutableList

@Composable
fun NoteContentMedia(
    block: MediaBlock,
    modifier: Modifier = Modifier,
    isEditable: Boolean = false,
) {
    when (block.media.count()) {
        1 -> OneMediaHolder(
            modifier = modifier.sizeIn(minHeight = 90.dp, maxHeight = 160.dp),
            media = block.media[0],
        )

        2 -> RowMediaHolder(modifier = modifier.height(120.dp), media = block.media)
        3 -> RowMediaHolder(modifier = modifier.height(110.dp), media = block.media)
        4 -> FourMediaHolder(modifier = modifier.height(160.dp), media = block.media)
        else -> ManyMediaHolder(modifier = modifier, media = block.media)
    }
}

@Composable
private fun OneMediaHolder(
    media: MediaBlock.Media,
    modifier: Modifier = Modifier,
) {
    if (media.ratio >= 1.4f) {
        MediaItem(
            modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
            media = media,
        )
    } else {
        Box(
            modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            MediaItem(media = media, contentScale = ContentScale.Fit)
        }
    }
}

@Composable
private fun RowMediaHolder(
    media: ImmutableList<MediaBlock.Media>,
    modifier: Modifier = Modifier,
) {
    val maxImagesCount = 4
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        val subList = media.take(maxImagesCount)
        val ratioSum = subList.sumOf { it.ratio.toDouble() }.toFloat()
        subList.forEachIndexed { index, item ->
            val weight = if (media.count() >= maxImagesCount) 1f else item.ratio / ratioSum
            MediaItem(
                modifier = Modifier.weight(weight),
                media = item,
                offscreenImageCount = if (index == subList.lastIndex) {
                    (media.count() - maxImagesCount).coerceAtLeast(0)
                } else {
                    0
                },
            )
        }
    }
}

@Composable
private fun FourMediaHolder(
    media: ImmutableList<MediaBlock.Media>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        MediaItem(modifier = Modifier.weight(0.55f), media = media[0])
        Column(
            modifier = Modifier.weight(0.45f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            MediaItem(modifier = Modifier.weight(0.6f), media = media[1])
            RowMediaHolder(modifier = Modifier.weight(0.4f), media = media.subList(2, 4))
        }
    }
}

@Composable
private fun ManyMediaHolder(
    media: ImmutableList<MediaBlock.Media>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        RowMediaHolder(modifier = Modifier.height(120.dp), media = media.subList(0, 2))
        RowMediaHolder(modifier = Modifier.height(80.dp), media = media.subList(2, media.count()))
    }
}

@Composable
private fun MediaItem(
    media: MediaBlock.Media,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    offscreenImageCount: Int = 0,
) {
    if (media is MediaBlock.Media.Image) {
        ImageItem(media, modifier, contentScale, offscreenImageCount)
    }
}

@Composable
private fun ImageItem(
    image: MediaBlock.Media.Image,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    offscreenImageCount: Int = 0,
) {
    if (offscreenImageCount == 0) {
        AsyncImage(
            modifier = modifier.clip(RoundedCornerShape(4.dp)),
            model = ImageRequest.Builder(LocalContext.current).data(image.uri).build(),
            contentDescription = null,
            contentScale = contentScale,
        )
    } else {
        Box(
            modifier = modifier.clip(RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center,
            propagateMinConstraints = true,
        ) {
            AsyncImage(
                modifier = Modifier.drawWithContent {
                    drawContent()
                    drawRect(color = Color.Black, alpha = 0.65f)
                },
                model = ImageRequest.Builder(LocalContext.current).data(image.uri).build(),
                contentDescription = null,
                contentScale = contentScale,
            )
            Text(
                modifier = Modifier.wrapContentSize(),
                text = "+$offscreenImageCount",
                textAlign = TextAlign.Center,
                color = Colors.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

@Preview
@Composable
private fun NoteContentMediaPreview() {
    SerenityTheme {
        NoteContentMedia(
            block = MediaBlock(
                id = "1",
                position = 0,
                media = buildImmutableList {
                    add(MediaBlock.Media.Image(id = "0", uri = "", position = 0, ratio = 1.2f))
                },
            ),
        )
    }
}
