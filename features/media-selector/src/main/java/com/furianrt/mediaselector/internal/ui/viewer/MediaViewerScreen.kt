package com.furianrt.mediaselector.internal.ui.viewer

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import com.furianrt.mediaselector.internal.ui.viewer.composables.MediaList
import com.furianrt.mediaselector.internal.ui.viewer.composables.MediaPager
import com.furianrt.mediaselector.internal.ui.viewer.composables.Toolbar
import com.furianrt.uikit.components.ControlsAnimatedVisibility
import com.furianrt.uikit.constants.SystemBarsConstants
import com.furianrt.uikit.extensions.hideSystemUi
import com.furianrt.uikit.extensions.showSystemUi
import com.furianrt.uikit.theme.LocalFont
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val UI_HIDE_DELAY = 3000L

@Composable
internal fun MediaViewerScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel = hiltViewModel<MediaViewerViewModel>()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val activity = LocalActivity.current

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)

    DisposableEffect(Unit) {
        val requestedOrientation = activity?.requestedOrientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_USER
        onDispose {
            requestedOrientation?.let { activity.requestedOrientation = it }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is MediaViewerEffect.CloseScreen -> onCloseRequestState()
                }
            }
    }

    SerenityTheme(
        isLightTheme = false,
        font = (uiState as? MediaViewerUiState.Success)?.font ?: LocalFont.current,
    ) {
        when (uiState) {
            is MediaViewerUiState.Success -> SuccessContent(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onCloseRequest = onCloseRequest,
            )

            is MediaViewerUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun SuccessContent(
    uiState: MediaViewerUiState.Success,
    modifier: Modifier = Modifier,
    onCloseRequest: () -> Unit = {},
    onEvent: (event: MediaViewerEvent) -> Unit = {},
) {
    val configuration = LocalConfiguration.current

    val pagerState = rememberPagerState(
        initialPage = uiState.initialMediaIndex,
        pageCount = { uiState.media.count() },
    )
    val scope = rememberCoroutineScope()
    val activity = LocalActivity.current
    var showControls by rememberSaveable { mutableStateOf(true) }
    val listState = rememberLazyListState()
    var isThumbDragging by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var controlsAlpha by remember { mutableFloatStateOf(1f) }
    var isVideoItem by remember { mutableStateOf(false) }

    DisposableEffect(showControls) {
        if (showControls) {
            activity?.window?.showSystemUi()
        } else {
            activity?.window?.hideSystemUi()
        }
        onDispose {
            activity?.window?.showSystemUi()
        }
    }

    LaunchedEffect(
        listState.isScrollInProgress,
        showControls,
        isThumbDragging,
        isPlaying,
    ) {
        snapshotFlow { pagerState.currentPage }
            .collectLatest { currentPage ->
                isVideoItem = uiState.media[currentPage] is MediaItem.Video
                val isScrollInProgress = listState.isScrollInProgress
                if (!isScrollInProgress && showControls && isVideoItem && !isThumbDragging && isPlaying) {
                    delay(UI_HIDE_DELAY)
                    showControls = false
                }
            }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        MediaPager(
            modifier = Modifier.fillMaxSize(),
            media = uiState.media,
            state = pagerState,
            showControls = showControls,
            onMediaItemClick = { showControls = !showControls },
            onThumbDragStart = { isThumbDragging = true },
            onThumbDragEnd = { isThumbDragging = false },
            onPause = { index ->
                if (index == pagerState.currentPage) {
                    isPlaying = false
                }
            },
            onResume = { index ->
                if (index == pagerState.currentPage) {
                    isPlaying = true
                }
            },
            onEnded = { index ->
                if (index == pagerState.currentPage) {
                    showControls = true
                    isPlaying = false
                }
            },
            onCloseDrag = { controlsAlpha = it },
            onCloseRequest = onCloseRequest,
        )
        ControlsAnimatedVisibility(
            modifier = Modifier
                .graphicsLayer { alpha = controlsAlpha }
                .align(Alignment.TopCenter),
            visible = showControls,
        ) {
            Toolbar(
                modifier = Modifier
                    .background(SystemBarsConstants.Color)
                    .statusBarsPadding(),
                totalImages = uiState.media.count(),
                currentImageIndex = pagerState.currentPage,
                selectionState = uiState.media[pagerState.currentPage].state,
                onBackClick = { onEvent(MediaViewerEvent.OnButtonBackClick) },
                onSelectClick = {
                    onEvent(
                        MediaViewerEvent.OnMediaSelectionToggle(
                            media = uiState.media[pagerState.currentPage],
                        ),
                    )
                },
            )
        }
        ControlsAnimatedVisibility(
            modifier = Modifier
                .graphicsLayer { alpha = controlsAlpha }
                .align(Alignment.BottomCenter),
            visible = showControls &&
                    (!isVideoItem ||
                            configuration.orientation == Configuration.ORIENTATION_PORTRAIT),
        ) {
            MediaList(
                modifier = Modifier
                    .background(SystemBarsConstants.Color)
                    .navigationBarsPadding(),
                media = uiState.media,
                state = listState,
                initialMediaIndex = uiState.initialMediaIndex,
                currentItem = pagerState.currentPage,
                onItemClick = { scope.launch { pagerState.scrollToPage(it) } },
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SerenityTheme {
        SuccessContent(
            uiState = MediaViewerUiState.Success(
                initialMediaIndex = 1,
                media = listOf(
                    MediaItem.Image(
                        id = 1L,
                        name = "1",
                        uri = Uri.EMPTY,
                        ratio = 1f,
                        state = SelectionState.Default,
                    ),
                    MediaItem.Image(
                        id = 2L,
                        name = "2",
                        uri = Uri.EMPTY,
                        ratio = 1f,
                        state = SelectionState.Selected(order = 1),
                    ),
                    MediaItem.Image(
                        id = 3L,
                        name = "3",
                        uri = Uri.EMPTY,
                        ratio = 1f,
                        state = SelectionState.Default,
                    ),
                ),
            ),
        )
    }
}