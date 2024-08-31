package com.furianrt.mediaselector.internal.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import com.furianrt.mediaselector.internal.ui.composables.ConfirmDialog
import com.furianrt.mediaselector.internal.ui.composables.DragHandle
import com.furianrt.uikit.constants.ToolbarConstants
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
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

    val scope = rememberCoroutineScope()

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

    var showConfirmDialog by remember { mutableStateOf(false) }
    var skipConfirmation by remember { mutableStateOf(false) }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    )

    LaunchedEffect(true) {
        scaffoldState.bottomSheetState.expand()
    }

    var closeDialog by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(scaffoldState.bottomSheetState.isVisible) {
        val isBottomSheetVisible = scaffoldState.bottomSheetState.isVisible
        val selectedCount = (uiState as? MediaSelectorUiState.Success)?.selectedCount ?: 0
        when {
            !isBottomSheetVisible && !closeDialog -> closeDialog = true

            !isBottomSheetVisible && selectedCount > 0 && !skipConfirmation -> {
                scaffoldState.bottomSheetState.expand()
                showConfirmDialog = true
            }

            !isBottomSheetVisible -> navHostController.popBackStack()
        }
    }

    val sheetSwipeEnabled by remember(uiState) {
        derivedStateOf {
            uiState !is MediaSelectorUiState.Success ||
                    (uiState.listState.firstVisibleItemIndex == 0 &&
                            uiState.listState.firstVisibleItemScrollOffset == 0) ||
                    !uiState.listState.isScrollInProgress
        }
    }

    val hazeState = remember { HazeState() }
    val backgroundModifier = Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = ToolbarConstants.toolbarHeight / 2)
            .haze(state = hazeState),
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

    if (showConfirmDialog) {
        ConfirmDialog(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .hazeChild(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        blurRadius = 20.dp,
                    ),
                ),
            onDismissRequest = { showConfirmDialog = false },
            onConfirmClick = {
                skipConfirmation = true
                scope.launch { scaffoldState.bottomSheetState.hide() }
            },
        )
    }

    BackHandler(
        enabled = scaffoldState.bottomSheetState.isVisible,
        onBack = {
            if (uiState is MediaSelectorUiState.Success && uiState.selectedCount > 0) {
                showConfirmDialog = true
            } else {
                scope.launch { scaffoldState.bottomSheetState.hide() }
            }
        },
    )
}
