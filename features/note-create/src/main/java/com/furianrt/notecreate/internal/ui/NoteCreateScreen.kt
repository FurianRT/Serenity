package com.furianrt.notecreate.internal.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.notecreate.internal.ui.composables.Toolbar
import com.furianrt.notecreate.internal.ui.entites.NoteItem
import com.furianrt.notepage.api.NotePageScreen
import com.furianrt.notepage.api.PageScreenState
import com.furianrt.notepage.api.rememberPageScreenState
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.extensions.isExpanded
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.utils.isGestureNavigationEnabled
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteCreateScreen(
    openMediaViewScreen: (noteId: String, mediaName: String, identifier: DialogIdentifier) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    onCloseRequest: () -> Unit,
) {
    val viewModel: NoteCreateViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val pageScreenState = rememberPageScreenState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is NoteCreateEffect.SaveCurrentNoteContent -> pageScreenState.saveContent()
                    is NoteCreateEffect.CloseScreen -> onCloseRequest()
                }
            }
    }
    ScreenContent(
        state = pageScreenState,
        uiState = uiState,
        onEvent = viewModel::onEvent,
        notePage = {
            NotePageScreen(
                state = pageScreenState,
                noteId = uiState.note.id,
                isInEditMode = uiState.isInEditMode,
                isNoteCreationMode = true,
                onFocusChange = { viewModel.onEvent(NoteCreateEvent.OnPageTitleFocusChange) },
                openMediaViewScreen = openMediaViewScreen,
                openMediaViewer = openMediaViewer,
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenContent(
    state: PageScreenState,
    uiState: NoteCreateUiState,
    onEvent: (event: NoteCreateEvent) -> Unit,
    notePage: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    BackHandler(
        enabled = uiState.isInEditMode && !isGestureNavigationEnabled(),
        onBack = { onEvent(NoteCreateEvent.OnButtonBackClick) },
    )

    LaunchedEffect(state.hasContentChanged) {
        onEvent(NoteCreateEvent.OnContentChanged(state.hasContentChanged))
    }

    val isListAtTop by remember {
        derivedStateOf {
            state.listState.firstVisibleItemIndex == 0 &&
                    state.listState.firstVisibleItemScrollOffset == 0
        }
    }
    Surface {
        CollapsingToolbarScaffold(
            modifier = Modifier.fillMaxSize(),
            state = state.toolbarState,
            scrollStrategy = ScrollStrategy.EnterAlways,
            enabled = !uiState.isInEditMode || !state.toolbarState.isExpanded,
            toolbarModifier = Modifier.drawBehind {
                if (!isListAtTop) {
                    drawBottomShadow(elevation = 8.dp)
                }
            },
            toolbar = {
                Box {
                    Toolbar(
                        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                        isInEditMode = uiState.isInEditMode,
                        timestamp = uiState.note.timestamp,
                        onEditClick = { onEvent(NoteCreateEvent.OnButtonEditClick) },
                        onBackButtonClick = { onEvent(NoteCreateEvent.OnButtonBackClick) },
                        onDateClick = {},
                    )
                    AnimatedVisibility(
                        modifier = Modifier.zIndex(1f),
                        visible = state.bottomSheetState.isVisible ||
                                state.bottomSheetState.targetValue == SheetValue.Expanded,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        ToolbarDim {
                            scope.launch { state.bottomSheetState.hide() }
                        }
                    }
                }
            },
            body = { notePage() },
        )
    }
}

@Composable
private fun ToolbarDim(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.4f))
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(ToolbarConstants.toolbarHeight)
            .clickableNoRipple(onClick = onClick)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        ScreenContent(
            state = rememberPageScreenState(),
            uiState = NoteCreateUiState(
                note = NoteItem(id = "", timestamp = System.currentTimeMillis()),
                isInEditMode = true,
            ),
            onEvent = {},
            notePage = { Box(Modifier.fillMaxSize()) },
        )
    }
}
