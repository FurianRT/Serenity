package com.furianrt.noteview.internal.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import com.furianrt.notepage.api.NotePageScreen
import com.furianrt.notepage.api.PageScreenState
import com.furianrt.notepage.api.rememberPageScreenState
import com.furianrt.noteview.internal.ui.composables.Toolbar
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.extensions.expand
import com.furianrt.uikit.extensions.isExpanded
import com.furianrt.uikit.extensions.isInMiddleState
import com.furianrt.uikit.extensions.performSnap
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
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
internal fun NoteViewScreenInternal(navHostController: NavHostController) {
    val viewModel: NoteViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val successScreenState = rememberSuccessScreenState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is NoteViewEffect.CloseScreen -> navHostController.popBackStack()
                    is NoteViewEffect.SaveCurrentNoteContent -> successScreenState.saveContent()
                }
            }
    }
    ScreenContent(
        state = successScreenState,
        uiState = uiState,
        onEvent = viewModel::onEvent,
        navHostController = navHostController,
    )
}

@Composable
private fun ScreenContent(
    state: SuccessScreenState,
    uiState: NoteViewUiState,
    onEvent: (event: NoteViewEvent) -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        when (uiState) {
            is NoteViewUiState.Loading -> LoadingScreen()
            is NoteViewUiState.Success -> SuccessScreen(
                state = state,
                uiState = uiState,
                onEvent = onEvent,
                navHostController = navHostController,
            )
        }
    }
}

@Composable
private fun SuccessScreen(
    state: SuccessScreenState,
    uiState: NoteViewUiState.Success,
    onEvent: (event: NoteViewEvent) -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val toolbarScaffoldState = rememberCollapsingToolbarScaffoldState()
    val pagerState = rememberPagerState(
        initialPage = uiState.initialPageIndex,
        pageCount = { uiState.notes.count() },
    )
    val pageScreensStates = remember { mutableStateMapOf<Int, PageScreenState>() }
    val currentPageScreenState = remember(pageScreensStates.size, pagerState.currentPage) {
        pageScreensStates[pagerState.currentPage]
    }

    state.setOnSaveContentListener { currentPageScreenState?.saveContent() }

    val needToSnapToolbar by remember(currentPageScreenState) {
        derivedStateOf {
            val isScrollInProgress = currentPageScreenState?.listState?.isScrollInProgress ?: false
            val isTitleScrollInProgress =
                currentPageScreenState?.titleScrollState?.isScrollInProgress ?: false
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

    val isListAtTop by remember(currentPageScreenState) {
        derivedStateOf {
            currentPageScreenState?.listState?.firstVisibleItemIndex == 0 &&
                    currentPageScreenState.listState.firstVisibleItemScrollOffset == 0
        }
    }

    CollapsingToolbarScaffold(
        modifier = modifier.fillMaxSize(),
        state = toolbarScaffoldState,
        scrollStrategy = ScrollStrategy.EnterAlways,
        enabled = !uiState.isInEditMode || !toolbarScaffoldState.isExpanded,
        toolbarModifier = Modifier.drawBehind {
            if (!isListAtTop) {
                drawBottomShadow(elevation = 8.dp)
            }
        },
        toolbar = {
            Toolbar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                isInEditMode = uiState.isInEditMode,
                date = date,
                onEditClick = { onEvent(NoteViewEvent.OnButtonEditClick) },
                onBackButtonClick = {
                    onEvent(
                        NoteViewEvent.OnButtonBackClick(
                            isContentSaved = !(currentPageScreenState?.hasContentChanged ?: false),
                        ),
                    )
                },
                onDateClick = {},
            )
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
                navHostController = navHostController,
                onFocusChange = { onEvent(NoteViewEvent.OnPageTitleFocusChange) },
            )
        }
    }
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
            state = rememberSuccessScreenState(),
            uiState = NoteViewUiState.Success(
                isInEditMode = false,
                initialPageIndex = 0,
                notes = persistentListOf(),
            ),
            onEvent = {},
            navHostController = NavHostController(LocalContext.current),
        )
    }
}