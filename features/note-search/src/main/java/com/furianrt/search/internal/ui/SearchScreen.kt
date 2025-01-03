package com.furianrt.search.internal.ui

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
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
import com.furianrt.search.api.entities.QueryData
import com.furianrt.search.internal.ui.composables.AllTagsList
import com.furianrt.search.internal.ui.composables.NotesCountItem
import com.furianrt.search.internal.ui.composables.Toolbar
import com.furianrt.search.internal.ui.entities.SearchListItem
import com.furianrt.search.internal.ui.entities.SelectedFilter
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.MovableToolbarState
import com.furianrt.uikit.components.MultiChoiceCalendar
import com.furianrt.uikit.components.SelectedDate
import com.furianrt.uikit.extensions.visibleItemsInfo
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.DialogIdentifier
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.ZonedDateTime

@Composable
internal fun SearchScreen(
    openNoteViewScreen: (noteId: String, identifier: DialogIdentifier, data: QueryData) -> Unit,
    onCloseRequest: () -> Unit,
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val toolbarState = remember { MovableToolbarState() }

    val filtersCount = uiState.selectedFilters.count(SelectedFilter::isSelected)
    var prevFiltersCount by rememberSaveable { mutableIntStateOf(filtersCount) }

    data class CalendarState(val start: SelectedDate?, val end: SelectedDate?)

    var calendarState: CalendarState? by remember { mutableStateOf(null) }
    val hazeState = remember { HazeState() }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is SearchEffect.CloseScreen -> onCloseRequest()
                    is SearchEffect.ShowDateSelector -> {
                        val startDate = effect.start?.let { SelectedDate(it) }
                        val endDate = effect.end?.let { SelectedDate(it) }
                        calendarState = CalendarState(startDate, endDate)
                    }

                    is SearchEffect.OpenNoteViewScreen -> openNoteViewScreen(
                        effect.noteId,
                        effect.identifier,
                        effect.queryData,
                    )
                }
            }
    }

    LaunchedEffect(filtersCount) {
        if (filtersCount != prevFiltersCount) {
            toolbarState.expand()
            prevFiltersCount = filtersCount
        }
    }

    ScreenContent(
        modifier = Modifier.haze(hazeState),
        uiState = uiState,
        onEvent = viewModel::onEvent,
        toolbarState = toolbarState,
    )

    calendarState?.let { state ->
        MultiChoiceCalendar(
            startDate = state.start,
            endDate = state.end,
            hazeState = hazeState,
            onDismissRequest = { calendarState = null },
            onDateSelected = { startDate, endDate ->
                viewModel.onEvent(SearchEvent.OnDateRangeSelected(startDate.date, endDate?.date))
            },
        )
    }
}

@Composable
private fun ScreenContent(
    uiState: SearchUiState,
    modifier: Modifier = Modifier,
    onEvent: (event: SearchEvent) -> Unit = {},
    toolbarState: MovableToolbarState = remember { MovableToolbarState() },
) {
    var toolbarHeight by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    MovableToolbarScaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        listState = if (uiState.state is SearchUiState.State.Empty) {
            rememberLazyListState()
        } else {
            listState
        },
        state = toolbarState,
        toolbar = {
            Toolbar(
                modifier = Modifier.onSizeChanged { toolbarHeight = it.height },
                selectedFilters = uiState.selectedFilters,
                queryState = uiState.searchQuery,
                onBackClick = { onEvent(SearchEvent.OnButtonBackClick) },
                onCalendarClick = { onEvent(SearchEvent.OnButtonCalendarClick) },
                onClearQueryClick = { onEvent(SearchEvent.OnButtonClearQueryClick) },
                onRemoveFilterClick = { onEvent(SearchEvent.OnRemoveFilterClick(it)) },
                onUnselectedTagClick = { onEvent(SearchEvent.OnTagClick(it.title)) },
                onDateFilterClick = { onEvent(SearchEvent.OnDateFilterClick(it)) },
            )
        },
    ) {
        AnimatedContent(
            targetState = uiState.state,
            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
            contentKey = { it::class.simpleName },
            label = "ContentAnim",
        ) { targetState ->
            when (targetState) {
                is SearchUiState.State.Success -> SuccessContent(
                    uiState = targetState,
                    onEvent = onEvent,
                    listState = listState,
                    toolbarState = toolbarState,
                    toolbarHeight = toolbarHeight,
                )

                is SearchUiState.State.Empty -> EmptyContent(
                    toolbarHeight = toolbarHeight,
                )
            }
        }
    }
}

