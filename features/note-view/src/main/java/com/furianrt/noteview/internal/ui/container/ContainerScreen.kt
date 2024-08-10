package com.furianrt.noteview.internal.ui.container

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import com.furianrt.core.buildImmutableList
import com.furianrt.noteview.internal.ui.container.composables.Toolbar
import com.furianrt.noteview.internal.ui.entites.ContainerScreenNote
import com.furianrt.noteview.internal.ui.page.PageScreen
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.extensions.expand
import com.furianrt.uikit.extensions.isCollapsed
import com.furianrt.uikit.extensions.isExpanded
import com.furianrt.uikit.extensions.isInMiddleState
import com.furianrt.uikit.extensions.performSnap
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.coroutines.flow.collectLatest
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@Composable
internal fun ContainerScreen(navHostController: NavHostController) {
    val viewModel: ContainerViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is ContainerEffect.CloseScreen -> {
                        navHostController.popBackStack()
                    }
                }
            }
    }
    ScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun ScreenContent(
    uiState: ContainerUiState,
    onEvent: (event: ContainerEvent) -> Unit,
) {
    Surface {
        when (uiState) {
            is ContainerUiState.Success -> SuccessScreen(uiState, onEvent)
            is ContainerUiState.Loading -> LoadingScreen()
            is ContainerUiState.Empty -> EmptyScreen()
        }
    }
}

@Composable
private fun SuccessScreen(
    uiState: ContainerUiState.Success,
    onEvent: (event: ContainerEvent) -> Unit,
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
            toolbarScaffoldState.expand()
        }
    }

    var date: String? by remember { mutableStateOf(null) }

    LaunchedEffect(key1 = pagerState.currentPage) {
        date = uiState.notes.getOrNull(pagerState.currentPage)?.date
    }

    CollapsingToolbarScaffold(
        modifier = Modifier.fillMaxSize(),
        state = toolbarScaffoldState,
        scrollStrategy = ScrollStrategy.EnterAlways,
        enabled = !uiState.isInEditMode || !toolbarScaffoldState.isExpanded,
        toolbarModifier = Modifier.drawBehind {
            if (currentPageScrollState?.firstVisibleItemScrollOffset != 0) {
                drawBottomShadow(elevation = 8.dp)
            }
        },
        toolbar = {
            Toolbar(
                isInEditMode = uiState.isInEditMode,
                date = date,
                onEditClick = { onEvent(ContainerEvent.OnButtonEditClick) },
                onBackButtonClick = { onEvent(ContainerEvent.OnButtonBackClick) },
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

            val isInEditMode by remember(uiState.isInEditMode) {
                derivedStateOf { pagerState.currentPage == index && uiState.isInEditMode }
            }

            PageScreen(
                noteId = uiState.notes[index].id,
                isInEditMode = isInEditMode,
                toolbarState = toolbarScaffoldState,
                listState = lazyListState,
                titleScrollState = titlesScrollState,
                onFocusChange = { onEvent(ContainerEvent.OnPageTitleFocusChange) },
            )
        }
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier)
}

@Composable
private fun EmptyScreen(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier)
}

@Preview
@Composable
private fun ContainerScreenSuccessPreview() {
    SerenityTheme {
        ScreenContent(
            uiState = ContainerUiState.Success(
                isInEditMode = false,
                initialPageIndex = 0,
                notes = buildImmutableList {
                    add(ContainerScreenNote(id = "1", date = "30 Sep 1992"))
                    add(ContainerScreenNote(id = "2", date = "22 Feb 2003"))
                    add(ContainerScreenNote(id = "3", date = "03 Dec 1900"))
                },
            ),
            onEvent = {},
        )
    }
}
