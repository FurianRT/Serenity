package com.furianrt.mediaselector.internal.ui.selector.composables

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.video.VideoFrameDecoder
import com.furianrt.mediaselector.internal.ui.entities.Constants
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import com.furianrt.uikit.components.DurationBadge
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun VideoItem(
    item: MediaItem.Video,
    modifier: Modifier = Modifier,
    onSelectClick: (item: MediaItem.Video) -> Unit = {},
    onClick: (item: MediaItem.Video) -> Unit = {},
) {
    val imageScaleValue by animateFloatAsState(
        targetValue = if (item.isSelected) Constants.SELECTED_ITEM_SCALE else 1f,
        animationSpec = tween(durationMillis = Constants.IMAGE_SCALE_ANIM_DURATION),
        label = "ItemScale"
    )
    val context = LocalContext.current
    val request = remember(item.id) {
        ImageRequest.Builder(context)
            .size(Constants.REQUESTED_IMAGE_SIZE)
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCacheKey(item.id.toString())
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCacheKey(item.id.toString())
            .data(item.uri)
            .decoderFactory { result, options, _ -> VideoFrameDecoder(result.source, options) }
            .build()
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(2.dp))
            .clickable { onClick(item) },
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = imageScaleValue
                    scaleY = imageScaleValue
                },
            model = request,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
            error = ColorPainter(MaterialTheme.colorScheme.tertiary),
            contentDescription = null,
        )
        DurationBadge(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 4.dp, bottom = 4.dp),
            duration = item.duration,
        )
        CheckBox(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 4.dp, end = 4.dp),
            state = item.state,
            onClick = { onSelectClick(item) },
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        VideoItem(
            item = MediaItem.Video(
                id = 0L,
                name = "",
                uri = Uri.EMPTY,
                ratio = 1f,
                state = SelectionState.Selected(1),
                duration = 10 * 60 * 1000,
            ),
        )
    }
}