@Composable
private fun SuccessContent(
    uiState: SearchUiState.State.Success,
    onEvent: (event: SearchEvent) -> Unit,
    listState: LazyListState,
    toolbarState: MovableToolbarState,
    toolbarHeight: Int,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(uiState.scrollToPosition) {
        if (uiState.scrollToPosition != null) {
            val visibleIndexes = listState.layoutInfo
                .visibleItemsInfo(itemVisiblePercentThreshold = 90f)
                .map(LazyListItemInfo::index)
            if (uiState.scrollToPosition !in visibleIndexes) {
                if (uiState.scrollToPosition == 1) {
                    listState.scrollToItem(0)
                } else {
                    listState.scrollToItem(uiState.scrollToPosition)
                }

            }
            toolbarState.expand(0)
            onEvent(SearchEvent.OnScrolledToItem)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(
            top = LocalDensity.current.run { toolbarHeight.toDp() } + 8.dp,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 16.dp,
        ),
    ) {
        items(
            count = uiState.items.count(),
            key = { index ->
                when (uiState.items[index]) {
                    is SearchListItem.TagsList -> SearchListItem.TagsList.ID
                    is SearchListItem.NotesCountTitle -> SearchListItem.NotesCountTitle.ID
                    is SearchListItem.Note -> index
                }
            },
            contentType = { uiState.items[it]::class.simpleName },
        ) { index ->
            when (val item = uiState.items[index]) {
                is SearchListItem.TagsList -> AllTagsList(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .animateItem()
                        .animateContentSize(),
                    tags = item.tags,
                    onTagClick = { onEvent(SearchEvent.OnTagClick(it.title)) },
                )

                is SearchListItem.Note -> NoteListItem(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .animateItem(placementSpec = null)
                        .animateContentSize(),
                    content = item.content,
                    tags = item.tags,
                    date = item.date,
                    onClick = { onEvent(SearchEvent.OnNoteItemClick(item.id)) },
                    onLongClick = {},
                    onTagClick = { onEvent(SearchEvent.OnTagClick(it.title)) },
                )

                is SearchListItem.NotesCountTitle -> NotesCountItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    count = item.count,
                )
            }
        }
    }
}

@Composable
private fun EmptyContent(
    toolbarHeight: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(top = LocalDensity.current.run { toolbarHeight.toDp() })
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Text(
            modifier = Modifier.padding(top = 100.dp),
            text = "No results found",
            style = MaterialTheme.typography.titleMedium,
        )
    }
}


@Composable
@Preview
private fun SuccessEmptyQueryPreview() {
    SerenityTheme {
        val tagsItem = SearchListItem.TagsList(
            tags = buildImmutableList {
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
            add(SearchListItem.NotesCountTitle(count = 5))
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

@Composable
@Preview
private fun EmptyStatePreview() {
    SerenityTheme {
        ScreenContent(
            uiState = SearchUiState(
                searchQuery = TextFieldState("Test query"),
                selectedFilters = persistentListOf(
                    SelectedFilter.Tag("Programming"),
                    SelectedFilter.DateRange(LocalDate.now(), LocalDate.now()),
                    SelectedFilter.Tag("Kotlin"),
                    SelectedFilter.Tag("Article"),
                ),
                state = SearchUiState.State.Empty,
            ),
        )
    }
}
