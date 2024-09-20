package com.furianrt.notecreate.internal.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import com.furianrt.notecreate.internal.ui.composables.Toolbar
import com.furianrt.notepage.api.NotePageScreen
import com.furianrt.notepage.api.PageScreenState
import com.furianrt.notepage.api.rememberPageScreenState
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.extensions.isExpanded
import kotlinx.coroutines.flow.collectLatest
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@Composable
internal fun NoteCreateScreenInternal(navHostController: NavHostController) {
    val viewModel: PageCreateViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val toolbarScaffoldState = rememberCollapsingToolbarScaffoldState()
    val pageScreenState = rememberPageScreenState(toolbarState = toolbarScaffoldState)

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is NoteCreateEffect.SaveCurrentNoteContent -> pageScreenState.saveContent()
                    is NoteCreateEffect.CloseScreen -> navHostController.popBackStack()
                }
            }
    }
    ScreenContent(
        state = pageScreenState,
        uiState = uiState,
        onEvent = viewModel::onEvent,
        navHostController = navHostController,
    )
}

@Composable
private fun ScreenContent(
    state: PageScreenState,
    uiState: NoteCreateUiState,
    navHostController: NavHostController,
    onEvent: (event: NoteCreateEvent) -> Unit,
    modifier: Modifier = Modifier,
) {


    val isListAtTop by remember {
        derivedStateOf {
            state.listState.firstVisibleItemIndex == 0 &&
                    state.listState.firstVisibleItemScrollOffset == 0
        }
    }
    Surface(modifier = modifier) {
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
                Toolbar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                    isInEditMode = uiState.isInEditMode,
                    timestamp = uiState.timestamp,
                    onEditClick = { onEvent(NoteCreateEvent.OnButtonEditClick) },
                    onBackButtonClick = {
                        onEvent(
                            NoteCreateEvent.OnButtonBackClick(
                                isContentSaved = !state.hasContentChanged,
                            ),
                        )
                    },
                    onDateClick = {},
                )
            },
        ) {
            NotePageScreen(
                state = state,
                noteId = uiState.noteId,
                isInEditMode = uiState.isInEditMode,
                isNoteCreationMode = true,
                navHostController = navHostController,
                onFocusChange = { onEvent(NoteCreateEvent.OnPageTitleFocusChange) },
            )
        }
    }
}
