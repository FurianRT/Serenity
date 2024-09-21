package com.furianrt.notecreate.internal.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
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
import com.furianrt.notecreate.internal.ui.entites.NoteItem
import com.furianrt.notepage.api.NotePageScreen
import com.furianrt.notepage.api.PageScreenState
import com.furianrt.notepage.api.rememberPageScreenState
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.extensions.isExpanded
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.coroutines.flow.collectLatest
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy

@Composable
internal fun NoteCreateScreenInternal(navHostController: NavHostController) {
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
                    is NoteCreateEffect.CloseScreen -> navHostController.popBackStack()
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
                navHostController = navHostController,
                onFocusChange = { viewModel.onEvent(NoteCreateEvent.OnPageTitleFocusChange) },
            )
        },
    )
}

@Composable
private fun ScreenContent(
    state: PageScreenState,
    uiState: NoteCreateUiState,
    onEvent: (event: NoteCreateEvent) -> Unit,
    notePage: @Composable () -> Unit,
) {
    val isListAtTop by remember {
        derivedStateOf {
            state.listState.firstVisibleItemIndex == 0 &&
                    state.listState.firstVisibleItemScrollOffset == 0
        }
    }
    BackHandler(
        enabled = uiState.isInEditMode,
        onBack = {
            onEvent(
                NoteCreateEvent.OnButtonBackClick(
                    isContentSaved = !state.hasContentChanged,
                ),
            )
        },
    )
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
                Toolbar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                    isInEditMode = uiState.isInEditMode,
                    timestamp = uiState.note.timestamp,
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
            body = { notePage() },
        )
    }
}

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
