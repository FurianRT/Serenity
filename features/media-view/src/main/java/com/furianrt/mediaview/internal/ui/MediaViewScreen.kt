package com.furianrt.mediaview.internal.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.mediaview.R
import com.furianrt.uikit.components.ControlsAnimatedVisibility
import com.furianrt.mediaview.internal.ui.composables.MediaList
import com.furianrt.mediaview.internal.ui.composables.MediaPager
import com.furianrt.mediaview.internal.ui.composables.Toolbar
import com.furianrt.mediaview.internal.ui.entities.MediaItem
import com.furianrt.uikit.components.SnackBar
import com.furianrt.uikit.constants.SystemBarsConstants
import com.furianrt.uikit.extensions.findActivity
import com.furianrt.uikit.extensions.hideSystemUi
import com.furianrt.uikit.extensions.showSystemUi
import com.furianrt.uikit.theme.Colors
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.furianrt.uikit.R as uiR

@Composable
internal fun MediaViewScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel = hiltViewModel<MediaViewModel>()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val snackBarHostState = remember { SnackbarHostState() }
    val imageSavedMessage = stringResource(R.string.media_view_saved_to_gallery)
    val imageNotSavedMessage = stringResource(uiR.string.general_error)

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is MediaViewEffect.CloseScreen -> onCloseRequest()
                    is MediaViewEffect.ShowMediaSavedMessage -> snackBarHostState.showSnackbar(
                        message = imageSavedMessage,
                        duration = SnackbarDuration.Short,
                    )

                    is MediaViewEffect.ShowMediaSaveErrorMessage -> snackBarHostState.showSnackbar(
                        message = imageNotSavedMessage,
                        duration = SnackbarDuration.Short,
                    )
                }
            }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    SnackBar(
                        title = data.visuals.message,
                        icon = painterResource(R.drawable.ic_download),
                        color = Colors.DarkGray.copy(alpha = 0.9f),
                    )
                },
            )
        },
        content = { paddingValues ->
            SuccessContent(
                paddingValues = paddingValues,
                uiState = uiState,
                onEvent = viewModel::onEvent,
            )
        }
    )
}

@Composable
private fun SuccessContent(
    uiState: MediaViewUiState,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    onEvent: (event: MediaViewEvent) -> Unit = {},
) {
    val pagerState = rememberPagerState(
        initialPage = uiState.initialMediaIndex,
        pageCount = { uiState.media.count() },
    )
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showControls by rememberSaveable { mutableStateOf(true) }

    DisposableEffect(showControls) {
        val window = context.findActivity()?.window ?: return@DisposableEffect onDispose {}
        if (showControls) {
            window.showSystemUi()
        } else {
            window.hideSystemUi()
        }
        onDispose {
            window.showSystemUi()
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
        )
        ControlsAnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(paddingValues),
            visible = showControls,
            label = "ToolbarAnim",
        ) {
            Toolbar(
                modifier = Modifier.background(SystemBarsConstants.Color),
                totalImages = uiState.media.count(),
                currentImageIndex = pagerState.currentPage,
                onBackClick = { onEvent(MediaViewEvent.OnButtonBackClick) },
                onDeleteClick = {
                    onEvent(MediaViewEvent.OnButtonDeleteClick(pagerState.currentPage))
                },
                onSaveMediaClick = {
                    onEvent(MediaViewEvent.OnButtonSaveToGalleryClick(pagerState.currentPage))
                }
            )
        }
        ControlsAnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(paddingValues),
            visible = showControls,
            label = "MediaListAnim",
        ) {
            MediaList(
                modifier = Modifier.background(SystemBarsConstants.Color),
                media = uiState.media,
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
            uiState = MediaViewUiState(
                initialMediaIndex = 1,
                media = persistentListOf(
                    MediaItem.Image(
                        name = "1",
                        uri = Uri.EMPTY,
                        ratio = 0.5f,
                        addedTime = 0L,
                    ),
                    MediaItem.Image(
                        name = "2",
                        uri = Uri.EMPTY,
                        ratio = 0.5f,
                        addedTime = 0L,
                    ),
                    MediaItem.Image(
                        name = "3",
                        uri = Uri.EMPTY,
                        ratio = 1.4f,
                        addedTime = 0L,
                    ),
                ),
            ),
        )
    }
}
