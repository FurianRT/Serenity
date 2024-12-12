package com.furianrt.notecreate.internal.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
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
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.MovableToolbarState
import com.furianrt.uikit.components.SelectedDate
import com.furianrt.uikit.components.SingleChoiceCalendar
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.toDateString
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.utils.isGestureNavigationEnabled
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteCreateScreen(
    openMediaViewScreen: (noteId: String, mediaName: String, identifier: DialogIdentifier) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    onCloseRequest: () -> Unit,
) {
    val viewModel: NoteCreateViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val pageScreenState = rememberPageScreenState()
    var calendarDialogState: SelectedDate? by remember { mutableStateOf(null) }
    val hazeState = remember { HazeState() }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is NoteCreateEffect.SaveCurrentNoteContent -> pageScreenState.saveContent()
                    is NoteCreateEffect.CloseScreen -> onCloseRequest()
                    is NoteCreateEffect.ShowDateSelector -> {
                        calendarDialogState = SelectedDate(effect.date)
                    }
                }
            }
    }
    ScreenContent(
        modifier = Modifier.haze(hazeState),
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

    calendarDialogState?.let { dialogState ->
        SingleChoiceCalendar(
            selectedDate = dialogState,
            hazeState = hazeState,
            onDismissRequest = { calendarDialogState = null },
            onDateSelected = { viewModel.onEvent(NoteCreateEvent.OnDateSelected(it.date)) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenContent(
    state: PageScreenState,
    uiState: NoteCreateUiState,
    onEvent: (event: NoteCreateEvent) -> Unit,
    notePage: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val toolbarState = remember { MovableToolbarState() }
    BackHandler(
        enabled = uiState.isInEditMode && !isGestureNavigationEnabled(),
        onBack = { onEvent(NoteCreateEvent.OnButtonBackClick) },
    )

    LaunchedEffect(state.hasContentChanged) {
        onEvent(NoteCreateEvent.OnContentChanged(state.hasContentChanged))
    }

    LaunchedEffect(uiState.isInEditMode) {
        if (uiState.isInEditMode) {
            toolbarState.expand()
        }
    }

    MovableToolbarScaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        state = toolbarState,
        enabled = !state.bottomSheetState.isVisible && !uiState.isInEditMode,
        listState = state.listState,
        toolbar = {
            val date = remember(uiState.note.date) {
                uiState.note.date.toDateString()
            }
            Toolbar(
                modifier = Modifier.statusBarsPadding(),
                isInEditMode = uiState.isInEditMode,
                date = date,
                onEditClick = { onEvent(NoteCreateEvent.OnButtonEditClick) },
                onBackButtonClick = {
                    focusManager.clearFocus()
                    onEvent(NoteCreateEvent.OnButtonBackClick)
                },
                onDateClick = { onEvent(NoteCreateEvent.OnButtonDateClick) },
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
        },
        content = { notePage() },
    )
}

@Composable
private fun ToolbarDim(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.scrim)
            .statusBarsPadding()
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
                note = NoteItem(id = "", date = ZonedDateTime.now()),
                isInEditMode = true,
            ),
            onEvent = {},
            notePage = { Box(Modifier.fillMaxSize()) },
        )
    }
}
