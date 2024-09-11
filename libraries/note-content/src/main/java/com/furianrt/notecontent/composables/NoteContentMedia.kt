package com.furianrt.notecontent.composables

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.furianrt.core.buildImmutableList
import com.furianrt.notecontent.entities.UiNoteContent.MediaBlock
import com.furianrt.notecontent.entities.contentHeightDp
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.shimmer
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.ImmutableList

@Composable
fun NoteContentMedia(
    block: MediaBlock,
    modifier: Modifier = Modifier,
    isEditable: Boolean = false,
) {
    val loadingColor = MaterialTheme.colorScheme.tertiaryContainer
    val loadingModifier = Modifier
        .drawWithContent {
            drawContent()
            drawRoundRect(
                color = loadingColor,
                cornerRadius = CornerRadius(8.dp.toPx()),
            )
        }
        .shimmer()
    when (block.media.count()) {
        1 -> OneMediaHolder(
            modifier = modifier
                .sizeIn(maxHeight = block.contentHeightDp.dp)
                .applyIf(block.showLoading) { loadingModifier },
            media = block.media[0],
        )

        2 -> RowMediaHolder(
            modifier = modifier
                .height(block.contentHeightDp.dp)
                .clip(RoundedCornerShape(8.dp))
                .applyIf(block.showLoading) { loadingModifier },
            media = block.media,
        )

        3 -> RowMediaHolder(
            modifier = modifier
                .height(block.contentHeightDp.dp)
                .clip(RoundedCornerShape(8.dp))
                .applyIf(block.showLoading) { loadingModifier },
            media = block.media,
        )

        4 -> FourMediaHolder(
            modifier = modifier
                .height(block.contentHeightDp.dp)
                .applyIf(block.showLoading) { loadingModifier },
            media = block.media
        )

        else -> ManyMediaHolder(
            modifier = modifier
                .height(block.contentHeightDp.dp)
                .applyIf(block.showLoading) { loadingModifier },
            media = block.media
        )
    }
}

@Composable
private fun OneMediaHolder(
    media: MediaBlock.Media,
    modifier: Modifier = Modifier,
) {
    if (media.ratio >= 1.4f) {
        MediaItem(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            cornerRadius = 0.dp,
            media = media,
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            MediaItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .blur(32.dp),
                media = media,
                cornerRadius = 0.dp,
            )
            MediaItem(
                modifier = Modifier.aspectRatio(
                    ratio = media.ratio,
                    matchHeightConstraintsFirst = true,
                ),
                media = media,
                cornerRadius = 0.dp,
                contentScale = ContentScale.Fit,
            )
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
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
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
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        RowMediaHolder(modifier = Modifier.weight(0.63f), media = media.subList(0, 2))
        RowMediaHolder(modifier = Modifier.weight(0.37f), media = media.subList(2, media.count()))
    }
}

@Composable
private fun MediaItem(
    media: MediaBlock.Media,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    cornerRadius: Dp = 4.dp,
    offscreenImageCount: Int = 0,
) {
    if (media is MediaBlock.Image) {
        ImageItem(media, modifier, contentScale, cornerRadius, offscreenImageCount)
    }
}

