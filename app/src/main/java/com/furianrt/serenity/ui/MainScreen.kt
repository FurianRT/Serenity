package com.furianrt.serenity.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.notecontent.composables.NoteItem
import com.furianrt.notecontent.entities.UiNote
import com.furianrt.notecontent.utils.generatePreviewNotes
import com.furianrt.serenity.ui.MainScrollState.ScrollDirection
import com.furianrt.serenity.ui.composables.BottomNavigationBar
import com.furianrt.serenity.ui.composables.Toolbar
import com.furianrt.uikit.extensions.addSerenityBackground
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.theme.SerenityTheme
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy

private const val SHOW_SCROLL_TO_TOP_MIN_ITEM_INDEX = 3

@Composable
fun MainScreen(
    screenState: MainScreenState = rememberMainState(),
) {
    val viewModel: MainViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MainEffect.ScrollToTop -> screenState.scrollToTop()
            }
        }
    }

    MainScreenContent(
        modifier = Modifier
            .fillMaxSize()
            .addSerenityBackground()
            .systemBarsPadding()
            .clipToBounds(),
        uiState = uiState,
        screenState = screenState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun MainScreenContent(
    uiState: MainUiState,
    onEvent: (event: MainEvent) -> Unit,
    modifier: Modifier = Modifier,
    screenState: MainScreenState = rememberMainState(),
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter,
    ) {
        CollapsingToolbarScaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(screenState.scrollConnection),
            state = screenState.toolbarState,
            scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed,
            toolbarModifier = Modifier.drawBehind {
                if (screenState.listState.firstVisibleItemScrollOffset > 0) {
                    drawBottomShadow()
                }
            },
            toolbar = {
                Toolbar(
                    toolbarScaffoldState = screenState.toolbarState,
                    listState = screenState.listState,
                    onSettingsClick = { onEvent(MainEvent.OnSettingsClick) },
                    onSearchClick = { onEvent(MainEvent.OnSearchClick) },
                )
            },
        ) {
            Spacer(modifier = Modifier)
            when (uiState) {
                is MainUiState.Loading -> MainLoading()
                is MainUiState.Empty -> MainEmpty()
                is MainUiState.Success -> MainSuccess(uiState.notes, screenState.listState)
            }
        }

        BottomNavigationBar(
            onScrollToTopClick = { onEvent(MainEvent.OnScrollToTopClick) },
            needToHideNavigation = {
                uiState.hasNotes && screenState.scrollState.scrollDirection == ScrollDirection.DOWN
            },
            needToShowScrollUpButton = {
                screenState.listState.firstVisibleItemIndex > SHOW_SCROLL_TO_TOP_MIN_ITEM_INDEX
            },
            onAddNoteClick = { onEvent(MainEvent.OnAddNoteClick) },
        )
    }
}

@Composable
private fun MainSuccess(
    notes: List<UiNote>,
    listState: LazyListState,
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(count = notes.count(), key = { notes[it].id }) { index ->
            NoteItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .clickable { },
                note = notes[index],
            )
        }
    }
}

@Preview
@Composable
private fun MainScreenSuccessPreview() {
    SerenityTheme {
        MainScreenContent(
            uiState = MainUiState.Success(generatePreviewNotes()),
            onEvent = {},
        )
    }
}

@Composable
private fun MainEmpty() {
}

@Composable
private fun MainLoading() {
}
