package com.furianrt.mediasorting.internal.ui

import android.net.Uri
import android.view.animation.OvershootInterpolator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.mediaselector.api.MediaSelectorBottomSheet
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.mediasorting.internal.ui.composables.AddMediaButton
import com.furianrt.mediasorting.internal.ui.composables.ConfirmCloseDialog
import com.furianrt.mediasorting.internal.ui.composables.DragAndDropHint
import com.furianrt.mediasorting.internal.ui.composables.ImageItem
import com.furianrt.mediasorting.internal.ui.composables.Toolbar
import com.furianrt.mediasorting.internal.ui.composables.VideoItem
import com.furianrt.mediasorting.internal.ui.entities.MediaItem
import com.furianrt.permissions.extensions.openAppSettingsScreen
import com.furianrt.permissions.ui.MediaPermissionDialog
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.MovableToolbarState
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.PreviewWithBackground
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.flow.collectLatest
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState
import java.time.ZonedDateTime

private const val HINT_ITEM_ID = "hint"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
internal fun MediaSortingScreen(
    onCloseRequest: () -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    openMediaViewScreen: (
        noteId: String,
        mediaId: String,
        mediaBlockId: String,
        identifier: DialogIdentifier,
    ) -> Unit,
) {
    val viewModel = hiltViewModel<MediaSortingViewModel>()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    val hazeState = remember { HazeState() }

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)
    val openMediaViewScreenState by rememberUpdatedState(openMediaViewScreen)
    val openMediaViewerState by rememberUpdatedState(openMediaViewer)
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    )

    val storagePermissionsState = rememberMultiplePermissionsState(
        permissions = PermissionsUtils.getMediaPermissionList(),
        onPermissionsResult = { viewModel.onEvent(MediaSortingEvent.OnMediaPermissionsSelected) },
    )

    var showMediaPermissionDialog by remember { mutableStateOf(false) }
    var showConfirmCloseDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is MediaSortingEffect.CloseScreen -> onCloseRequestState()
                    is MediaSortingEffect.RequestStoragePermissions -> {
                        storagePermissionsState.launchMultiplePermissionRequest()
                    }

                    is MediaSortingEffect.OpenMediaSelector -> {
                        bottomSheetScaffoldState.bottomSheetState.expand()
                    }

                    is MediaSortingEffect.ShowPermissionsDeniedDialog -> {
                        showMediaPermissionDialog = true
                    }

                    is MediaSortingEffect.ShowConfirmCloseDialog -> {
                        showConfirmCloseDialog = true
                    }

                    is MediaSortingEffect.OpenMediaViewScreen -> openMediaViewScreenState(
                        effect.noteId,
                        effect.mediaId,
                        effect.mediaBlockId,
                        effect.identifier,
                    )

                    is MediaSortingEffect.OpenMediaViewer -> openMediaViewerState(effect.route)
                }
            }
    }

    MediaSelectorBottomSheet(
        modifier = Modifier
            .fillMaxSize()
            .hazeSource(hazeState),
        state = bottomSheetScaffoldState,
        openMediaViewer = { viewModel.onEvent(MediaSortingEvent.OnOpenMediaViewerRequest(it)) },
        onMediaSelected = { viewModel.onEvent(MediaSortingEvent.OnMediaSelected(it)) },
    ) {
        Content(
            uiState = uiState,
            onEvent = viewModel::onEvent,
        )
        DimSurfaceOverlay(
            visible = bottomSheetScaffoldState.bottomSheetState.isVisible ||
                    bottomSheetScaffoldState.bottomSheetState.targetValue == SheetValue.Expanded,
        )
    }

    BackHandler(
        enabled = uiState.hasContentChanged,
        onBack = { viewModel.onEvent(MediaSortingEvent.OnButtonBackClick) },
    )

    if (showMediaPermissionDialog) {
        MediaPermissionDialog(
            hazeState = hazeState,
            onSettingsClick = context::openAppSettingsScreen,
            onDismissRequest = { showMediaPermissionDialog = false },
        )
    }

    if (showConfirmCloseDialog) {
        ConfirmCloseDialog(
            hazeState = hazeState,
            onSaveClick = { viewModel.onEvent(MediaSortingEvent.OnButtonDoneClick) },
            onDiscardClick = { viewModel.onEvent(MediaSortingEvent.OnConfirmCloseClick) },
            onDismissRequest = { showConfirmCloseDialog = false },
        )
    }
}

