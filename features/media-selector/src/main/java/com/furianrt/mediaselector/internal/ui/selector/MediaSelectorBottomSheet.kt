package com.furianrt.mediaselector.internal.ui.selector

import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.core.orFalse
import com.furianrt.mediaselector.R
import com.furianrt.mediaselector.api.MediaSelectorState
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.mediaselector.internal.ui.entities.MediaAlbumItem
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnPartialAccessMessageClick
import com.furianrt.mediaselector.internal.ui.selector.composables.DragHandle
import com.furianrt.mediaselector.internal.ui.selector.composables.PermissionsMessage
import com.furianrt.permissions.extensions.openAppSettingsScreen
import com.furianrt.permissions.ui.CameraPermissionDialog
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.uikit.components.ConfirmationDialog
import com.furianrt.uikit.components.SkipFirstEffect
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.extensions.pxToDp
import com.furianrt.uikit.utils.isGestureNavigationEnabled
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import com.furianrt.uikit.R as uiR

private const val CONTENT_ANIM_DURATION = 300
private val PREDICTIVE_BACK_TRANSLATION = 100.dp

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class,
    ExperimentalFoundationApi::class,
)
@Composable
internal fun MediaSelectorBottomSheetInternal(
    state: MediaSelectorState,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(PaddingValues) -> Unit,
) {
    val viewModel = hiltViewModel<MediaSelectorViewModel>()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        lifecycle.addObserver(viewModel)
        onDispose {
            lifecycle.removeObserver(viewModel)
        }
    }

    val storagePermissionsState = rememberMultiplePermissionsState(
        permissions = PermissionsUtils.getMediaPermissionList(),
        onPermissionsResult = { viewModel.onEvent(MediaSelectorEvent.OnMediaPermissionsSelected) },
    )

    val cameraPermissionState = rememberPermissionState(
        permission = PermissionsUtils.getCameraPermission(),
        onPermissionResult = { viewModel.onEvent(MediaSelectorEvent.OnCameraPermissionSelected) },
    )

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { viewModel.onEvent(MediaSelectorEvent.OnTakePictureResult(it)) },
    )

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
        onResult = { viewModel.onEvent(MediaSelectorEvent.OnTakeVideoResult(it)) },
    )

    var albumsDialogState: List<MediaAlbumItem>? by remember { mutableStateOf(null) }
    var showCameraPermissionDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyGridState(
        cacheWindow = LazyLayoutCacheWindow(ahead = 200.dp, behind = 150.dp),
    )
    val openMediaViewerState by rememberUpdatedState(openMediaViewer)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is MediaSelectorEffect.CloseScreen -> {
                        state.blockHiddenState(false)
                        state.bottomSheetState.hide()
                        listState.scrollToItem(0)
                    }

                    is MediaSelectorEffect.RequestMediaPermissions -> {
                        storagePermissionsState.launchMultiplePermissionRequest()
                    }

                    is MediaSelectorEffect.OpenMediaViewer -> openMediaViewerState(
                        MediaViewerRoute(
                            mediaId = effect.mediaId,
                            albumId = effect.albumId,
                            dialogId = effect.dialogId,
                            requestId = effect.requestId,
                            singleChoice = effect.singleChoice,
                            allowVideo = effect.allowVideo,
                        ),
                    )

                    is MediaSelectorEffect.ShowAlbumsList -> {
                        albumsDialogState = effect.albums.takeIf { it.isNotEmpty() }
                    }

                    is MediaSelectorEffect.HideAlbumsList -> albumsDialogState = null
                    is MediaSelectorEffect.TakePicture -> scope.launch {
                        try {
                            photoLauncher.launch(effect.uri)
                        } catch (e: ActivityNotFoundException) {
                            viewModel.onEvent(MediaSelectorEvent.OnCameraNotFoundError(e))
                        }
                    }

                    is MediaSelectorEffect.TakeVideo -> scope.launch {
                        try {
                            videoLauncher.launch(effect.uri)
                        } catch (e: ActivityNotFoundException) {
                            viewModel.onEvent(MediaSelectorEvent.OnCameraNotFoundError(e))
                        }
                    }

                    is MediaSelectorEffect.RequestCameraPermission -> {
                        cameraPermissionState.launchPermissionRequest()
                    }

                    is MediaSelectorEffect.ShowCameraPermissionsDeniedDialog -> {
                        showCameraPermissionDialog = true
                    }

                    is MediaSelectorEffect.ShowMessage -> {
                        Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
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

    LaunchedEffect(state.params, state.isVisible) {
        if (state.isVisible) {
            viewModel.onEvent(MediaSelectorEvent.OnExpanded(state.params))
        } else {
            bottomSheetTranslationY = 0.dp
        }
    }

    LaunchedEffect(state.bottomSheetState.isVisible) {
        if (!state.bottomSheetState.isVisible && !state.isHiddenStateBlocked.value) {
            listState.scrollToItem(0)
        }
    }

    LaunchedEffect(uiState.hasSelectedItems) {
        state.blockHiddenState(uiState.hasSelectedItems)
    }

    val hazeState = rememberHazeState()

    val sheetPeekHeight by remember {
        derivedStateOf { listState.layoutInfo.viewportSize.height * 0.75f }
    }

    val statusBarPv = WindowInsets.statusBars.asPaddingValues()
    val statusBarHeight = rememberSaveable { statusBarPv.calculateTopPadding().value }
    var sheetSwipeEnabled by remember { mutableStateOf(true) }

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = state.scaffoldState,
        sheetContainerColor = Color.Transparent,
        containerColor = Color.Transparent,
        sheetShadowElevation = 0.dp,
        sheetShape = RectangleShape,
        sheetPeekHeight = sheetPeekHeight.pxToDp(),
        sheetSwipeEnabled = sheetSwipeEnabled,
        sheetDragHandle = null,
        snackbarHost = {},
        content = { paddingValues ->
            Box {
                content(paddingValues)
            }
        },
        sheetContent = {
            Spacer(Modifier.height(statusBarHeight.dp))
            SheetContent(
                modifier = Modifier
                    .graphicsLayer { translationY = bottomSheetTranslationY.toPx() }
                    .hazeSource(hazeState),
                uiState = uiState,
                onEvent = viewModel::onEvent,
                listState = listState,
                albumsDialogState = albumsDialogState,
                sheetState = state.bottomSheetState,
                onDragStart = { sheetSwipeEnabled = false },
                onDragEnd = { sheetSwipeEnabled = true }
            )
        },
    )

    if (state.isHiddenStateBlocked.value) {
        ConfirmationDialog(
            title = stringResource(R.string.media_selector_discard_title),
            hint = stringResource(R.string.media_selector_discard_hint),
            confirmText = stringResource(uiR.string.action_discard),
            hazeState = hazeState,
            onDismissRequest = { state.isHiddenStateBlocked.value = false },
            onConfirmClick = { viewModel.onEvent(MediaSelectorEvent.OnCloseScreenRequest) },
        )
    }

    if (showCameraPermissionDialog) {
        CameraPermissionDialog(
            hazeState = hazeState,
            onDismissRequest = { showCameraPermissionDialog = false },
            onSettingsClick = context::openAppSettingsScreen,
        )
    }

    PredictiveBackHandler(
        enabled = state.bottomSheetState.isVisible && isGestureNavigationEnabled(),
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
        enabled = state.bottomSheetState.isVisible && !isGestureNavigationEnabled(),
        onBack = { viewModel.onEvent(MediaSelectorEvent.OnCloseScreenRequest) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SheetContent(
    uiState: MediaSelectorUiState,
    onEvent: (event: MediaSelectorEvent) -> Unit,
    listState: LazyGridState,
    albumsDialogState: List<MediaAlbumItem>?,
    sheetState: SheetState,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val showPartialAccessMessage = (uiState as? MediaSelectorUiState.Success)
        ?.showPartialAccessMessage.orFalse()
    val shadowColor = MaterialTheme.colorScheme.surfaceDim
    SkipFirstEffect(uiState.selectedAlbum?.id ?: MediaAlbumItem.ALL_MEDIA_ALBUM_ID) {
        listState.scrollToItem(0)
    }
    Surface(
        modifier = modifier,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        if (listState.canScrollBackward) {
                            drawBottomShadow(shadowColor)
                        }
                    },
            ) {
                DragHandle()
                if (showPartialAccessMessage) {
                    PermissionsMessage(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onEvent(OnPartialAccessMessageClick) },
                    )
                }
            }
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    fadeIn(tween(CONTENT_ANIM_DURATION))
                        .togetherWith(fadeOut(tween(CONTENT_ANIM_DURATION)))
                },
                contentKey = { it::class.simpleName },
            ) { targetState ->
                when (targetState) {
                    is MediaSelectorUiState.Loading -> LoadingContent()
                    is MediaSelectorUiState.Success -> SuccessContent(
                        uiState = targetState,
                        onEvent = onEvent,
                        listState = listState,
                        albumsDialogState = albumsDialogState,
                        sheetState = sheetState,
                        onBarDragStart = onDragStart,
                        onBarDragEnd = onDragEnd,
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = albumsDialogState != null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim),
            )
        }
    }
}
