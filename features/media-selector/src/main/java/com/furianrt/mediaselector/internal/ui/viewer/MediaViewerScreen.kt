package com.furianrt.mediaselector.internal.ui.viewer

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.furianrt.uikit.extensions.findActivity
import com.furianrt.uikit.extensions.hideSystemUi
import com.furianrt.uikit.extensions.showSystemUi
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
internal fun MediaViewerScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel = hiltViewModel<MediaViewerViewModel>()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is MediaViewerEffect.CloseScreen -> onCloseRequest()
                }
            }
    }

    when (uiState) {
        is MediaViewerUiState.Success -> SuccessContent(
            uiState = uiState,
            onEvent = viewModel::onEvent,
        )

        is MediaViewerUiState.Loading -> Box(
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun SuccessContent(
    uiState: MediaViewerUiState.Success,
    modifier: Modifier = Modifier,
    onEvent: (event: MediaViewerEvent) -> Unit = {},
) {
    val pagerState = rememberPagerState(
        initialPage = uiState.initialMediaIndex,
        pageCount = { uiState.media.count() },
    )
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showControls by rememberSaveable { mutableStateOf(true) }

    /*DisposableEffect(showControls) {
        val window = context.findActivity()?.window ?: return@DisposableEffect onDispose {}
        if (showControls) {
            window.showSystemUi()
        } else {
            window.hideSystemUi()
        }
        onDispose {
            window.showSystemUi()
        }
    }*/

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
        )
        ControlsAnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopCenter),
            visible = showControls,
            label = "ToolbarAnim",
        ) {
            Toolbar(
                modifier = Modifier.background(SystemBarsConstants.Color),
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
                .align(Alignment.BottomCenter),
            visible = showControls,
            label = "MediaListAnim",
        ) {
            MediaList(
                modifier = Modifier.background(SystemBarsConstants.Color),
                media = uiState.media,
                initialMediaIndex = uiState.initialMediaIndex,
                currentItem = pagerState.currentPage,
                onItemClick = { scope.launch { pagerState.scrollToPage(it) } },
            )
        }
    }
}

@Preview(backgroundColor = 0xFF000000)
@Composable
private fun Preview() {
    SerenityTheme {
        SuccessContent(
            uiState = MediaViewerUiState.Success(
                initialMediaIndex = 1,
                media = persistentListOf(
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
                        state = SelectionState.Selected(1),
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