@Composable
private fun Content(
    uiState: MediaSortingUiState,
    onEvent: (event: MediaSortingEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyGridState()
    val toolbarState = remember { MovableToolbarState() }
    val bottomInsetPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val statusBarPv = WindowInsets.statusBars.asPaddingValues()
    val statusBarHeight = rememberSaveable { statusBarPv.calculateTopPadding().value }

    MovableToolbarScaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        state = toolbarState,
        listState = listState,
        enabled = false,
        toolbar = {
            Toolbar(
                modifier = Modifier.padding(top = statusBarHeight.dp),
                onBackClick = { onEvent(MediaSortingEvent.OnButtonBackClick) },
                onDoneClick = { onEvent(MediaSortingEvent.OnButtonDoneClick) },
            )
        }
    ) { topPadding ->
        ContentList(
            uiState = uiState,
            onEvent = onEvent,
            listState = listState,
            contentPadding = PaddingValues(
                top = topPadding + 8.dp,
                bottom = 80.dp + bottomInsetPadding,
                start = 8.dp,
                end = 4.dp,
            ),
        )
        AddMediaButton(
            modifier = Modifier
                .padding(end = 24.dp, bottom = 24.dp)
                .navigationBarsPadding()
                .align(Alignment.BottomEnd),
            onClick = { onEvent(MediaSortingEvent.OnAddMediaClick) },
        )
    }
}

@Composable
private fun ContentList(
    uiState: MediaSortingUiState,
    onEvent: (event: MediaSortingEvent) -> Unit,
    listState: LazyGridState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val reorderableLazyColumnState = rememberReorderableLazyGridState(
        lazyGridState = listState,
        scrollThresholdPadding = contentPadding,
        onMove = { from, to -> onEvent(MediaSortingEvent.OnMediaItemMoved(from, to)) },
    )
    val listSpanCount = 3
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        state = listState,
        columns = GridCells.Fixed(listSpanCount),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = contentPadding,
    ) {
        itemsIndexed(
            items = uiState.media,
            key = { _, item -> item.id },
            contentType = { _, item -> item::class },
        ) { _, item ->
            ReorderableItem(
                state = reorderableLazyColumnState,
                key = item.id,
            ) {
                var isDragging by remember { mutableStateOf(false) }

                val overshootInterpolator = remember { OvershootInterpolator(3.5f) }
                val scale by animateFloatAsState(
                    targetValue = if (isDragging) 1.1f else 1f,
                    animationSpec = if (isDragging) {
                        tween(
                            durationMillis = 250,
                            easing = { overshootInterpolator.getInterpolation(it) },
                        )
                    } else {
                        tween(durationMillis = 400)
                    },
                )
                val alpha by animateFloatAsState(targetValue = if (isDragging) 0.7f else 1f)

                val draggableModifier = Modifier.longPressDraggableHandle(
                    onDragStarted = {
                        isDragging = true
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onDragStopped = { isDragging = false }
                )
                val graphicsLayerModifier = Modifier.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                }
                when (item) {
                    is MediaItem.Image -> ImageItem(
                        modifier = Modifier
                            .then(graphicsLayerModifier)
                            .then(draggableModifier),
                        item = item,
                        onClick = { onEvent(MediaSortingEvent.OnMediaClick(it)) },
                        onDeleteClick = { onEvent(MediaSortingEvent.OnRemoveMediaClick(it)) },
                    )

                    is MediaItem.Video -> VideoItem(
                        modifier = Modifier
                            .then(graphicsLayerModifier)
                            .then(draggableModifier),
                        item = item,
                        onClick = { onEvent(MediaSortingEvent.OnMediaClick(it)) },
                        onDeleteClick = { onEvent(MediaSortingEvent.OnRemoveMediaClick(it)) },
                    )
                }
            }
        }

        item(
            key = HINT_ITEM_ID,
            span = { GridItemSpan(listSpanCount) },
        ) {
            DragAndDropHint()
        }
    }
}

@Composable
private fun DimSurfaceOverlay(
    visible: Boolean,
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
                .clickableNoRipple {},
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        Content(
            uiState = MediaSortingUiState(
                hasContentChanged = false,
                media = buildList {
                    repeat(7) { index ->
                        add(
                            MediaItem.Image(
                                id = index.toString(),
                                name = "",
                                uri = Uri.EMPTY,
                                ratio = 1f,
                                addedDate = ZonedDateTime.now(),
                            )
                        )
                    }
                    repeat(6) { index ->
                        add(
                            MediaItem.Video(
                                id = (7 + index).toString(),
                                name = "",
                                uri = Uri.EMPTY,
                                ratio = 1f,
                                duration = 1500,
                                addedDate = ZonedDateTime.now(),
                            )
                        )
                    }
                }
            ),
            onEvent = {},
        )
    }
}
