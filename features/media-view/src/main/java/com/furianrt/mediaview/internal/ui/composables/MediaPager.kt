package com.furianrt.mediaview.internal.ui.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.furianrt.mediaview.internal.ui.entities.MediaItem
import com.github.panpf.zoomimage.compose.zoom.rememberZoomableState
import com.github.panpf.zoomimage.compose.zoom.zoom
import com.github.panpf.zoomimage.zoom.ScalesCalculator
import kotlinx.collections.immutable.ImmutableList

private const val SCALE_MULTIPLIER = 1.8f

@Composable
internal fun MediaPager(
    media: ImmutableList<MediaItem>,
    state: PagerState,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        modifier = modifier,
        state = state,
        key = { media[it].name },
        pageSpacing = 8.dp,
        beyondViewportPageCount = 1,

        ) { page ->
        when (val item = media[page]) {
            is MediaItem.Image -> ImagePage(
                item = item,
            )

            is MediaItem.Video -> VideoPage(
                item = item,
            )
        }
    }
}

@Composable
private fun ImagePage(
    item: MediaItem.Image,
    modifier: Modifier = Modifier,
) {
    val zoomableState = rememberZoomableState()
    LaunchedEffect(zoomableState) {
        zoomableState.scalesCalculator = ScalesCalculator.dynamic(SCALE_MULTIPLIER)
    }
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
            .fillMaxSize()
            .zoom(zoomableState),
        model = request,
        placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
        error = ColorPainter(MaterialTheme.colorScheme.tertiary),
        contentDescription = null,
    )
}

@Composable
internal fun VideoPage(
    item: MediaItem.Video,
    modifier: Modifier = Modifier,
) {
}
