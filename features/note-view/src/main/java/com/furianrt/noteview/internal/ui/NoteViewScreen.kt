package com.furianrt.noteview.internal.ui

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.core.orFalse
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.notelistui.composables.ConfirmNotesDeleteDialog
import com.furianrt.notepage.api.NotePageScreen
import com.furianrt.notepage.api.PageScreenState
import com.furianrt.notepage.api.rememberPageScreenState
import com.furianrt.noteview.internal.ui.composables.Toolbar
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.MovableToolbarState
import com.furianrt.uikit.components.SelectedDate
import com.furianrt.uikit.components.SingleChoiceCalendar
import com.furianrt.uikit.components.SnackBar
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.toDateString
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import com.furianrt.uikit.R as uiR

@Immutable
private data class CalendarState(
    val date: SelectedDate,
    val datesWithNotes: Set<LocalDate>,
)

@Composable
internal fun NoteViewScreen(
    openMediaViewScreen: (noteId: String, mediaId: String, identifier: DialogIdentifier) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    onCloseRequest: () -> Unit,
) {
    val viewModel: NoteViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    var calendarDialogState: CalendarState? by remember { mutableStateOf(null) }
    var deleteConfirmationDialogState: String? by remember { mutableStateOf(null) }
    val hazeState = remember { HazeState() }
    val snackBarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is NoteViewEffect.CloseScreen -> {
                        focusManager.clearFocus()
                        onCloseRequestState()
                    }
                    is NoteViewEffect.ShowDateSelector -> {
                        calendarDialogState = CalendarState(
                            date = SelectedDate(effect.date),
                            datesWithNotes = effect.datesWithNotes,
                        )
                    }

                    is NoteViewEffect.ShowDeleteConfirmationDialog -> {
                        deleteConfirmationDialogState = effect.noteId
                    }

                    is NoteViewEffect.ShowSyncProgressMessage -> {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = effect.message,
                            duration = SnackbarDuration.Short,
                        )
                    }
                }
            }
    }
    ScreenContent(
        modifier = Modifier.haze(hazeState),
        uiState = uiState,
        hazeState = hazeState,
        snackBarHostState = snackBarHostState,
        onEvent = viewModel::onEvent,
        openMediaViewScreen = openMediaViewScreen,
        openMediaViewer = openMediaViewer,
    )
    calendarDialogState?.let { dialogState ->
        SingleChoiceCalendar(
            selectedDate = dialogState.date,
            hasNotes = { dialogState.datesWithNotes.contains(it) },
            hazeState = hazeState,
            onDismissRequest = { calendarDialogState = null },
            onDateSelected = { viewModel.onEvent(NoteViewEvent.OnDateSelected(it.date)) },
        )
    }
    deleteConfirmationDialogState?.let { dialogState ->
        ConfirmNotesDeleteDialog(
            notesCount = 1,
            hazeState = hazeState,
            onConfirmClick = { viewModel.onEvent(NoteViewEvent.OnConfirmDeleteClick(dialogState)) },
            onDismissRequest = { deleteConfirmationDialogState = null },
        )
    }
}

