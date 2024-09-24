package com.furianrt.mediaview.internal.ui

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import com.furianrt.mediaview.internal.ui.composables.MediaList
import com.furianrt.mediaview.internal.ui.composables.MediaPager
import com.furianrt.mediaview.internal.ui.composables.Toolbar
import com.furianrt.mediaview.internal.ui.entities.MediaItem
import com.furianrt.uikit.theme.SerenityTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
internal fun MediaViewScreenInternal(
    navHostController: NavHostController,
) {
    val viewModel = hiltViewModel<MediaViewModel>()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is MediaViewEffect.CloseScreen -> navHostController.popBackStack()
                }
            }
    }

    Surface(color = Color.Black) {
        when (uiState) {
            is MediaViewUiState.Success -> if (uiState.media.isEmpty()) {
                navHostController.popBackStack()
            } else {
                SuccessContent(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                )
            }

            is MediaViewUiState.Loading -> LoadingContent()
        }
    }
}

@Composable
private fun SuccessContent(
    uiState: MediaViewUiState.Success,
    onEvent: (event: MediaViewEvent) -> Unit = {},
) {
    val pagerState = rememberPagerState(
        initialPage = uiState.initialMediaIndex,
        pageCount = { uiState.media.count() },
    )
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val hazeState = remember { HazeState() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .haze(hazeState),
        contentAlignment = Alignment.Center,
    ) {
        MediaPager(
            modifier = Modifier.fillMaxSize(),
            media = uiState.media,
            state = pagerState,
        )
        Toolbar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.statusBars),
            totalImages = uiState.media.count(),
            currentImageIndex = pagerState.currentPage + 1,
            dropDownHazeState = hazeState,
            onBackClick = { onEvent(MediaViewEvent.OnButtonBackClick) },
        )
        MediaList(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars),
            media = uiState.media,
            currentItem = pagerState.currentPage,
            state = listState,
            onItemClick = { scope.launch { pagerState.scrollToPage(it) } },
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize())
}

@Preview(backgroundColor = 0xFF000000)
@Composable
private fun Preview() {
    SerenityTheme {
        SuccessContent(
            uiState = MediaViewUiState.Success(
                initialMediaIndex = 1,
                media = persistentListOf(
                    MediaItem.Image(
                        name = "1",
                        uri = Uri.EMPTY,
                        ratio = 0.5f,
                    ),
                    MediaItem.Video(
                        name = "1",
                        uri = Uri.EMPTY,
                        ratio = 1f,
                        duration = 2000,
                    ),
                    MediaItem.Image(
                        name = "1",
                        uri = Uri.EMPTY,
                        ratio = 1.4f,
                    ),
                ),
            ),
        )
    }
}
