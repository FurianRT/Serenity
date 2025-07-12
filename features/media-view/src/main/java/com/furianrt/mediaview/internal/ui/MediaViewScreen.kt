package com.furianrt.mediaview.internal.ui

import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.mediaview.R
import com.furianrt.mediaview.internal.ui.composables.MediaList
import com.furianrt.mediaview.internal.ui.composables.MediaPager
import com.furianrt.mediaview.internal.ui.composables.Toolbar
import com.furianrt.mediaview.internal.ui.entities.MediaItem
import com.furianrt.uikit.components.ControlsAnimatedVisibility
import com.furianrt.uikit.components.SnackBar
import com.furianrt.uikit.constants.SystemBarsConstants
import com.furianrt.uikit.extensions.hideSystemUi
import com.furianrt.uikit.extensions.showSystemUi
import com.furianrt.uikit.theme.Colors
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.IntentCreator
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.furianrt.uikit.R as uiR

private const val UI_HIDE_DELAY = 3000L

@Composable
internal fun MediaViewScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel = hiltViewModel<MediaViewModel>()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    val activity = LocalActivity.current

    val snackBarHostState = remember { SnackbarHostState() }
    val imageSavedMessage = stringResource(R.string.media_view_saved_to_gallery)
    val imageNotSavedMessage = stringResource(uiR.string.general_error)

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
                    is MediaViewEffect.CloseScreen -> onCloseRequestState()
                    is MediaViewEffect.ShowMediaSavedMessage -> {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = imageSavedMessage,
                            duration = SnackbarDuration.Short,
                        )
                    }

                    is MediaViewEffect.ShowMediaSaveErrorMessage -> {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = imageNotSavedMessage,
                            duration = SnackbarDuration.Short,
                        )
                    }

                    is MediaViewEffect.ShowSyncProgressMessage -> {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = effect.message,
                            duration = SnackbarDuration.Short,
                        )
                    }

                    is MediaViewEffect.ShareMedia -> IntentCreator.mediaShareIntent(
                        uri = effect.media.uri,
                        mediaType = when (effect.media) {
                            is MediaItem.Image -> IntentCreator.MediaType.IMAGE
                            is MediaItem.Video -> IntentCreator.MediaType.VIDEO
                        },
                    ).onSuccess { intent ->
                        context.startActivity(intent)
                    }.onFailure { error ->
                        error.printStackTrace()
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = context.getString(uiR.string.general_error),
                            duration = SnackbarDuration.Short,
                        )
                    }
                }
            }
    }

    SuccessContent(
        snackBarHostState = snackBarHostState,
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onCloseRequest = onCloseRequest,
    )
}

@Composable
private fun SuccessContent(
    uiState: MediaViewUiState,
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState,
    onEvent: (event: MediaViewEvent) -> Unit = {},
    onCloseRequest: () -> Unit = {},
) {
    val pagerState = rememberPagerState(
        initialPage = uiState.initialMediaIndex,
        pageCount = { uiState.media.count() },
    )
    val scope = rememberCoroutineScope()
    val hazeState = remember { HazeState() }
    val activity = LocalActivity.current
    var showControls by rememberSaveable { mutableStateOf(true) }
    val hapticFeedback = LocalHapticFeedback.current
    val listState = rememberLazyListState()
    var isThumbDragging by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var controlsAlpha by remember { mutableFloatStateOf(1f) }

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
        pagerState.currentPage,
        isThumbDragging,
        isPlaying,
    ) {
        val isVideoItem = uiState.media[pagerState.currentPage] is MediaItem.Video
        val isScrollInProgress = listState.isScrollInProgress
        if (!isScrollInProgress && showControls && isVideoItem && !isThumbDragging && isPlaying) {
            delay(UI_HIDE_DELAY)
            showControls = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .hazeSource(hazeState),
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
                hazeState = hazeState,
                onBackClick = { onEvent(MediaViewEvent.OnButtonBackClick) },
                onDeleteClick = {
                    onEvent(MediaViewEvent.OnButtonDeleteClick(pagerState.currentPage))
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                },
                onSaveMediaClick = {
                    onEvent(MediaViewEvent.OnButtonSaveToGalleryClick(pagerState.currentPage))
                },
                onShareClick = {
                    onEvent(MediaViewEvent.OnButtonShareClick(pagerState.currentPage))
                },
            )
        }
        ControlsAnimatedVisibility(
            modifier = Modifier
                .graphicsLayer { alpha = controlsAlpha }
                .align(Alignment.BottomCenter),
            visible = showControls,
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
        SnackbarHost(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            hostState = snackBarHostState,
            snackbar = { data ->
                SnackBar(
                    title = data.visuals.message,
                    icon = painterResource(R.drawable.ic_download),
                    color = Colors.Common.DarkGray.copy(alpha = 0.9f),
                )
            },
        )
    }
}

@Preview(backgroundColor = 0xFF000000)
@Composable
private fun Preview() {
    SerenityTheme {
        SuccessContent(
            uiState = MediaViewUiState(
                initialMediaIndex = 1,
                media = persistentListOf(
                    MediaItem.Image(id = "1", name = "1", uri = Uri.EMPTY, ratio = 0.5f),
                    MediaItem.Image(id = "2", name = "2", uri = Uri.EMPTY, ratio = 0.5f),
                    MediaItem.Image(id = "3", name = "3", uri = Uri.EMPTY, ratio = 1.4f),
                ),
            ),
            snackBarHostState = SnackbarHostState(),
        )
    }
}
