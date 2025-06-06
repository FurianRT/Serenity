package com.furianrt.mediaview.internal.ui.composables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.video.VideoFrameDecoder
import com.furianrt.mediaview.internal.ui.entities.MediaItem
import com.furianrt.uikit.components.DurationBadge
import com.furianrt.uikit.extensions.clickableNoRipple
import kotlinx.collections.immutable.ImmutableList

private const val IMAGE_SCALE_ANIM_DURATION = 300
private val SELECTED_ITEM_HEIGHT = 70.dp
private val ITEM_SIZE = 60.dp
private val HORIZONTAL_PADDING = 12.dp

@Composable
internal fun MediaList(
    state: LazyListState,
    media: ImmutableList<MediaItem>,
    initialMediaIndex: Int,
    currentItem: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemWidth = LocalDensity.current.run {
        (SELECTED_ITEM_HEIGHT + HORIZONTAL_PADDING).toPx().toInt()
    }
    LaunchedEffect(initialMediaIndex) {
        state.scrollToItem(
            index = currentItem,
            scrollOffset = (itemWidth - state.layoutInfo.viewportSize.width) / 2,
        )
    }

    LaunchedEffect(currentItem) {
        state.animateScrollToItem(
            index = currentItem,
            scrollOffset = (itemWidth - state.layoutInfo.viewportSize.width) / 2,
        )
    }
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp),
        state = state,
        contentPadding = PaddingValues(horizontal = HORIZONTAL_PADDING),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        flingBehavior = rememberSnapFlingBehavior(state),
    ) {
        items(
            count = media.count(),
            key = { media[it].id },
            contentType = { media[it]::class.simpleName },
        ) { index ->
            when (val item = media[index]) {
                is MediaItem.Image -> ImageItem(
                    modifier = Modifier.padding(end = if (index == media.lastIndex) 0.dp else 4.dp),
                    item = item,
                    isSelected = currentItem == index,
                    onClick = { onItemClick(index) },
                )

                is MediaItem.Video -> VideoItem(
                    modifier = Modifier.padding(end = if (index == media.lastIndex) 0.dp else 4.dp),
                    item = item,
                    isSelected = currentItem == index,
                    onClick = { onItemClick(index) },
                )
            }
        }
    }
}

@Composable
internal fun ImageItem(
    item: MediaItem.Image,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scaleValue by animateDpAsState(
        targetValue = if (isSelected) SELECTED_ITEM_HEIGHT else ITEM_SIZE,
        animationSpec = tween(durationMillis = IMAGE_SCALE_ANIM_DURATION),
        label = "ItemScale"
    )
    val context = LocalContext.current
    val request = remember(item.id) {
        ImageRequest.Builder(context)
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCacheKey(item.id)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCacheKey(item.id)
            .data(item.uri)
            .build()
    }
    AsyncImage(
        modifier = modifier
            .size(width = 60.dp, height = scaleValue)
            .clip(RoundedCornerShape(4.dp))
            .clickableNoRipple(onClick = onClick),
        model = request,
        contentScale = ContentScale.Crop,
        placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
        error = ColorPainter(MaterialTheme.colorScheme.tertiary),
        contentDescription = null,
    )
}

@Composable
internal fun VideoItem(
    item: MediaItem.Video,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scaleValue by animateDpAsState(
        targetValue = if (isSelected) SELECTED_ITEM_HEIGHT else ITEM_SIZE,
        animationSpec = tween(durationMillis = IMAGE_SCALE_ANIM_DURATION),
        label = "ItemScale"
    )
    val context = LocalContext.current
    val request = remember(item.id) {
        ImageRequest.Builder(context)
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCacheKey(item.id)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCacheKey(item.id)
            .decoderFactory { result, options, _ -> VideoFrameDecoder(result.source, options) }
            .data(item.uri)
            .build()
    }
    Box(modifier = modifier) {
        AsyncImage(
            modifier = Modifier
                .size(width = 60.dp, height = scaleValue)
                .clip(RoundedCornerShape(4.dp))
                .clickableNoRipple(onClick = onClick),
            model = request,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
            error = ColorPainter(MaterialTheme.colorScheme.tertiary),
            contentDescription = null,
        )
        DurationBadge(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 2.dp, bottom = 2.dp),
            duration = item.duration,
        )
    }
}
