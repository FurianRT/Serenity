package com.furianrt.notecreate.internal.ui

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.notecreate.internal.ui.composables.Toolbar
import com.furianrt.notecreate.internal.ui.entites.NoteItem
import com.furianrt.notelistui.composables.ConfirmNotesDeleteDialog
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.notepage.api.NotePageScreen
import com.furianrt.notepage.api.PageScreenState
import com.furianrt.notepage.api.entities.NotePageAction
import com.furianrt.notepage.api.rememberPageScreenState
import com.furianrt.uikit.components.FileExportProgress
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.MovableToolbarState
import com.furianrt.uikit.components.SelectedDate
import com.furianrt.uikit.components.SingleChoiceCalendar
import com.furianrt.uikit.components.SnackBar
import com.furianrt.uikit.components.rememberMovableToolbarState
import com.furianrt.uikit.extensions.toDateString
import com.furianrt.uikit.theme.LocalFont
import com.furianrt.uikit.theme.LocalHasMediaRoute
import com.furianrt.uikit.theme.LocalHasMediaSortingRoute
import com.furianrt.uikit.theme.LocalIsLightTheme
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.IntentCreator
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.utils.isGestureNavigationEnabled
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.milliseconds
import com.furianrt.uikit.R as uiR

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
    val context = LocalContext.current

    val pageScreenState = rememberPageScreenState()
    var calendarDialogState: CalendarState? by remember { mutableStateOf(null) }
    val hazeState = rememberHazeState()
    val focusManager = LocalFocusManager.current
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarState = rememberMovableToolbarState()

    val generalErrorMessage = stringResource(uiR.string.general_error)

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)

    val graphicsLayer = rememberGraphicsLayer()
    var exportRedrawTrigger by remember { mutableIntStateOf(0) }
    var isPdfExportInProgress by remember { mutableStateOf(false) }
    var exportStubBitmap: BitmapPainter? by remember { mutableStateOf(null) }

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

                    is NoteCreateEffect.CaptureNoteScreenContent -> {
                        val bitmaps = mutableListOf<Bitmap>()
                        val scrollState = pageScreenState.listState
                        val pageSize = scrollState.viewportSize
                        val totalScroll = scrollState.maxValue
                        val defaultScroll = scrollState.value
                        var currentScroll = 6
                        isPdfExportInProgress = true
                        delay(100.milliseconds)
                        exportStubBitmap = BitmapPainter(graphicsLayer.toImageBitmap())
                        toolbarState.showBlur = false
                        delay(500.milliseconds)
                        while (currentScroll < totalScroll && bitmaps.size < 50) {
                            if (currentScroll > 6) {
                                toolbarState.isHidden = true
                            }
                            scrollState.scrollTo(currentScroll)
                            exportRedrawTrigger++
                            delay(50.milliseconds)
                            bitmaps.add(graphicsLayer.toImageBitmap().asAndroidBitmap())
                            currentScroll += pageSize
                        }
                        scrollState.scrollTo(currentScroll)
                        exportRedrawTrigger++
                        delay(50.milliseconds)
                        val lastBitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                        val y = (currentScroll - totalScroll).coerceAtLeast(0)
                        val cropped = Bitmap.createBitmap(
                            lastBitmap,
                            0,
                            y,
                            lastBitmap.width,
                            lastBitmap.height - y,
                        )
                        bitmaps.add(cropped)
                        toolbarState.isHidden = false
                        toolbarState.showBlur = true
                        scrollState.scrollTo(defaultScroll)
                        toolbarState.expand()
                        effect.onCaptured(bitmaps)
                        isPdfExportInProgress = false
                        exportStubBitmap = null
                    }

                    is NoteCreateEffect.SharePdfFile -> IntentCreator.pdfShareIntent(effect.uri)
                        .onSuccess { intent ->
                            context.startActivity(intent)
                        }
                        .onFailure { error ->
                            error.printStackTrace()
                            snackBarHostState.currentSnackbarData?.dismiss()
                            snackBarHostState.showSnackbar(
                                message = generalErrorMessage,
                                duration = SnackbarDuration.Short,
                            )
                        }
                }
            }
    }

    val successState = uiState as? NoteCreateUiState.Success
    val selectedBackground = when (val theme = successState?.note?.theme) {
        is UiNoteTheme.Solid -> theme.color
        is UiNoteTheme.Image -> theme.color
        null -> null
    }

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
                modifier = Modifier
                    .hazeSource(hazeState)
                    .drawWithContent {
                        if (isPdfExportInProgress && exportRedrawTrigger >= 0) {
                            graphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                        }
                        drawContent()
                    },
                state = pageScreenState,
                uiState = state,
                hazeState = hazeState,
                toolbarState = toolbarState,
                snackBarHostState = snackBarHostState,
                showToolbarActions = exportStubBitmap == null,
                onEvent = viewModel::onEvent,
                notePage = {
                    NotePageScreen(
                        state = pageScreenState,
                        noteId = state.note.id,
                        action = state.pageAction,
                        isInEditMode = state.isInEditMode,
                        isSelected = true,
                        isNoteCreationMode = true,
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

        AnimatedVisibility(
            visible = isPdfExportInProgress,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            FileExportProgress(
                modifier = Modifier.then(exportStubBitmap?.let { Modifier.paint(it) } ?: Modifier)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessContent(
    state: PageScreenState,
    uiState: NoteCreateUiState.Success,
    snackBarHostState: SnackbarHostState,
    toolbarState: MovableToolbarState,
    hazeState: HazeState,
    showToolbarActions: Boolean,
    onEvent: (event: NoteCreateEvent) -> Unit,
    notePage: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    val date = remember(uiState.note.date) { uiState.note.date.toDateString() }

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
        enabled = !state.mediaSelectorState.isVisible && !uiState.isInEditMode,
        listState = state.listState,
        contentOnTop = state.isBottomSheetExpanded,
        dimSurface = state.dimSurface,
        onDimClick = { scope.launch { state.mediaSelectorState.collapse() } },
        toolbar = {
            Toolbar(
                modifier = Modifier.statusBarsPadding(),
                isInEditMode = uiState.isInEditMode,
                isPinned = uiState.note.isPinned,
                date = date,
                dropDownHazeState = hazeState,
                showActionButtons = showToolbarActions,
                onEditClick = { onEvent(NoteCreateEvent.OnButtonEditClick) },
                onBackButtonClick = {
                    focusManager.clearFocus()
                    onEvent(NoteCreateEvent.OnButtonBackClick)
                },
                onDateClick = { onEvent(NoteCreateEvent.OnButtonDateClick) },
                onDeleteClick = { onEvent(NoteCreateEvent.OnButtonDeleteClick) },
                onPinClick = { onEvent(NoteCreateEvent.OnPinClick) },
                onExportPdfClick = { onEvent(NoteCreateEvent.OnExportPdfClick) },
            )
        },
        content = {
            notePage()
            SnackbarHost(
                modifier = Modifier.align(Alignment.BottomCenter),
                hostState = snackBarHostState,
                snackbar = { data ->
                    SnackBar(
                        title = data.visuals.message,
                        icon = painterResource(uiR.drawable.ic_error),
                        tonalColor = MaterialTheme.colorScheme.tertiaryContainer,
                    )
                },
            )
        },
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
                    theme = null,
                    date = ZonedDateTime.now(),
                    isPinned = false,
                ),
                isInEditMode = true,
                pageAction = NotePageAction.DEFAULT,
                font = LocalFont.current,
            ),
            hazeState = HazeState(),
            toolbarState = rememberMovableToolbarState(),
            snackBarHostState = SnackbarHostState(),
            showToolbarActions = true,
            onEvent = {},
            notePage = { Box(Modifier.fillMaxSize()) },
        )
    }
}