@Composable
private fun ScreenContent(
    uiState: NoteViewUiState,
    snackBarHostState: SnackbarHostState,
    hazeState: HazeState,
    onEvent: (event: NoteViewEvent) -> Unit,
    openMediaViewScreen: (noteId: String, mediaId: String, identifier: DialogIdentifier) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is NoteViewUiState.Loading -> LoadingScreen(
            modifier = modifier,
        )

        is NoteViewUiState.Success -> SuccessScreen(
            modifier = modifier,
            uiState = uiState,
            snackBarHostState = snackBarHostState,
            hazeState = hazeState,
            onEvent = onEvent,
            openMediaViewScreen = openMediaViewScreen,
            openMediaViewer = openMediaViewer,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessScreen(
    uiState: NoteViewUiState.Success,
    snackBarHostState: SnackbarHostState,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    openMediaViewScreen: (noteId: String, mediaId: String, identifier: DialogIdentifier) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    onEvent: (event: NoteViewEvent) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val pagerState = rememberPagerState(
        initialPage = uiState.initialPageIndex,
        pageCount = { uiState.notes.count() },
    )
    val pageScreensStates = remember { mutableStateMapOf<Int, PageScreenState>() }
    val currentPageState = remember(pageScreensStates.size, pagerState.currentPage) {
        pageScreensStates[pagerState.currentPage]
    }

    val toolbarState = remember { MovableToolbarState() }
    var skipToolbarExpand by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }
            .collectLatest { currentPage ->
                onEvent(NoteViewEvent.OnPageChange(currentPage))
                if (!skipToolbarExpand) {
                    toolbarState.expand()
                } else {
                    skipToolbarExpand = false
                }
            }
    }

    LaunchedEffect(uiState.currentPageIndex) {
        if (uiState.currentPageIndex != pagerState.currentPage) {
            pagerState.scrollToPage(uiState.currentPageIndex)
        }
    }

    LaunchedEffect(currentPageState?.hasContentChanged.orFalse()) {
        onEvent(NoteViewEvent.OnContentChanged(currentPageState?.hasContentChanged.orFalse()))
    }

    BackHandler(
        enabled = uiState.isInEditMode,
        onBack = { onEvent(NoteViewEvent.OnButtonEditClick) },
    )

    val scope = rememberCoroutineScope()

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
        listState = currentPageState?.listState ?: rememberScrollState(),
        enabled = currentPageState?.bottomSheetState?.isVisible == false && !uiState.isInEditMode,
        toolbar = {
            val date = remember(uiState.date) {
                uiState.date.toDateString()
            }
            Toolbar(
                modifier = Modifier.statusBarsPadding(),
                isInEditMode = uiState.isInEditMode,
                date = date,
                isPinned = uiState.notes[pagerState.currentPage].isPinned,
                dropDownHazeState = hazeState,
                onEditClick = { onEvent(NoteViewEvent.OnButtonEditClick) },
                onBackButtonClick = {
                    focusManager.clearFocus()
                    onEvent(NoteViewEvent.OnButtonBackClick)
                },
                onDateClick = { onEvent(NoteViewEvent.OnButtonDateClick) },
                onDeleteClick = {
                    val noteId = uiState.notes[pagerState.currentPage].id
                    onEvent(NoteViewEvent.OnDeleteClick(noteId))
                },
                onPinClick = {
                    val note = uiState.notes[pagerState.currentPage]
                    onEvent(
                        NoteViewEvent.OnPinClick(
                            noteId = note.id,
                            isPinned = note.isPinned,
                        )
                    )
                }
            )
            AnimatedVisibility(
                modifier = Modifier.zIndex(1f),
                visible = currentPageState?.dimSurface.orFalse(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                ToolbarDim {
                    scope.launch { currentPageState?.bottomSheetState?.hide() }
                }
            }
        },
    ) {
        HorizontalPager(
            userScrollEnabled = !uiState.isInEditMode,
            verticalAlignment = Alignment.Top,
            state = pagerState,
            key = { uiState.notes[it].id },
        ) { index ->
            val pageScreenState = rememberPageScreenState()
            pageScreensStates[index] = pageScreenState

            val isCurrentPage by remember(index) {
                derivedStateOf { pagerState.currentPage == index }
            }

            NotePageScreen(
                state = pageScreenState,
                noteId = uiState.notes[index].id,
                isSelected = isCurrentPage,
                isInEditMode = isCurrentPage && uiState.isInEditMode,
                isNoteCreationMode = false,
                onFocusChange = { onEvent(NoteViewEvent.OnPageTitleFocusChange) },
                openMediaViewScreen = openMediaViewScreen,
                openMediaViewer = openMediaViewer,
            )
        }

        SnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            hostState = snackBarHostState,
            snackbar = { data ->
                SnackBar(
                    title = data.visuals.message,
                    icon = painterResource(uiR.drawable.ic_cloud_sync),
                    tonalColor = MaterialTheme.colorScheme.tertiaryContainer,
                )
            },
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
            .background(MaterialTheme.colorScheme.scrim)
            .statusBarsPadding()
            .height(ToolbarConstants.toolbarHeight)
            .clickableNoRipple(onClick = onClick)
    )
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize())
}

@PreviewWithBackground
@Composable
private fun ScreenSuccessPreview() {
    SerenityTheme {
        ScreenContent(
            uiState = NoteViewUiState.Success(
                isInEditMode = false,
                initialPageIndex = 0,
                currentPageIndex = 0,
                notes = persistentListOf(),
                date = ZonedDateTime.now(),
            ),
            snackBarHostState = SnackbarHostState(),
            hazeState = HazeState(),
            onEvent = {},
            openMediaViewScreen = { _, _, _ -> },
            openMediaViewer = {},
        )
    }
}
