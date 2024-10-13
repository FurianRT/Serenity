package com.furianrt.mediaselector.internal.ui.selector

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.mediaselector.R
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.mediaselector.internal.ui.selector.composables.DragHandle
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.uikit.components.ConfirmationDialog
import com.furianrt.uikit.extensions.clickableNoRipple
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
internal fun MediaSelectorBottomSheetInternal(
    state: BottomSheetScaffoldState,
    onMediaSelected: (result: MediaResult) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
    content: @Composable (PaddingValues) -> Unit,
) {
    val viewModel = hiltViewModel<MediaSelectorViewModel>()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val storagePermissionsState = rememberMultiplePermissionsState(
        permissions = PermissionsUtils.getMediaPermissionList(),
        onPermissionsResult = { viewModel.onEvent(MediaSelectorEvent.OnMediaPermissionsSelected) },
    )

    var showConfirmDialog by remember { mutableStateOf(false) }
    var skipConfirmation by remember { mutableStateOf(false) }

    val listState = rememberLazyGridState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is MediaSelectorEffect.CloseScreen -> {
                        skipConfirmation = false
                        showConfirmDialog = false
                        state.bottomSheetState.hide()
                        listState.scrollToItem(0)
                    }

                    is MediaSelectorEffect.RequestMediaPermissions -> {
                        storagePermissionsState.launchMultiplePermissionRequest()
                    }

                    is MediaSelectorEffect.OpenMediaViewer -> openMediaViewer(
                        MediaViewerRoute(
                            mediaId = effect.mediaId,
                            dialogId = effect.dialogId,
                            requestId = effect.requestId,
                        ),
                    )

                    is MediaSelectorEffect.SendMediaResult -> {
                        onMediaSelected(effect.result)
                    }
                }
            }
    }

    LaunchedEffect(state.bottomSheetState.isVisible) {
        val isBottomSheetVisible = state.bottomSheetState.isVisible
        val selectedCount = (uiState as? MediaSelectorUiState.Success)?.selectedCount ?: 0
        when {
            !isBottomSheetVisible && selectedCount > 0 && !skipConfirmation -> {
                state.bottomSheetState.expand()
                showConfirmDialog = true
            }

            !isBottomSheetVisible -> {
                viewModel.onEvent(MediaSelectorEvent.OnCloseScreenRequest)
            }
        }
    }

    val sheetSwipeEnabled by remember(uiState) {
        derivedStateOf {
            uiState !is MediaSelectorUiState.Success ||
                    (listState.firstVisibleItemIndex == 0 &&
                            listState.firstVisibleItemScrollOffset == 0) ||
                    !listState.isScrollInProgress
        }
    }

    val hazeState = remember { HazeState() }
    val backgroundModifier = Modifier
        .background(MaterialTheme.colorScheme.surface)
        .background(MaterialTheme.colorScheme.tertiary)

    BottomSheetScaffold(
        modifier = modifier.haze(state = hazeState),
        scaffoldState = state,
        sheetContainerColor = Color.Transparent,
        containerColor = Color.Transparent,
        sheetSwipeEnabled = sheetSwipeEnabled,
        sheetShadowElevation = 0.dp,
        sheetDragHandle = {},
        snackbarHost = {},
        content = { paddingValues ->
            Box {
                content(paddingValues)
                AnimatedVisibility(
                    visible = state.bottomSheetState.isVisible ||
                            state.bottomSheetState.targetValue == SheetValue.Expanded,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f))
                            .clickableNoRipple {
                                viewModel.onEvent(MediaSelectorEvent.OnCloseScreenRequest)
                            },
                    )
                }
            }
        },
        sheetContent = {
            DragHandle(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .then(backgroundModifier),
            )
            when (uiState) {
                is MediaSelectorUiState.Loading -> LoadingContent(
                    modifier = backgroundModifier,
                )

                is MediaSelectorUiState.Empty -> EmptyContent(
                    modifier = backgroundModifier,
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                )

                is MediaSelectorUiState.Success -> SuccessContent(
                    modifier = backgroundModifier.padding(bottom = bottomPadding),
                    uiState = uiState,
                    listState = listState,
                    onEvent = viewModel::onEvent,
                )
            }
        },
    )

    if (showConfirmDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.media_selector_discard_title),
            hint = stringResource(R.string.media_selector_discard_hint),
            cancelText = stringResource(com.furianrt.uikit.R.string.action_cancel),
            confirmText = stringResource(com.furianrt.uikit.R.string.action_discard),
            hazeState = hazeState,
            onDismissRequest = { showConfirmDialog = false },
            onConfirmClick = { viewModel.onEvent(MediaSelectorEvent.OnCloseScreenRequest) },
        )
    }

    BackHandler(
        enabled = state.bottomSheetState.isVisible,
        onBack = {
            if (uiState is MediaSelectorUiState.Success && uiState.selectedCount > 0) {
                showConfirmDialog = true
            } else {
                viewModel.onEvent(MediaSelectorEvent.OnCloseScreenRequest)
            }
        },
    )
}
