package com.furianrt.mediaselector.internal.ui.viewer.composables

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.annotation.OptIn
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.uikit.components.ButtonPlayPause
import com.furianrt.uikit.components.ControlsAnimatedVisibility
import com.furianrt.uikit.components.VideoSlider
import com.furianrt.uikit.extensions.dpToPx
import com.github.panpf.zoomimage.CoilZoomAsyncImage
import com.github.panpf.zoomimage.compose.zoom.rememberZoomableState
import com.github.panpf.zoomimage.compose.zoom.zoom
import com.github.panpf.zoomimage.rememberCoilZoomState
import com.github.panpf.zoomimage.zoom.ScalesCalculator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import androidx.media3.common.MediaItem as ExoMediaItem
import com.furianrt.uikit.R as uiR

private const val SCALE_MULTIPLIER = 1.8f
private const val SLIDER_UPDATE_INTERVAL = 25L
private const val DRAG_TO_CLOSE_DELIMITER = 4f

@Composable
internal fun MediaPager(
    media: ImmutableList<MediaItem>,
    state: PagerState,
    showControls: Boolean,
    onThumbDragStart: () -> Unit,
    onThumbDragEnd: () -> Unit,
    onMediaItemClick: () -> Unit,
    onPause: (index: Int) -> Unit,
    onResume: (index: Int) -> Unit,
    onEnded: (index: Int) -> Unit,
    onCloseDrag: (value: Float) -> Unit,
    onCloseRequest: () -> Unit,
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
                onCloseDrag = onCloseDrag,
                onCloseRequest = onCloseRequest,
            )

            is MediaItem.Video -> VideoPage(
                item = item,
                isPlaying = state.currentPage == page,
                showControls = showControls,
                onClick = onMediaItemClick,
                onThumbDragStart = onThumbDragStart,
                onThumbDragEnd = onThumbDragEnd,
                onPause = { onPause(page) },
                onResume = { onResume(page) },
                onEnded = { onEnded(page) },
                onCloseDrag = onCloseDrag,
                onCloseRequest = onCloseRequest,
            )
        }
    }
}

@Composable
private fun ImagePage(
    item: MediaItem.Image,
    onClick: () -> Unit,
    onCloseDrag: (value: Float) -> Unit,
    onCloseRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val zoomState = rememberCoilZoomState()
    LaunchedEffect(zoomState.zoomable) {
        zoomState.zoomable.scalesCalculator = ScalesCalculator.dynamic(SCALE_MULTIPLIER)
    }

    var dragOffset by remember { mutableFloatStateOf(0f) }
    val dragState = rememberDraggableState { delta ->
        dragOffset = (dragOffset + delta).coerceAtLeast(0f)
    }
    val enableDrag by remember {
        derivedStateOf {
            zoomState.zoomable.minScale == zoomState.zoomable.transform.scaleX &&
                    zoomState.zoomable.minScale == zoomState.zoomable.transform.scaleY
        }
    }

    val request = remember(item.id) {
        ImageRequest.Builder(context)
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCacheKey(item.id.toString())
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCacheKey(item.id.toString())
            .data(item.uri)
            .build()
    }
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val maxHeightPx = this.maxHeight.dpToPx()
        val offsetToClose = maxHeightPx / DRAG_TO_CLOSE_DELIMITER
        var showContent by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            snapshotFlow { dragOffset }
                .collect { onCloseDrag((offsetToClose - it) / offsetToClose) }
        }

        if (showContent) {
            CoilZoomAsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .draggable(
                        state = dragState,
                        enabled = enableDrag,
                        orientation = Orientation.Vertical,
                        onDragStopped = {
                            if (dragOffset > offsetToClose) {
                                showContent = false
                                onCloseRequest()
                            } else {
                                AnimationState(dragOffset).animateTo(
                                    targetValue = 0f,
                                    block = { dragOffset = value },
                                )
                            }
                        },
                    )
                    .graphicsLayer {
                        translationY = dragOffset
                        alpha =
                            ((offsetToClose - dragOffset * 0.4f) / offsetToClose).coerceAtLeast(0.5f)
                    },
                zoomState = zoomState,
                model = request,
                onTap = { onClick() },
                scrollBar = null,
                placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
                error = ColorPainter(MaterialTheme.colorScheme.tertiary),
                contentDescription = null,
            )
        }
    }
}

@SuppressLint("InflateParams")
@OptIn(UnstableApi::class)
@Composable
internal fun VideoPage(
    item: MediaItem.Video,
    isPlaying: Boolean,
    showControls: Boolean,
    onThumbDragStart: () -> Unit,
    onThumbDragEnd: () -> Unit,
    onClick: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onEnded: () -> Unit,
    onCloseDrag: (value: Float) -> Unit,
    onCloseRequest: () -> Unit,
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

    var dragOffset by remember { mutableFloatStateOf(0f) }
    val dragState = rememberDraggableState { delta ->
        dragOffset = (dragOffset + delta).coerceAtLeast(0f)
    }
    val enableDrag by remember {
        derivedStateOf {
            zoomableState.minScale == zoomableState.transform.scaleX &&
                    zoomableState.minScale == zoomableState.transform.scaleY
        }
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
                    onThumbDragStart()
                    isThumbDragging = true
                }

                is DragInteraction.Stop -> {
                    onThumbDragEnd()
                    isThumbDragging = false
                }
            }
        }
    }

    var showContent by remember { mutableStateOf(true) }
    if (showContent) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .graphicsLayer {
                    val offsetToClose = size.height / DRAG_TO_CLOSE_DELIMITER
                    translationY = dragOffset
                    alpha =
                        ((offsetToClose - dragOffset * 0.4f) / offsetToClose).coerceAtLeast(0.5f)
                },
            contentAlignment = Alignment.Center,
        ) {
            val maxHeightPx = this.maxHeight.dpToPx()
            val offsetToClose = maxHeightPx / DRAG_TO_CLOSE_DELIMITER

            LaunchedEffect(Unit) {
                snapshotFlow { dragOffset }
                    .collect { onCloseDrag((offsetToClose - it) / offsetToClose) }
            }

            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .zoom(zoomable = zoomableState, onTap = { onClick() })
                    .draggable(
                        state = dragState,
                        enabled = enableDrag,
                        orientation = Orientation.Vertical,
                        onDragStopped = {
                            if (dragOffset > offsetToClose) {
                                showContent = false
                                onCloseRequest()
                            } else {
                                AnimationState(dragOffset).animateTo(
                                    targetValue = 0f,
                                    block = { dragOffset = value },
                                )
                            }
                        },
                    ),
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
}
