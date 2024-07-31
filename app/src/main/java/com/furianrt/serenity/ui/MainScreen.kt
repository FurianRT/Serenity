package com.furianrt.serenity.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.furianrt.core.buildImmutableList
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.serenity.ui.MainScrollState.ScrollDirection
import com.furianrt.serenity.ui.composables.BottomNavigationBar
import com.furianrt.serenity.ui.composables.NoteListItem
import com.furianrt.serenity.ui.composables.Toolbar
import com.furianrt.serenity.ui.entities.MainScreenNote
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy

private const val SHOW_SCROLL_TO_TOP_MIN_ITEM_INDEX = 3

@Composable
internal fun MainScreen(
    navHostController: NavHostController,
    screenState: MainScreenState = rememberMainState(),
) {
    val viewModel: MainViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MainEffect.ScrollToTop -> {
                    screenState.scrollToTop()
                }

                is MainEffect.OpenNoteScreen -> {
                    navHostController.navigate("Note/${effect.noteId}")
                }

                is MainEffect.OpenSettingsScreen -> {
                    navHostController.navigate("Settings")
                }
            }
        }
    }

    MainScreenContent(
        uiState = uiState,
        screenState = screenState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun MainScreenContent(
    uiState: MainUiState,
    onEvent: (event: MainEvent) -> Unit,
    screenState: MainScreenState = rememberMainState(),
) {
    val needToShowScrollUpButton by remember {
        derivedStateOf {
            screenState.listState.firstVisibleItemIndex > SHOW_SCROLL_TO_TOP_MIN_ITEM_INDEX
        }
    }

    val needToHideNavigation by remember(screenState.scrollState.scrollDirection) {
        derivedStateOf {
            uiState.hasNotes && screenState.scrollState.scrollDirection == ScrollDirection.DOWN
        }
    }

    val systemBarsHeight = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

    Surface {
        Box(contentAlignment = Alignment.BottomCenter) {
            CollapsingToolbarScaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(screenState.scrollConnection),
                state = screenState.toolbarState,
                scrollStrategy = ScrollStrategy.EnterAlways,
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
                when (uiState) {
                    is MainUiState.Loading -> MainLoading()
                    is MainUiState.Empty -> MainEmpty()
                    is MainUiState.Success -> MainSuccess(
                        notes = uiState.notes,
                        listState = screenState.listState,
                        onEvent = onEvent,
                    )
                }
            }
            BottomNavigationBar(
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = systemBarsHeight + 24.dp,
                ),
                onScrollToTopClick = { onEvent(MainEvent.OnScrollToTopClick) },
                needToHideNavigation = { needToHideNavigation },
                needToShowScrollUpButton = { needToShowScrollUpButton },
                onAddNoteClick = { onEvent(MainEvent.OnAddNoteClick) },
            )
        }
    }
}

@Composable
private fun MainSuccess(
    notes: ImmutableList<MainScreenNote>,
    listState: LazyListState,
    onEvent: (event: MainEvent) -> Unit,
) {
    val systemBarHeight = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(
            start = 8.dp,
            end = 8.dp,
            top = 8.dp,
            bottom = systemBarHeight + 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(count = notes.count(), key = { notes[it].id }) { index ->
            NoteListItem(
                modifier = Modifier.animateItem(),
                note = notes[index],
                onClick = { onEvent(MainEvent.OnNoteClick(it)) },
                onTagClick = { onEvent(MainEvent.OnNoteTagClick(it)) },
            )
        }
    }
}

@Composable
private fun MainEmpty() {
    Spacer(modifier = Modifier)
}

@Composable
private fun MainLoading() {
    Spacer(modifier = Modifier)
}

@Preview
@Composable
private fun MainScreenSuccessPreview() {
    SerenityTheme {
        MainScreenContent(
            uiState = MainUiState.Success(
                notes = generatePreviewNotes(),
            ),
            onEvent = {},
        )
    }
}

private fun generatePreviewNotes() = buildImmutableList {
    for (i in 0..5) {
        add(
            MainScreenNote(
                id = i.toString(),
                timestamp = 0,
                tags = persistentListOf(),
                content = persistentListOf(
                    UiNoteContent.Title(
                        id = "1",
                        position = 0,
                        text = "Kotlin is a modern programming language with a " +
                                "lot more syntactic sugar compared to Java, and as such " +
                                "there is equally more black magic",
                    ),
                ),
            ),
        )
    }
}
