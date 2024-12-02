package com.furianrt.search.internal.ui

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.furianrt.search.internal.ui.composables.SelectedTagsList
import com.furianrt.search.internal.ui.composables.Toolbar
import com.furianrt.search.internal.ui.entities.SearchListItem
import com.furianrt.search.internal.ui.entities.SearchListItem.TagsList
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import java.time.ZonedDateTime

@Composable
internal fun SearchScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->

            }
    }

    ScreenContent(
        uiState = uiState
    )
}

@Composable
private fun ScreenContent(
    uiState: SearchUiState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Toolbar(
                queryState = uiState.searchQuery,
                onBackClick = {},
                onCalendarClick = {},
            )
        },
    ) { paddingValues ->
        when (uiState.state) {
            is SearchUiState.State.Success -> SuccessContent(
                modifier = modifier.padding(paddingValues),
                uiState = uiState.state,
            )

            is SearchUiState.State.Loading -> LoadingContent(
                modifier = modifier.padding(paddingValues),
            )

            is SearchUiState.State.Empty -> EmptyContent(
                modifier = modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun SuccessContent(
    uiState: SearchUiState.State.Success,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        SelectedTagsList(
            tags = uiState.selectedTags,
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues()
        ) {
            items(
                count = uiState.items.count(),
                key = { uiState.items[it].id },
                contentType = { uiState.items[it]::class.simpleName },
            ) { index ->
                when (val item = uiState.items[index]) {
                    is SearchListItem.TagsList -> AllTagsList(
                        modifier = Modifier
                            .animateItem()
                            .animateContentSize(),
                        tags = item.items,
                        maxRowsCount = item.rowsLimit,
                        onTagClick = {},
                        onShowAllClick = {},
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
                    )
                }
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
        val tagsItem = SearchListItem.TagsList(
            items = buildImmutableList {
                repeat(20) { index ->
                    add(
                        SearchListItem.TagsList.Tag(
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
                    selectedTags = persistentListOf(),
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
                state = SearchUiState.State.Success(
                    items = noteItems,
                    selectedTags = persistentListOf(
                        TagsList.Tag("Programming", 2),
                        TagsList.DateRange(ZonedDateTime.now(), ZonedDateTime.now()),
                    ),
                ),
            ),
        )
    }
}
