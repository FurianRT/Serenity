package com.furianrt.noteview.internal.ui

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.furianrt.notepage.api.NotePageScreen
import com.furianrt.notepage.api.PageScreenState
import com.furianrt.notepage.api.rememberPageScreenState
import com.furianrt.noteview.internal.ui.composables.Toolbar
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.extensions.expand
import com.furianrt.uikit.extensions.isExpanded
import com.furianrt.uikit.extensions.isInMiddleState
import com.furianrt.uikit.extensions.performSnap
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@Stable
internal class SuccessScreenState {
    private var onSaveContentRequest: () -> Unit = {}

    fun setOnSaveContentListener(callback: () -> Unit) {
        onSaveContentRequest = callback
    }

    fun saveContent() {
        onSaveContentRequest()
    }
}

@Composable
internal fun rememberSuccessScreenState(): SuccessScreenState = remember { SuccessScreenState() }

@Composable
internal fun NoteViewScreen(
    openMediaViewScreen: (noteId: String, mediaName: String, identifier: DialogIdentifier) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    onCloseRequest: () -> Unit,
) {
    val viewModel: NoteViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val successScreenState = rememberSuccessScreenState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is NoteViewEffect.CloseScreen -> onCloseRequest()
                    is NoteViewEffect.SaveCurrentNoteContent -> successScreenState.saveContent()
                }
            }
    }
    ScreenContent(
        state = successScreenState,
        uiState = uiState,
        onEvent = viewModel::onEvent,
        openMediaViewScreen = openMediaViewScreen,
        openMediaViewer = openMediaViewer,
    )
}

@Composable
private fun ScreenContent(
    state: SuccessScreenState,
    uiState: NoteViewUiState,
    onEvent: (event: NoteViewEvent) -> Unit,
    openMediaViewScreen: (noteId: String, mediaName: String, identifier: DialogIdentifier) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        when (uiState) {
            is NoteViewUiState.Loading -> LoadingScreen()
            is NoteViewUiState.Success -> SuccessScreen(
                state = state,
                uiState = uiState,
                onEvent = onEvent,
                openMediaViewScreen = openMediaViewScreen,
                openMediaViewer = openMediaViewer,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessScreen(
    uiState: NoteViewUiState.Success,
    modifier: Modifier = Modifier,
    state: SuccessScreenState = rememberSuccessScreenState(),
    openMediaViewScreen: (noteId: String, mediaName: String, identifier: DialogIdentifier) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    onEvent: (event: NoteViewEvent) -> Unit = {},
) {
    val toolbarScaffoldState = rememberCollapsingToolbarScaffoldState()
    val pagerState = rememberPagerState(
        initialPage = uiState.initialPageIndex,
        pageCount = { uiState.notes.count() },
    )
    val pageScreensStates = remember { mutableStateMapOf<Int, PageScreenState>() }
    val currentPageState = remember(pageScreensStates.size, pagerState.currentPage) {
        pageScreensStates[pagerState.currentPage]
    }

    state.setOnSaveContentListener { currentPageState?.saveContent() }

    val needToSnapToolbar by remember(currentPageState) {
        derivedStateOf {
            val isScrollInProgress = currentPageState?.listState?.isScrollInProgress ?: false
            val isTitleScrollInProgress =
                currentPageState?.titleScrollState?.isScrollInProgress ?: false
            !isScrollInProgress &&
                    !isTitleScrollInProgress &&
                    toolbarScaffoldState.isInMiddleState &&
                    !toolbarScaffoldState.toolbarState.isScrollInProgress
        }
    }

    LaunchedEffect(needToSnapToolbar) {
        if (needToSnapToolbar) {
            toolbarScaffoldState.performSnap()
        }
    }

    LaunchedEffect(uiState.isInEditMode) {
        if (uiState.isInEditMode) {
            toolbarScaffoldState.expand(duration = 0)
        }
    }

    var date: String? by remember { mutableStateOf(null) }

    LaunchedEffect(pagerState.currentPage) {
        date = uiState.notes.getOrNull(pagerState.currentPage)?.date
        onEvent(NoteViewEvent.OnPageChange(pagerState.currentPage))
    }

    BackHandler(
        enabled = uiState.isInEditMode,
        onBack = { onEvent(NoteViewEvent.OnButtonEditClick) },
    )

    val isListAtTop by remember(currentPageState) {
        derivedStateOf {
            currentPageState?.listState?.firstVisibleItemIndex == 0 &&
                    currentPageState.listState.firstVisibleItemScrollOffset == 0
        }
    }

    val hazeState = remember { HazeState() }

    val scope = rememberCoroutineScope()

    CollapsingToolbarScaffold(
        modifier = modifier
            .fillMaxSize()
            .haze(hazeState),
        state = toolbarScaffoldState,
        scrollStrategy = ScrollStrategy.EnterAlways,
        enabled = !uiState.isInEditMode || !toolbarScaffoldState.isExpanded,
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
                    date = date,
                    dropDownHazeState = hazeState,
                    onEditClick = { onEvent(NoteViewEvent.OnButtonEditClick) },
                    onBackButtonClick = {
                        onEvent(
                            NoteViewEvent.OnButtonBackClick(
                                isContentSaved = !(currentPageState?.hasContentChanged
                                    ?: false),
                            ),
                        )
                    },
                    onDateClick = {},
                    onDeleteClick = {
                        val noteId = uiState.notes[pagerState.currentPage].id
                        onEvent(NoteViewEvent.OnDeleteClick(noteId))
                    },
                )
                AnimatedVisibility(
                    modifier = Modifier.zIndex(1f),
                    visible = currentPageState?.bottomSheetState?.isVisible == true ||
                            currentPageState?.bottomSheetState?.targetValue == SheetValue.Expanded,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    ToolbarDim {
                        scope.launch { currentPageState?.bottomSheetState?.hide() }
                    }
                }
            }
        },
    ) {
        HorizontalPager(
            modifier = Modifier,
            userScrollEnabled = !uiState.isInEditMode,
            verticalAlignment = Alignment.Top,
            state = pagerState,
        ) { index ->
            val pageScreenState = rememberPageScreenState(toolbarState = toolbarScaffoldState)
            pageScreensStates[index] = pageScreenState

            val isCurrentPage by remember { derivedStateOf { pagerState.currentPage == index } }

            NotePageScreen(
                state = pageScreenState,
                noteId = uiState.notes[index].id,
                isInEditMode = isCurrentPage && uiState.isInEditMode,
                isNoteCreationMode = false,
                onFocusChange = { onEvent(NoteViewEvent.OnPageTitleFocusChange) },
                openMediaViewScreen = openMediaViewScreen,
                openMediaViewer = openMediaViewer,
            )
        }
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
        SuccessScreen(
            uiState = NoteViewUiState.Success(
                isInEditMode = false,
                initialPageIndex = 0,
                notes = persistentListOf(),
            ),
            openMediaViewScreen = { _, _, _ -> },
            openMediaViewer = {},
        )
    }
}
