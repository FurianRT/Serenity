package com.furianrt.mediaselector.internal.ui

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import com.furianrt.core.buildImmutableList
import com.furianrt.mediaselector.R
import com.furianrt.mediaselector.internal.ui.MediaSelectorEvent.OnPartialAccessMessageClick
import com.furianrt.mediaselector.internal.ui.composables.ImageItem
import com.furianrt.mediaselector.internal.ui.composables.PermissionsMessage
import com.furianrt.mediaselector.internal.ui.composables.VideoItem
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
internal fun MediaSelectorBottomSheetInternal(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val viewModel: MediaSelectorViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val storagePermissionsState = rememberMultiplePermissionsState(
        permissions = uiState.mediaPermissionsList,
        onPermissionsResult = {
            viewModel.onEvent(MediaSelectorEvent.OnMediaPermissionsSelected)
        },
    )

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is MediaSelectorEffect.CloseScreen -> navHostController.popBackStack()
                    is MediaSelectorEffect.RequestMediaPermissions -> {
                        storagePermissionsState.launchMultiplePermissionRequest()
                    }
                }
            }
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    )
    val scope = rememberCoroutineScope()
    LaunchedEffect(true) {
        scope.launch { scaffoldState.bottomSheetState.expand() }
    }

    var closeDialog by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(scaffoldState.bottomSheetState.isVisible) {
        if (!scaffoldState.bottomSheetState.isVisible && !closeDialog) {
            closeDialog = true
            return@LaunchedEffect
        }
        if (!scaffoldState.bottomSheetState.isVisible) {
            navHostController.popBackStack()
        }
    }

    val sheetSwipeEnabled by remember(uiState) {
        derivedStateOf {
            uiState !is MediaSelectorUiState.Success ||
                    uiState.listState.firstVisibleItemIndex == 0 ||
                    !uiState.listState.isScrollInProgress
        }
    }

    val backgroundModifier = Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = ToolbarConstants.toolbarHeight / 2),
    ) {
        BottomSheetScaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = scaffoldState,
            sheetContainerColor = MaterialTheme.colorScheme.surface,
            containerColor = Color.Transparent,
            sheetSwipeEnabled = sheetSwipeEnabled,
            sheetDragHandle = { DragHandle(modifier = backgroundModifier) },
            snackbarHost = {},
            content = {},
            sheetContent = {
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
                        modifier = backgroundModifier,
                        uiState = uiState,
                        onEvent = viewModel::onEvent,
                    )
                }
            },
        )
    }

    BackHandler(
        enabled = scaffoldState.bottomSheetState.isVisible,
        onBack = { scope.launch { scaffoldState.bottomSheetState.hide() } },
    )
}

@Composable
private fun SuccessContent(
    uiState: MediaSelectorUiState.Success,
    onEvent: (event: MediaSelectorEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listSpanCount = 3
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        state = uiState.listState,
        columns = GridCells.Fixed(listSpanCount),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(start = 4.dp, end = 4.dp, bottom = 8.dp),
    ) {
        if (uiState.showPartialAccessMessage) {
            item(
                span = { GridItemSpan(listSpanCount) },
                content = { PermissionsMessage(onClick = { onEvent(OnPartialAccessMessageClick) }) }
            )
        }

        items(
            count = uiState.items.count(),
            key = { uiState.items[it].id },
            contentType = { uiState.items[it].javaClass.name },
        ) { index ->
            when (val item = uiState.items[index]) {
                is MediaItem.Image -> ImageItem(
                    modifier = Modifier.clip(
                        RoundedCornerShape(
                            topStart = if (index == 0) 8.dp else 0.dp,
                            topEnd = if (index == listSpanCount - 1) 8.dp else 0.dp,
                        )
                    ),
                    item = item,
                    onSelectClick = { onEvent(MediaSelectorEvent.OnSelectItemClick(it)) },
                )

                is MediaItem.Video -> VideoItem(
                    modifier = Modifier.clip(
                        RoundedCornerShape(
                            topStart = if (index == 0) 8.dp else 0.dp,
                            topEnd = if (index == listSpanCount - 1) 8.dp else 0.dp,
                        )
                    ),
                    item = item,
                    onSelectClick = { onEvent(MediaSelectorEvent.OnSelectItemClick(it)) },
                )
            }
        }
    }
}

@Composable
private fun DragHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp, 4.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                ),
        )
    }
}

// TODO Сделать индикатор
@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize())
}

// TODO Сделать пустое состояние покрасивше
@Composable
private fun EmptyContent(
    uiState: MediaSelectorUiState.Empty,
    onEvent: (event: MediaSelectorEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (uiState.showPartialAccessMessage) {
            PermissionsMessage(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = { onEvent(OnPartialAccessMessageClick) },
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.media_selector_empty_list_title),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@PreviewWithBackground
@Composable
private fun SuccessContentPreview() {
    SerenityTheme {
        SuccessContent(
            onEvent = {},
            uiState = MediaSelectorUiState.Success(
                items = buildImmutableList {
                    repeat(18) { index ->
                        val item = if (index % 2 == 0) {
                            MediaItem.Image(
                                id = index.toLong(),
                                uri = Uri.EMPTY,
                                title = "Test title $index",
                                state = if (index == 4) {
                                    SelectionState.Selected(1)
                                } else {
                                    SelectionState.Default
                                },
                            )
                        } else {
                            MediaItem.Video(
                                id = index + 10L,
                                uri = Uri.EMPTY,
                                title = "Test title $index",
                                state = if (index == 9) {
                                    SelectionState.Selected(2)
                                } else {
                                    SelectionState.Default
                                },
                                duration = "0:10",
                            )
                        }
                        add(item)
                    }
                },
                showPartialAccessMessage = true,
            ),
        )
    }
}
