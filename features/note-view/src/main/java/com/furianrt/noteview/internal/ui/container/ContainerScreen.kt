package com.furianrt.noteview.internal.ui.container

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
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
import com.furianrt.noteview.internal.ui.container.composables.Toolbar
import com.furianrt.noteview.internal.ui.page.PageScreen
import com.furianrt.noteview.internal.ui.page.PageScreenState
import com.furianrt.noteview.internal.ui.page.rememberPageScreenState
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
    internal var onSaveContentRequest: () -> Unit = {}

    fun saveContent() {
        onSaveContentRequest()
    }
}

@Composable
internal fun rememberSuccessScreenState(): SuccessScreenState {
    return remember { SuccessScreenState() }
}

@Composable
internal fun ContainerScreen(navHostController: NavHostController) {
    val viewModel: ContainerViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val successScreenState = rememberSuccessScreenState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is ContainerEffect.CloseScreen -> navHostController.popBackStack()
                    is ContainerEffect.SaveCurrentNoteContent -> successScreenState.saveContent()
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
    uiState: ContainerUiState,
    onEvent: (event: ContainerEvent) -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        when (uiState) {
            is ContainerUiState.Loading -> LoadingScreen()
            is ContainerUiState.Success -> SuccessScreen(
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
    uiState: ContainerUiState.Success,
    onEvent: (event: ContainerEvent) -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val toolbarScaffoldState = rememberCollapsingToolbarScaffoldState()
    val pagerState = rememberPagerState(
        initialPage = uiState.initialPageIndex,
        pageCount = { uiState.notes.count() },
    )
    val listsScrollStates = remember { mutableStateMapOf<Int, LazyListState>() }
    val currentPageScrollState = remember(listsScrollStates.size, pagerState.currentPage) {
        listsScrollStates[pagerState.currentPage]
    }

    val titlesScrollStates = remember { mutableStateMapOf<Int, ScrollState>() }
    val currentPageTitlesScrollState = remember(titlesScrollStates.size, pagerState.currentPage) {
        titlesScrollStates[pagerState.currentPage]
    }

    val pageScreensStates = remember { mutableStateMapOf<Int, PageScreenState>() }
    val currentPageScreenState = remember(listsScrollStates.size, pagerState.currentPage) {
        pageScreensStates[pagerState.currentPage]
    }

    state.onSaveContentRequest = { currentPageScreenState?.saveContent() }

    val needToSnapToolbar by remember(currentPageScrollState) {
        derivedStateOf {
            val isScrollInProgress = currentPageScrollState?.isScrollInProgress ?: false
            val isTitleScrollInProgress = currentPageTitlesScrollState?.isScrollInProgress ?: false
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

    LaunchedEffect(key1 = pagerState.currentPage) {
        date = uiState.notes.getOrNull(pagerState.currentPage)?.date
    }

    BackHandler(
        enabled = uiState.isInEditMode,
        onBack = { onEvent(ContainerEvent.OnButtonEditClick) },
    )

    val isListAtTop by remember(currentPageScrollState) {
        derivedStateOf {
            currentPageScrollState?.firstVisibleItemIndex == 0 &&
                    currentPageScrollState.firstVisibleItemScrollOffset == 0
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
                isInEditMode = uiState.isInEditMode,
                date = date,
                onEditClick = { onEvent(ContainerEvent.OnButtonEditClick) },
                onBackButtonClick = {
                    onEvent(
                        ContainerEvent.OnButtonBackClick(
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
            val lazyListState = rememberLazyListState()
            listsScrollStates[index] = lazyListState

            val titlesScrollState = rememberScrollState()
            titlesScrollStates[index] = titlesScrollState

            val pageScreenState = rememberPageScreenState()
            pageScreensStates[index] = pageScreenState

            val isCurrentPage by remember { derivedStateOf { pagerState.currentPage == index } }

            PageScreen(
                state = pageScreenState,
                noteId = uiState.notes[index].id,
                isInEditMode = isCurrentPage && uiState.isInEditMode,
                toolbarState = toolbarScaffoldState,
                listState = lazyListState,
                titleScrollState = titlesScrollState,
                navHostController = navHostController,
                onFocusChange = { onEvent(ContainerEvent.OnPageTitleFocusChange) },
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
            uiState = ContainerUiState.Success(
                isInEditMode = false,
                initialPageIndex = 0,
                notes = persistentListOf(),
            ),
            onEvent = {},
            navHostController = NavHostController(LocalContext.current),
        )
    }
}
