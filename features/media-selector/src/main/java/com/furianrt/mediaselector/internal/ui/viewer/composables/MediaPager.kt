package com.furianrt.mediaselector.internal.ui.viewer.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.uikit.components.ButtonPlayPause
import com.furianrt.uikit.components.ControlsAnimatedVisibility
import com.furianrt.uikit.components.VideoSlider
import com.github.panpf.zoomimage.compose.zoom.rememberZoomableState
import com.github.panpf.zoomimage.compose.zoom.zoom
import com.github.panpf.zoomimage.zoom.ScalesCalculator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import androidx.media3.common.MediaItem as ExoMediaItem

private const val SCALE_MULTIPLIER = 1.8f
private const val SLIDER_UPDATE_INTERVAL = 200L

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MediaPager(
    media: ImmutableList<MediaItem>,
    state: PagerState,
    showControls: Boolean,
    onMediaItemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
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
                    onClick = onMediaItemClick,
                )

                is MediaItem.Video -> VideoPage(
                    item = item,
                    isPlaying = state.currentPage == page,
                    showControls = showControls,
                    onClick = onMediaItemClick,
                )
            }
        }
    }
}

@Composable
private fun ImagePage(
    item: MediaItem.Image,
    onClick: () -> Unit,
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
            .zoom(zoomable = zoomableState, onTap = { onClick() }),
        model = request,
        placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
        error = ColorPainter(MaterialTheme.colorScheme.tertiary),
        contentDescription = null,
    )
}

@Composable
internal fun VideoPage(
    item: MediaItem.Video,
    isPlaying: Boolean,
    showControls: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val exoPlayer = remember(item.name) { ExoPlayer.Builder(context).build() }
    val mediaSource = remember(item.name) { ExoMediaItem.fromUri(item.uri) }
    var playing by rememberSaveable(isPlaying) { mutableStateOf(isPlaying) }
    var isEnded by rememberSaveable { mutableStateOf(false) }
    var currentPosition by rememberSaveable { mutableLongStateOf(0L) }
    var isThumbDragging by remember { mutableStateOf(false) }

    val zoomableState = rememberZoomableState()
    LaunchedEffect(zoomableState) {
        zoomableState.scalesCalculator = ScalesCalculator.dynamic(SCALE_MULTIPLIER)
    }

    LaunchedEffect(mediaSource) {
        exoPlayer.setMediaItem(mediaSource)
        exoPlayer.prepare()
        exoPlayer.seekTo(currentPosition)
    }

    LaunchedEffect(playing) {
        if (playing) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isEnded = playbackState == ExoPlayer.STATE_ENDED
                if (isEnded) {
                    playing = false
                    currentPosition = exoPlayer.currentPosition
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    LaunchedEffect(playing, isThumbDragging) {
        while (playing && !isThumbDragging) {
            currentPosition = exoPlayer.currentPosition
            delay(SLIDER_UPDATE_INTERVAL)
        }
    }

    LifecycleStartEffect(playing) {
        if (playing) {
            exoPlayer.play()
        }
        onStopOrDispose {
            exoPlayer.pause()
        }
    }

    val sliderInteractionSource = remember { MutableInteractionSource() }
    LaunchedEffect(sliderInteractionSource) {
        sliderInteractionSource.interactions.collect { interaction ->
            when (interaction) {
                is DragInteraction.Start -> isThumbDragging = true
                is DragInteraction.Stop -> isThumbDragging = false
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .zoom(zoomable = zoomableState, onTap = { onClick() })
                .alpha(0.99f),
            onRelease = { it.player?.release() },
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                }
            },
        )
        ControlsAnimatedVisibility(
            visible = showControls,
            label = "ButtonPlayPauseAnim",
        ) {
            ButtonPlayPause(
                isPlay = !playing,
                onClick = {
                    if (!playing && isEnded) {
                        exoPlayer.seekTo(0)
                    }
                    playing = !playing
                },
            )
        }

        ControlsAnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 90.dp),
            visible = showControls,
            label = "VideoSliderAnim",
        ) {
            VideoSlider(
                value = currentPosition.toFloat(),
                valueRange = 0f..item.duration.toFloat(),
                interactionSource = sliderInteractionSource,
                onValueChange = { currentPosition = it.toLong() },
                onValueChangeFinished = { exoPlayer.seekTo(currentPosition) },
            )
        }
    }
}