@Composable
private fun ImageItem(
    image: MediaBlock.Image,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    cornerRadius: Dp = 4.dp,
    offscreenImageCount: Int = 0,
) {
    val request = ImageRequest.Builder(LocalContext.current)
        .diskCachePolicy(CachePolicy.DISABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .memoryCacheKey(image.id)
        .data(image.uri)
        .build()

    if (offscreenImageCount == 0) {
        AsyncImage(
            modifier = modifier.clip(RoundedCornerShape(cornerRadius)),
            model = request,
            placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
            contentDescription = null,
            contentScale = contentScale,
        )
    } else {
        Box(
            modifier = modifier.clip(RoundedCornerShape(cornerRadius)),
            contentAlignment = Alignment.Center,
            propagateMinConstraints = true,
        ) {
            AsyncImage(
                modifier = Modifier.drawWithContent {
                    drawContent()
                    drawRect(color = Color.Black, alpha = 0.4f)
                },
                model = request,
                placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
                contentDescription = null,
                contentScale = contentScale,
            )
            Text(
                modifier = Modifier.wrapContentSize(),
                text = "+$offscreenImageCount",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

@PreviewWithBackground
@Composable
private fun NoteContentOneMediaPreview() {
    SerenityTheme {
        NoteContentMedia(
            block = MediaBlock(
                id = "1",
                media = buildImmutableList {
                    add(MediaBlock.Image(id = "0", uri = Uri.EMPTY, addedTime = 0, ratio = 1.1f))
                },
            ),
        )
    }
}

@PreviewWithBackground
@Composable
private fun NoteContentTwoMediaPreview() {
    SerenityTheme {
        NoteContentMedia(
            block = MediaBlock(
                id = "1",
                media = buildImmutableList {
                    add(MediaBlock.Image(id = "0", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "1", uri = Uri.EMPTY, addedTime = 0, ratio = 1f))
                },
            ),
        )
    }
}

@PreviewWithBackground
@Composable
private fun NoteContentThreeMediaPreview() {
    SerenityTheme {
        NoteContentMedia(
            block = MediaBlock(
                id = "1",
                media = buildImmutableList {
                    add(MediaBlock.Image(id = "0", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "1", uri = Uri.EMPTY, addedTime = 0, ratio = 1f))
                    add(MediaBlock.Image(id = "2", uri = Uri.EMPTY, addedTime = 0, ratio = 0.4f))
                },
            ),
        )
    }
}

@PreviewWithBackground
@Composable
private fun NoteContentFourMediaPreview() {
    SerenityTheme {
        NoteContentMedia(
            block = MediaBlock(
                id = "1",
                media = buildImmutableList {
                    add(MediaBlock.Image(id = "0", uri = Uri.EMPTY, addedTime = 0, ratio = 0.4f))
                    add(MediaBlock.Image(id = "1", uri = Uri.EMPTY, addedTime = 0, ratio = 1.8f))
                    add(MediaBlock.Image(id = "2", uri = Uri.EMPTY, addedTime = 0, ratio = 1f))
                    add(MediaBlock.Image(id = "3", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                },
            ),
        )
    }
}

@PreviewWithBackground
@Composable
private fun NoteContentFiveMediaPreview() {
    SerenityTheme {
        NoteContentMedia(
            block = MediaBlock(
                id = "1",
                media = buildImmutableList {
                    add(MediaBlock.Image(id = "0", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "1", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "2", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "3", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "4", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                },
            ),
        )
    }
}

@PreviewWithBackground
@Composable
private fun NoteContentSixMediaPreview() {
    SerenityTheme {
        NoteContentMedia(
            block = MediaBlock(
                id = "1",
                media = buildImmutableList {
                    add(MediaBlock.Image(id = "0", uri = Uri.EMPTY, addedTime = 0, ratio = 1f))
                    add(MediaBlock.Image(id = "1", uri = Uri.EMPTY, addedTime = 0, ratio = 1.5f))
                    add(MediaBlock.Image(id = "2", uri = Uri.EMPTY, addedTime = 0, ratio = 1f))
                    add(MediaBlock.Image(id = "3", uri = Uri.EMPTY, addedTime = 0, ratio = 1.9f))
                    add(MediaBlock.Image(id = "4", uri = Uri.EMPTY, addedTime = 0, ratio = 1f))
                    add(MediaBlock.Image(id = "5", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                },
            ),
        )
    }
}

@PreviewWithBackground
@Composable
private fun NoteContentSevenMediaPreview() {
    SerenityTheme {
        NoteContentMedia(
            block = MediaBlock(
                id = "1",
                media = buildImmutableList {
                    add(MediaBlock.Image(id = "0", uri = Uri.EMPTY, addedTime = 0, ratio = 1.5f))
                    add(MediaBlock.Image(id = "1", uri = Uri.EMPTY, addedTime = 0, ratio = 1f))
                    add(MediaBlock.Image(id = "2", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "3", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "4", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "5", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "6", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                },
            ),
        )
    }
}
