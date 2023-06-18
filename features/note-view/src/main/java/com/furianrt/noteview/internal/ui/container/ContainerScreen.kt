package com.furianrt.noteview.internal.ui.container

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.noteview.internal.ui.container.composables.Toolbar
import com.furianrt.noteview.internal.ui.page.PageScreen
import com.furianrt.uikit.extensions.addSerenityBackground
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.extensions.isInMiddleState
import com.furianrt.uikit.extensions.performSnap
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.collections.immutable.persistentListOf
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

private const val OFFSCREEN_PAGE_COUNT = 1

@Composable
internal fun ContainerScreen() {
    val viewModel: ContainerViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
        }
    }

    ScreenContent(
        modifier = Modifier
            .fillMaxSize()
            .addSerenityBackground()
            .systemBarsPadding()
            .clipToBounds(),
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun ScreenContent(
    uiState: ContainerUiState,
    onEvent: (event: ContainerEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is ContainerUiState.Success -> SuccessScreen(uiState, onEvent, modifier)
        is ContainerUiState.Loading -> LoadingScreen(modifier)
        is ContainerUiState.Empty -> EmptyScreen(modifier)
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun SuccessScreen(
    uiState: ContainerUiState.Success,
    onEvent: (event: ContainerEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val toolbarScaffoldState = rememberCollapsingToolbarScaffoldState()
    val pagerState = rememberPagerState(initialPage = uiState.initialPageIndex)
    val listsScrollStates = remember { mutableStateMapOf<Int, LazyListState>() }
    val currentPageScrollState = remember(listsScrollStates.size, pagerState.currentPage) {
        listsScrollStates[pagerState.currentPage]
    }

    val needToSnapToolbar by remember(currentPageScrollState) {
        derivedStateOf {
            val isScrollInProgress = currentPageScrollState?.isScrollInProgress ?: false
            !isScrollInProgress &&
                toolbarScaffoldState.isInMiddleState &&
                !toolbarScaffoldState.toolbarState.isScrollInProgress
        }
    }

    LaunchedEffect(needToSnapToolbar) {
        if (needToSnapToolbar) {
            toolbarScaffoldState.performSnap()
        }
    }

    CollapsingToolbarScaffold(
        modifier = modifier,
        state = toolbarScaffoldState,
        scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed,
        toolbarModifier = Modifier.drawBehind {
            if (currentPageScrollState?.firstVisibleItemScrollOffset != 0) {
                drawBottomShadow(elevation = 8.dp)
            }
        },
        toolbar = {
            Toolbar(
                isInEditMode = { uiState.isInEditMode },
                date = { uiState.date },
                onEditClick = { onEvent(ContainerEvent.OnButtonEditClick) },
            )
        },
    ) {
        Spacer(modifier = Modifier)
        HorizontalPager(
            pageCount = uiState.notesIds.count(),
            beyondBoundsPageCount = OFFSCREEN_PAGE_COUNT,
            userScrollEnabled = !uiState.isInEditMode,
            verticalAlignment = Alignment.Top,
            state = pagerState,
        ) { index ->
            val lazyListState = rememberLazyListState()
            listsScrollStates[index] = lazyListState
            val isInEditMode by remember(uiState.isInEditMode) {
                derivedStateOf { pagerState.currentPage == index && uiState.isInEditMode }
            }
            PageScreen(
                noteId = uiState.notesIds[index],
                isInEditMode = isInEditMode,
                lazyListState = lazyListState,
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
}

@Preview
@Composable
private fun ContainerScreenSuccessPreview() {
    SerenityTheme {
        ScreenContent(
            uiState = ContainerUiState.Success(
                isInEditMode = false,
                initialPageIndex = 0,
                date = "30 Sep 1992",
                notesIds = persistentListOf("0", "1", "2"),
            ),
            onEvent = {},
        )
    }
}
