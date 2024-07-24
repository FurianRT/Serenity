package com.furianrt.noteview.internal.ui.container

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.furianrt.core.buildImmutableList
import com.furianrt.noteview.internal.ui.container.composables.Toolbar
import com.furianrt.noteview.internal.ui.entites.ContainerScreenNote
import com.furianrt.noteview.internal.ui.page.PageScreen
import com.furianrt.uikit.theme.SerenityTheme

private const val OFFSCREEN_PAGE_COUNT = 1

@Composable
internal fun ContainerScreen(navHostController: NavHostController) {
    val viewModel: ContainerViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ContainerEffect.CloseScreen -> {
                    navHostController.popBackStack()
                }
            }
        }
    }
    ScreenContent(
        modifier = Modifier
            .fillMaxSize()
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
    Surface(modifier = modifier) {
        when (uiState) {
            is ContainerUiState.Success -> SuccessScreen(uiState, onEvent, modifier)
            is ContainerUiState.Loading -> LoadingScreen(modifier)
            is ContainerUiState.Empty -> EmptyScreen(modifier)
        }
    }
}

@Composable
private fun SuccessScreen(
    uiState: ContainerUiState.Success,
    onEvent: (event: ContainerEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(
        initialPage = uiState.initialPageIndex,
        initialPageOffsetFraction = 0f,
        pageCount = { uiState.notes.count() },
    )
    var date: String? by remember { mutableStateOf(null) }

    LaunchedEffect(key1 = pagerState.currentPage) {
        date = uiState.notes.getOrNull(pagerState.currentPage)?.date
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Toolbar(
            isInEditMode = uiState.isInEditMode,
            date = date,
            onEditClick = { onEvent(ContainerEvent.OnButtonEditClick) },
            onBackButtonClick = { onEvent(ContainerEvent.OnButtonBackClick) },
            onDateClick = {},
        )

        HorizontalPager(
            beyondViewportPageCount = OFFSCREEN_PAGE_COUNT,
            userScrollEnabled = !uiState.isInEditMode,
            verticalAlignment = Alignment.Top,
            state = pagerState,
        ) { index ->
            val isCurrentPage by remember {
                derivedStateOf { pagerState.currentPage == index }
            }
            PageScreen(
                noteId = uiState.notes[index].id,
                isInEditMode = isCurrentPage && uiState.isInEditMode,
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
