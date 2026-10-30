package com.furianrt.apptheme.internal.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.apptheme.internal.ui.composables.ThemeItem
import com.furianrt.apptheme.internal.ui.composables.Toolbar
import com.furianrt.mediaselector.api.MediaSelectorBottomSheet
import com.furianrt.mediaselector.api.MediaSelectorState
import com.furianrt.mediaselector.api.MediaSelectorState.*
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.mediaselector.api.rememberMediaSelectorState
import com.furianrt.permissions.extensions.openAppSettingsScreen
import com.furianrt.permissions.ui.MediaPermissionDialog
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.uikit.components.AppBackground
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.extensions.clickableNoRipple
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun AppThemeScreen(
    onCloseRequest: () -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
) {
    val viewModel: AppThemeViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current

    val hazeState = rememberHazeState()
    val mediaSelectorState = rememberMediaSelectorState()

    val storagePermissionsState = rememberMultiplePermissionsState(
        permissions = PermissionsUtils.getMediaPermissionList(),
        onPermissionsResult = { viewModel.onEvent(AppThemeEvent.OnMediaPermissionsSelected) },
    )

    var showMediaPermissionDialog by remember { mutableStateOf(false) }

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)
    val openMediaViewerState by rememberUpdatedState(openMediaViewer)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is AppThemeEffect.CloseScreen -> onCloseRequestState()
                    is AppThemeEffect.RequestStoragePermissions -> {
                        storagePermissionsState.launchMultiplePermissionRequest()
                    }

                    is AppThemeEffect.OpenMediaSelector -> mediaSelectorState.expand(
                        params = Params(
                            singleChoice = true,
                            allowVideo = false,
                            onMediaSelected = effect.onMediaSelected,
                        ),
                    )

                    is AppThemeEffect.ShowPermissionsDeniedDialog -> {
                        showMediaPermissionDialog = true
                    }

                    is AppThemeEffect.OpenMediaViewer -> openMediaViewerState(effect.route)
                }
            }
    }

    Content(
        modifier = Modifier.hazeSource(hazeState),
        state = uiState,
        mediaSelectorState = mediaSelectorState,
        onEvent = viewModel::onEvent,
    )

    if (showMediaPermissionDialog) {
        MediaPermissionDialog(
            hazeState = hazeState,
            onSettingsClick = context::openAppSettingsScreen,
            onDismissRequest = { showMediaPermissionDialog = false },
        )
    }
}

@Composable
private fun Content(
    state: AppThemeState,
    mediaSelectorState: MediaSelectorState,
    onEvent: (event: AppThemeEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyGridState()

    MediaSelectorBottomSheet(
        modifier = modifier.fillMaxSize(),
        state = mediaSelectorState,
        openMediaViewer = { onEvent(AppThemeEvent.OnOpenMediaViewerRequest(it)) },
    ) {
        MovableToolbarScaffold(
            modifier = Modifier.fillMaxSize(),
            listState = listState,
            blurAlpha = 0.5f,
            enabled = false,
            toolbar = {
                Toolbar(
                    modifier = Modifier.statusBarsPadding(),
                    onAddClick = { onEvent(AppThemeEvent.OnAddClick) },
                    onBackClick = { onEvent(AppThemeEvent.OnBackClick) },
                )
            }
        ) { topPadding ->
            Crossfade(
                targetState = state.theme,
            ) { targetState ->
                AppBackground(
                    theme = targetState,
                )
            }
            when (state.content) {
                is AppThemeState.Content.Loading -> LoadingContent()
                is AppThemeState.Content.Success -> SuccessContent(
                    state = state.content,
                    onEvent = onEvent,
                    topPadding = topPadding,
                    listState = listState,
                )
            }
        }
        DimSurfaceOverlay(
            visible = mediaSelectorState.isVisible,
            onClick = { scope.launch { mediaSelectorState.collapse() } },
        )
    }
}

@Composable
private fun DimSurfaceOverlay(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim)
                .clickableNoRipple(onClick = onClick),
        )
    }
}

@Composable
private fun SuccessContent(
    state: AppThemeState.Content.Success,
    onEvent: (event: AppThemeEvent) -> Unit,
    listState: LazyGridState,
    topPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val bottomInsetPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        state = listState,
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = topPadding + 16.dp,
            bottom = bottomInsetPadding + 16.dp,
        ),
    ) {
        itemsIndexed(
            items = state.themes,
            key = { _, item -> item.id },
        ) { _, item ->
            ThemeItem(
                theme = item,
                isSelected = item.id == state.selectedId,
                onClick = { onEvent(AppThemeEvent.OnThemeClick(it)) },
            )
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize())
}
