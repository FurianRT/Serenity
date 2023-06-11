package com.furianrt.noteview.internal.ui.container

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.noteview.internal.ui.container.composables.Toolbar
import com.furianrt.uikit.composables.NoteItem
import com.furianrt.uikit.extensions.addSerenityBackground
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.generatePreviewNotes
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@Composable
internal fun ContainerScreen() {
    val viewModel: ContainerViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            /*when (effect) {
                is MainEffect.ScrollToTop -> screenState.scrollToTop()
            }*/
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
    val listsScrollStates = remember { mutableStateMapOf<Int, ScrollState>() }
    val currentPageScrollState = remember(listsScrollStates.size, pagerState.currentPage) {
        listsScrollStates[pagerState.currentPage]
    }

    CollapsingToolbarScaffold(
        modifier = modifier,
        state = toolbarScaffoldState,
        scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed,
        toolbarModifier = Modifier.drawBehind {
            if (currentPageScrollState?.value != 0) {
                drawBottomShadow()
            }
        },
        toolbar = {
            Toolbar(
                toolbarScaffoldState = toolbarScaffoldState,
                isScrollInProgress = currentPageScrollState?.isScrollInProgress ?: false,
                onEvent = onEvent,
            )
        },
    ) {
        Spacer(modifier = Modifier)
        HorizontalPager(
            pageCount = uiState.notes.count(),
            state = pagerState,
        ) { index ->
            val scrollState = rememberScrollState()
            listsScrollStates[index] = scrollState
            NoteItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(all = 8.dp)
                    .clickable(
                        onClick = {},
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null

                    ),
                note = uiState.notes[index],
            )
        }
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier,
) {

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
                initialPageIndex = 0,
                notes = generatePreviewNotes(),
            ),
            onEvent = {},
        )
    }
}