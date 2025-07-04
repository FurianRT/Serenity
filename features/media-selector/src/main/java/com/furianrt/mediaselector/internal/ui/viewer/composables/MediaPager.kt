package com.furianrt.mediaselector.internal.ui.viewer.composables

import android.view.LayoutInflater
import androidx.annotation.OptIn
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
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
import com.furianrt.uikit.R as uiR

private const val SCALE_MULTIPLIER = 1.8f
private const val SLIDER_UPDATE_INTERVAL = 25L

@Composable
internal fun MediaPager(
    media: ImmutableList<MediaItem>,
    state: PagerState,
    showControls: Boolean,
    onThumbDrugStart: () -> Unit,
    onThumbDrugEnd: () -> Unit,
    onMediaItemClick: () -> Unit,
    onPause: (index: Int) -> Unit,
    onResume: (index: Int) -> Unit,
    onEnded: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        modifier = modifier,
        state = state,
        key = { media[it].id },
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
                onThumbDrugStart = onThumbDrugStart,
                onThumbDrugEnd = onThumbDrugEnd,
                onPause = { onPause(page) },
                onResume = { onResume(page) },
                onEnded = { onEnded(page) },
            )
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
    val request = remember(item.id) {
        ImageRequest.Builder(context)
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCacheKey(item.id.toString())
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCacheKey(item.id.toString())
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

@OptIn(UnstableApi::class)
@Composable
internal fun VideoPage(
    item: MediaItem.Video,
    isPlaying: Boolean,
    showControls: Boolean,
    onThumbDrugStart: () -> Unit,
    onThumbDrugEnd: () -> Unit,
    onClick: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onEnded: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val exoPlayer = remember(item.id) {
        ExoPlayer.Builder(context)
            .setLoadControl(
                DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                        DefaultLoadControl.DEFAULT_MIN_BUFFER_MS / 20,
                        DefaultLoadControl.DEFAULT_MAX_BUFFER_MS / 20,
                        DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS / 10,
                        DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS / 10
                    )
                    .build(),
            )
            .build()
    }
    val mediaSource = remember(item.id) { ExoMediaItem.fromUri(item.uri) }
    var isEnded by rememberSaveable(item.id) { mutableStateOf(false) }
    var playing by rememberSaveable(isPlaying, item.id) { mutableStateOf(isPlaying && !isEnded) }
    var currentPosition by rememberSaveable(item.id) { mutableLongStateOf(0L) }
    var isThumbDragging by remember(item.id) { mutableStateOf(false) }

    val zoomableState = rememberZoomableState()
    LaunchedEffect(zoomableState) {
        zoomableState.scalesCalculator = ScalesCalculator.dynamic(SCALE_MULTIPLIER)
    }

    LaunchedEffect(item.id) {
        exoPlayer.setMediaItem(mediaSource)
        exoPlayer.prepare()
        exoPlayer.seekTo(currentPosition)
    }

    LaunchedEffect(playing) {
        if (playing) {
            exoPlayer.play()
            onResume()
        } else {
            exoPlayer.pause()
            onPause()
        }
    }

    DisposableEffect(item.id) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (isEnded && playbackState == ExoPlayer.STATE_BUFFERING) {
                    playing = true
                    onResume()
                }
                isEnded = playbackState == ExoPlayer.STATE_ENDED
                if (isEnded) {
                    currentPosition = exoPlayer.currentPosition
                    playing = false
                    onPause()
                    onEnded()
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(playing, isThumbDragging) {
        while (playing && !isThumbDragging) {
            currentPosition = exoPlayer.currentPosition
            delay(SLIDER_UPDATE_INTERVAL)
        }
    }

    LifecycleStartEffect(playing) {
        onStopOrDispose {
            exoPlayer.pause()
        }
    }

    val sliderInteractionSource = remember { MutableInteractionSource() }
    LaunchedEffect(sliderInteractionSource) {
        sliderInteractionSource.interactions.collect { interaction ->
            when (interaction) {
                is DragInteraction.Start -> {
                    onThumbDrugStart()
                    isThumbDragging = true
                }

                is DragInteraction.Stop -> {
                    onThumbDrugEnd()
                    isThumbDragging = false
                }
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
                .zoom(zoomable = zoomableState, onTap = { onClick() }),
            onRelease = { it.player?.release() },
            factory = { ctx ->
                val playerView = LayoutInflater.from(ctx)
                    .inflate(uiR.layout.layout_player_view, null) as PlayerView
                playerView.apply {
                    player = exoPlayer
                    useController = false
                    setEnableComposeSurfaceSyncWorkaround(true)
                }
            },
        )
        ControlsAnimatedVisibility(
            visible = showControls,
            label = "ButtonPlayPauseAnim",
        ) {
            ButtonPlayPause(
                isPlay = !playing || isEnded,
                onClick = {
                    if (isEnded) {
                        exoPlayer.seekTo(0)
                        playing = true
                    } else {
                        playing = !playing
                    }
                },
            )
        }

        ControlsAnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 90.dp),
            visible = showControls,
            label = "VideoSliderAnim",
        ) {
            VideoSlider(
                progress = currentPosition.toFloat() / item.duration,
                duration = item.duration,
                interactionSource = sliderInteractionSource,
                onProgressChange = { progress ->
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                    currentPosition = (item.duration * progress).toLong()
                },
                onProgressChangeFinished = { exoPlayer.seekTo(currentPosition) },
            )
        }
    }
}
