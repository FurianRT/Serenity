package com.furianrt.mediaview.internal.ui.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.furianrt.mediaview.internal.ui.entities.MediaItem
import com.furianrt.uikit.constants.SystemBarsConstants
import com.furianrt.uikit.extensions.clickableNoRipple
import kotlinx.collections.immutable.ImmutableList

private const val IMAGE_SCALE_ANIM_DURATION = 300
private const val SELECTED_ITEM_HEIGHT_DP = 70f
private const val ITEM_SIZE_DP = 60f
private const val HORIZONTAL_PADDING_DP = 12f

@Composable
internal fun MediaList(
    media: ImmutableList<MediaItem>,
    currentItem: Int,
    state: LazyListState,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var listWidth by remember { mutableIntStateOf(0) }
    val itemWidth = LocalDensity.current.run {
        (SELECTED_ITEM_HEIGHT_DP + HORIZONTAL_PADDING_DP).dp.toPx().toInt()
    }
    LaunchedEffect(currentItem) {
        state.animateScrollToItem(index = currentItem, scrollOffset = (itemWidth - listWidth) / 2)
    }
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(SystemBarsConstants.Color)
            .onSizeChanged { listWidth = it.width },
        state = state,
        contentPadding = PaddingValues(horizontal = HORIZONTAL_PADDING_DP.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        flingBehavior = rememberSnapFlingBehavior(state),
    ) {
        items(
            count = media.count(),
            key = { media[it].name },
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
    val imageScaleValue by animateFloatAsState(
        targetValue = if (isSelected) SELECTED_ITEM_HEIGHT_DP else ITEM_SIZE_DP,
        animationSpec = tween(durationMillis = IMAGE_SCALE_ANIM_DURATION),
        label = "ItemScale"
    )
    val context = LocalContext.current
    val request = remember(item.name) {
        ImageRequest.Builder(context)
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCacheKey(item.name)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCacheKey(item.name)
            .data(item.uri)
            .build()
    }
    AsyncImage(
        modifier = modifier
            .size(width = 60.dp, height = imageScaleValue.dp)
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
}
