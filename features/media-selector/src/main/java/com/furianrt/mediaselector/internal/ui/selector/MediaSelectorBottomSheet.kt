package com.furianrt.mediaselector.internal.ui.selector

import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.mediaselector.R
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.mediaselector.internal.ui.selector.composables.DragHandle
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.uikit.components.ConfirmationDialog
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.utils.isGestureNavigationEnabled
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import com.furianrt.uikit.R as uiR

private const val CONTENT_ANIM_DURATION = 300
private val PREDICTIVE_BACK_TRANSLATION = 100.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
internal fun MediaSelectorBottomSheetInternal(
    state: BottomSheetScaffoldState,
    onMediaSelected: (result: MediaResult) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(PaddingValues) -> Unit,
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
    val onMediaSelectedState by rememberUpdatedState(onMediaSelected)
    val openMediaViewerState by rememberUpdatedState(openMediaViewer)

    LaunchedEffect(Unit) {
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

                    is MediaSelectorEffect.OpenMediaViewer -> openMediaViewerState(
                        MediaViewerRoute(
                            mediaId = effect.mediaId,
                            dialogId = effect.dialogId,
                            requestId = effect.requestId,
                        ),
                    )

                    is MediaSelectorEffect.SendMediaResult -> {
                        onMediaSelectedState(effect.result)
                    }
                }
            }
    }

    LifecycleStartEffect(Unit) {
        viewModel.onEvent(MediaSelectorEvent.OnScreenResumed)
        onStopOrDispose {}
    }

    val translationYAnim = remember { Animatable(0f) }
    var bottomSheetTranslationY by remember(translationYAnim) {
        mutableStateOf(translationYAnim.value.dp)
    }

    val isBottomSheetVisible = state.bottomSheetState.isVisible
    LaunchedEffect(isBottomSheetVisible) {
        val selectedCount = (uiState as? MediaSelectorUiState.Success)?.selectedCount ?: 0
        if (!isBottomSheetVisible) {
            bottomSheetTranslationY = 0.dp
        }
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

    LaunchedEffect(state.bottomSheetState.targetValue) {
        if (state.bottomSheetState.targetValue == SheetValue.Expanded) {
            viewModel.onEvent(MediaSelectorEvent.OnExpanded)
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

    val statusBarPv = WindowInsets.statusBars.asPaddingValues()
    val statusBarHeight = rememberSaveable { statusBarPv.calculateTopPadding().value }
    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = state,
        sheetContainerColor = Color.Transparent,
        containerColor = Color.Transparent,
        sheetSwipeEnabled = sheetSwipeEnabled,
        sheetShadowElevation = 0.dp,
        sheetShape = RectangleShape,
        sheetDragHandle = null,
        snackbarHost = {},
        content = { paddingValues ->
            Box {
                content(paddingValues)
            }
        },
        sheetContent = {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ToolbarConstants.toolbarHeight + statusBarHeight.dp)
                    .clickableNoRipple { scope.launch { state.bottomSheetState.hide() } },
            )
            SheetContent(
                modifier = Modifier
                    .graphicsLayer { translationY = bottomSheetTranslationY.toPx() }
                    .hazeSource(hazeState),
                uiState = uiState,
                onEvent = viewModel::onEvent,
                listState = listState,
            )
        },
    )

    if (showConfirmDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.media_selector_discard_title),
            hint = stringResource(R.string.media_selector_discard_hint),
            confirmText = stringResource(uiR.string.action_discard),
            hazeState = hazeState,
            onDismissRequest = { showConfirmDialog = false },
            onConfirmClick = { viewModel.onEvent(MediaSelectorEvent.OnCloseScreenRequest) },
        )
    }

    PredictiveBackHandler(
        enabled = isBottomSheetVisible && isGestureNavigationEnabled(),
        onBack = { progress ->
            try {
                progress.collect { event ->
                    bottomSheetTranslationY = PREDICTIVE_BACK_TRANSLATION * event.progress
                }
                viewModel.onEvent(MediaSelectorEvent.OnCloseScreenRequest)
            } catch (_: CancellationException) {
                translationYAnim.animateTo(0f)
            }
        },
    )

    BackHandler(
        enabled = isBottomSheetVisible && !isGestureNavigationEnabled(),
        onBack = {
            if (uiState is MediaSelectorUiState.Success && uiState.selectedCount > 0) {
                showConfirmDialog = true
            } else {
                viewModel.onEvent(MediaSelectorEvent.OnCloseScreenRequest)
            }
        },
    )
}

@Composable
private fun SheetContent(
    uiState: MediaSelectorUiState,
    onEvent: (event: MediaSelectorEvent) -> Unit,
    listState: LazyGridState,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column {
            DragHandle()
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    fadeIn(tween(CONTENT_ANIM_DURATION))
                        .togetherWith(fadeOut(tween(CONTENT_ANIM_DURATION)))
                },
                contentKey = { it::class.simpleName },
                label = "StateAnim",
            ) { targetState ->
                when (targetState) {
                    is MediaSelectorUiState.Loading -> LoadingContent()

                    is MediaSelectorUiState.Empty -> EmptyContent(
                        uiState = targetState,
                        onEvent = onEvent,
                    )

                    is MediaSelectorUiState.Success -> SuccessContent(
                        uiState = targetState,
                        listState = listState,
                        onEvent = onEvent,
                    )
                }
            }
        }
    }
}
