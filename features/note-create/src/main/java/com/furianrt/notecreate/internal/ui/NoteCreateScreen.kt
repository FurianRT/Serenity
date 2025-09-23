package com.furianrt.notecreate.internal.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.notecreate.internal.ui.composables.Toolbar
import com.furianrt.notecreate.internal.ui.entites.NoteItem
import com.furianrt.notelistui.composables.ConfirmNotesDeleteDialog
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
import com.furianrt.uikit.theme.LocalFont
import com.furianrt.uikit.theme.LocalHasMediaRoute
import com.furianrt.uikit.theme.LocalHasMediaSortingRoute
import com.furianrt.uikit.theme.LocalIsLightTheme
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.utils.isGestureNavigationEnabled
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime

@Immutable
private data class CalendarState(
    val date: SelectedDate,
    val datesWithNotes: Set<LocalDate>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteCreateScreen(
    openMediaViewScreen: (noteId: String, mediaId: String, identifier: DialogIdentifier) -> Unit,
    openMediaSortingScreen: (noteId: String, blockId: String, identifier: DialogIdentifier) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    onCloseRequest: () -> Unit,
) {
    val viewModel: NoteCreateViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val pageScreenState = rememberPageScreenState()
    var calendarDialogState: CalendarState? by remember { mutableStateOf(null) }
    val hazeState = remember { HazeState() }
    val focusManager = LocalFocusManager.current

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is NoteCreateEffect.CloseScreen -> {
                        focusManager.clearFocus()
                        onCloseRequestState()
                    }

                    is NoteCreateEffect.ShowDateSelector -> {
                        calendarDialogState = CalendarState(
                            date = SelectedDate(effect.date),
                            datesWithNotes = effect.datesWithNotes,
                        )
                    }

                    is NoteCreateEffect.ShowDeleteConfirmationDialog -> {
                        showDeleteConfirmationDialog = true
                    }
                }
            }
    }

    val successState = uiState as? NoteCreateUiState.Success
    val selectedBackground = successState?.note?.background

    val isLightTheme = when {
        LocalHasMediaRoute.current -> false
        LocalHasMediaSortingRoute.current -> LocalIsLightTheme.current
        else -> selectedBackground?.isLight ?: LocalIsLightTheme.current
    }

    SerenityTheme(
        isLightTheme = isLightTheme,
        font = successState?.font ?: LocalFont.current,
        colorScheme = selectedBackground?.colorScheme ?: MaterialTheme.colorScheme,
    ) {
        when (val state = uiState) {
            is NoteCreateUiState.Success -> SuccessContent(
                modifier = Modifier.hazeSource(hazeState),
                state = pageScreenState,
                uiState = state,
                hazeState = hazeState,
                onEvent = viewModel::onEvent,
                notePage = {
                    NotePageScreen(
                        state = pageScreenState,
                        noteId = state.note.id,
                        isInEditMode = state.isInEditMode,
                        isSelected = true,
                        isNoteCreationMode = true,
                        onBackgroundChanged = {
                            viewModel.onEvent(
                                NoteCreateEvent.OnBackgroundChanged(
                                    state.note.id,
                                    it
                                )
                            )
                        },
                        onTitleFocused = { viewModel.onEvent(NoteCreateEvent.OnPageTitleFocused) },
                        onLocationClick = { viewModel.onEvent(NoteCreateEvent.OnLocationClick) },
                        openMediaViewScreen = openMediaViewScreen,
                        openMediaViewer = openMediaViewer,
                        openMediaSortingScreen = openMediaSortingScreen,
                    )
                },
            )

            is NoteCreateUiState.Loading -> LoadingContent(
                modifier = Modifier.hazeSource(hazeState),
            )
        }


        calendarDialogState?.let { dialogState ->
            SingleChoiceCalendar(
                selectedDate = dialogState.date,
                hasNotes = { dialogState.datesWithNotes.contains(it) },
                hazeState = hazeState,
                onDismissRequest = { calendarDialogState = null },
                onDateSelected = { viewModel.onEvent(NoteCreateEvent.OnDateSelected(it.date)) },
            )
        }

        if (showDeleteConfirmationDialog) {
            ConfirmNotesDeleteDialog(
                notesCount = 1,
                hazeState = hazeState,
                onConfirmClick = { viewModel.onEvent(NoteCreateEvent.OnConfirmDeleteClick) },
                onDismissRequest = { showDeleteConfirmationDialog = false },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessContent(
    state: PageScreenState,
    uiState: NoteCreateUiState.Success,
    hazeState: HazeState,
    onEvent: (event: NoteCreateEvent) -> Unit,
    notePage: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val toolbarState = remember { MovableToolbarState() }

    val statusBarPv = WindowInsets.statusBars.asPaddingValues()
    val statusBarHeight = rememberSaveable { statusBarPv.calculateTopPadding().value }

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
                modifier = Modifier.padding(top = statusBarHeight.dp),
                isInEditMode = uiState.isInEditMode,
                isPinned = uiState.note.isPinned,
                date = date,
                dropDownHazeState = hazeState,
                onEditClick = { onEvent(NoteCreateEvent.OnButtonEditClick) },
                onBackButtonClick = {
                    focusManager.clearFocus()
                    onEvent(NoteCreateEvent.OnButtonBackClick)
                },
                onDateClick = { onEvent(NoteCreateEvent.OnButtonDateClick) },
                onDeleteClick = { onEvent(NoteCreateEvent.OnButtonDeleteClick) },
                onPinClick = { onEvent(NoteCreateEvent.OnPinClick) },
            )
            AnimatedVisibility(
                modifier = Modifier.zIndex(1f),
                visible = state.dimSurface,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.scrim)
                        .padding(top = statusBarHeight.dp)
                        .height(ToolbarConstants.toolbarHeight)
                        .clickableNoRipple {
                            scope.launch { state.bottomSheetState.hide() }
                        }
                )
            }
        },
        content = { notePage() },
    )
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        SuccessContent(
            state = rememberPageScreenState(),
            uiState = NoteCreateUiState.Success(
                note = NoteItem(
                    id = "",
                    background = null,
                    date = ZonedDateTime.now(),
                    isPinned = false,
                ),
                isInEditMode = true,
                font = LocalFont.current,
            ),
            hazeState = HazeState(),
            onEvent = {},
            notePage = { Box(Modifier.fillMaxSize()) },
        )
    }
}
