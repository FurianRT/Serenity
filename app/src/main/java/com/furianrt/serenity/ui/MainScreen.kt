package com.furianrt.serenity.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.serenity.ui.composables.BottomNavigationBar
import com.furianrt.serenity.ui.composables.Toolbar
import com.furianrt.uikit.composables.NoteItem
import com.furianrt.uikit.entities.UiNote
import com.furianrt.uikit.extensions.addSerenityBackground
import com.furianrt.uikit.theme.SerenityTheme
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy

private const val SHOW_SCROLL_TO_TOP_MIN_ITEM_INDEX = 5

@Composable
fun MainScreen(
    uiState: MainUiState = rememberHomeState(),
) {
    val viewModel: MainViewModel = hiltViewModel()
    val vmState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MainEffect.ScrollToTop -> uiState.scrollToTop()
            }
        }
    }

    MainScreenContent(
        vmState = vmState,
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun MainScreenContent(
    vmState: MainState,
    uiState: MainUiState = rememberHomeState(),
    onEvent: (event: MainEvent) -> Unit = {},
) {
    val scrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val scrollSlippage = 3f
                when {
                    available.y > scrollSlippage -> {
                        uiState.scrollState.scrollDirectionState.value =
                            HomeScrollState.ScrollDirection.UP
                    }

                    available.y < -scrollSlippage -> {
                        uiState.scrollState.scrollDirectionState.value =
                            HomeScrollState.ScrollDirection.DOWN
                    }
                }
                uiState.scrollState.firstVisibleIndexState.value =
                    uiState.listState.firstVisibleItemIndex
                return super.onPreScroll(available, source)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .addSerenityBackground()
            .systemBarsPadding()
            .clipToBounds(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        CollapsingToolbarScaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollConnection),
            state = uiState.toolbarState,
            scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed,
            toolbarModifier = Modifier.drawBehind {
                if (uiState.listState.firstVisibleItemScrollOffset > 0f) {
                    drawToolbarShadow()
                }
            },
            toolbar = { Toolbar(uiState.toolbarState, uiState.listState) },
        ) {
            Spacer(modifier = Modifier)
            when (vmState) {
                is MainState.Loading -> MainLoading()
                is MainState.Empty -> MainEmpty()
                is MainState.Success -> MainSuccess(vmState.notes, uiState.listState)
            }
        }

        val needToHideNavigation by remember {
            derivedStateOf {
                uiState.scrollState.firstVisibleIndex >= 0 &&
                    uiState.scrollState.scrollDirection == HomeScrollState.ScrollDirection.DOWN
            }
        }

        val needToShowScrollUpButton by remember {
            derivedStateOf {
                uiState.scrollState.firstVisibleIndex > SHOW_SCROLL_TO_TOP_MIN_ITEM_INDEX
            }
        }

        BottomNavigationBar(
            modifier = Modifier.padding(bottom = 24.dp),
            onScrollToTopClick = { onEvent(MainEvent.OnScrollToTopClick) },
            needToHideNavigation = { needToHideNavigation },
            needToShowScrollUpButton = { needToShowScrollUpButton },
        )
    }
}

private fun DrawScope.drawToolbarShadow(
    color: Color = Color.Black,
    elevation: Dp = 6.dp,
) = drawRect(
    topLeft = Offset(0f, size.height),
    size = Size(size.width, elevation.toPx()),
    brush = Brush.verticalGradient(
        colors = listOf(color.copy(alpha = 0.2f), Color.Transparent),
        startY = size.height,
        endY = size.height + elevation.toPx(),
    ),
)

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
            NoteItem(notes[index])
        }
    }
}

@Preview
@Composable
private fun GreetingPreview() {
    SerenityTheme {
        MainScreenContent(
            vmState = MainState.Success(generatePreviewNotes()),
        )
    }
}

@Composable
private fun MainEmpty() {
}

@Composable
private fun MainLoading() {
}

fun generatePreviewNotes() = buildList {
    val title = "Kotlin is a modern programming language with a " +
        "lot more syntactic sugar compared to Java, and as such " +
        "there is equally more black magic"
    for (i in 0..2) {
        add(
            UiNote(
                id = i.toString(),
                time = 0,
                title = title,
            ),
        )
    }
}
