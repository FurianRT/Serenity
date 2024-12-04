package com.furianrt.search.internal.ui

import android.net.Uri
import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.core.buildImmutableList
import com.furianrt.notelistui.composables.NoteListItem
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.search.internal.ui.composables.AllTagsList
import com.furianrt.search.internal.ui.composables.Toolbar
import com.furianrt.search.internal.ui.entities.SearchListItem
import com.furianrt.search.internal.ui.entities.SelectedFilter
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.MovableToolbarState
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.ZonedDateTime

@Composable
internal fun SearchScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val listState = rememberLazyListState()
    val toolbarState = remember { MovableToolbarState() }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is SearchEffect.CloseScreen -> onCloseRequest()
                }
            }
    }

    ScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        listState = listState,
        toolbarState = toolbarState,
    )
}

@Composable
private fun ScreenContent(
    uiState: SearchUiState,
    modifier: Modifier = Modifier,
    onEvent: (event: SearchEvent) -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
    toolbarState: MovableToolbarState = remember { MovableToolbarState() },
) {
    val view = LocalView.current
    var toolbarHeight by remember { mutableIntStateOf(0) }
    MovableToolbarScaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        listState = listState,
        state = toolbarState,
        toolbar = {
            Toolbar(
                modifier = Modifier.onSizeChanged { toolbarHeight = it.height },
                selectedFilters = uiState.selectedFilters,
                queryState = uiState.searchQuery,
                onBackClick = { onEvent(SearchEvent.OnButtonBackClick) },
                onCalendarClick = { onEvent(SearchEvent.OnButtonCalendarClick) },
                onClearQueryClick = { onEvent(SearchEvent.OnButtonClearQueryClick) },
                onRemoveFilterClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    onEvent(SearchEvent.OnRemoveFilterClick(it))
                },
            )
        },
    ) {
        when (uiState.state) {
            is SearchUiState.State.Success -> SuccessContent(
                uiState = uiState.state,
                onEvent = onEvent,
                listState = listState,
                toolbarHeight = toolbarHeight,
            )

            is SearchUiState.State.Loading -> LoadingContent()
            is SearchUiState.State.Empty -> EmptyContent()
        }
    }
}

@Composable
private fun SuccessContent(
    uiState: SearchUiState.State.Success,
    onEvent: (event: SearchEvent) -> Unit,
    listState: LazyListState,
    toolbarHeight: Int,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(
            start = 8.dp,
            end = 8.dp,
            top = LocalDensity.current.run { toolbarHeight.toDp() } + 8.dp,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 16.dp,
        ),
    ) {
        items(
            count = uiState.items.count(),
            key = { uiState.items[it].id },
            contentType = { uiState.items[it]::class.simpleName },
        ) { index ->
            when (val item = uiState.items[index]) {
                is SearchListItem.FiltersList -> AllTagsList(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .animateItem()
                        .animateContentSize(),
                    tags = item.items,
                    onTagClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        onEvent(SearchEvent.OnTagClick(it.id))
                    },
                )

                is SearchListItem.Note -> NoteListItem(
                    modifier = Modifier
                        .animateItem()
                        .animateContentSize(),
                    content = item.content,
                    tags = item.tags,
                    date = item.date,
                    onClick = {},
                    onLongClick = {},
                    onTagClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        onEvent(SearchEvent.OnTagClick(it.title))
                    },
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize())
}

@Composable
private fun EmptyContent(
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize())
}


@Composable
@Preview
private fun SuccessEmptyQueryPreview() {
    SerenityTheme {
        val tagsItem = SearchListItem.FiltersList(
            items = buildImmutableList {
                repeat(20) { index ->
                    add(
                        SearchListItem.FiltersList.Filter.Tag(
                            title = "Title $index",
                            count = index + 1,
                        ),
                    )
                }
            },
        )
        ScreenContent(
            uiState = SearchUiState(
                state = SearchUiState.State.Success(
                    items = persistentListOf(tagsItem),
                ),
            ),
        )
    }
}

@Composable
@Preview
private fun SuccessFilledQueryPreview() {
    SerenityTheme {
        val noteItems = buildImmutableList {
            repeat(5) { index ->
                add(
                    SearchListItem.Note(
                        id = index.toString(),
                        date = "19.06.2023",
                        tags = persistentListOf(
                            UiNoteTag.Regular(title = "Programming", isRemovable = false),
                            UiNoteTag.Regular(title = "Android", isRemovable = false),
                        ),
                        content = persistentListOf(
                            UiNoteContent.Title(
                                id = "1",
                                state = TextFieldState(
                                    initialText = "Kotlin is a modern programming language with a " +
                                            "lot more syntactic sugar compared to Java, and as such " +
                                            "there is equally more black magic",
                                ),
                            ),
                            UiNoteContent.MediaBlock(
                                id = "1",
                                media = persistentListOf(
                                    UiNoteContent.MediaBlock.Image(
                                        name = "",
                                        addedDate = ZonedDateTime.now(),
                                        ratio = 1.5f,
                                        uri = Uri.EMPTY,
                                    )
                                ),
                            ),
                        ),
                    ),
                )
            }
        }
        ScreenContent(
            uiState = SearchUiState(
                searchQuery = TextFieldState("Test query"),
                selectedFilters = persistentListOf(
                    SelectedFilter.Tag("Programming"),
                    SelectedFilter.DateRange(LocalDate.now(), LocalDate.now()),
                    SelectedFilter.Tag("Kotlin"),
                    SelectedFilter.Tag("Article"),
                ),
                state = SearchUiState.State.Success(
                    items = noteItems,
                ),
            ),
        )
    }
}
