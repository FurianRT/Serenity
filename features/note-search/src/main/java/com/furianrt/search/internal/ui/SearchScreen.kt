package com.furianrt.search.internal.ui

import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.core.buildImmutableList
import com.furianrt.notelistui.composables.ConfirmNotesDeleteDialog
import com.furianrt.notelistui.composables.NoteListItem
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontFamily
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
import com.furianrt.uikit.components.SnackBar
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.DialogIdentifier
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.ZonedDateTime
import com.furianrt.uikit.R as uiR

@Immutable
private data class CalendarState(
    val start: SelectedDate?,
    val end: SelectedDate?,
    val datesWithNotes: Set<LocalDate>,
)

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

    var calendarState: CalendarState? by remember { mutableStateOf(null) }
    var showDeleteConfirmDialogState: Int? by remember { mutableStateOf(null) }
    val hazeState = remember { HazeState() }
    val snackBarHostState = remember { SnackbarHostState() }

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)
    val openNoteViewScreenState by rememberUpdatedState(openNoteViewScreen)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is SearchEffect.CloseScreen -> onCloseRequestState()
                    is SearchEffect.ShowDateSelector -> {
                        val startDate = effect.start?.let { SelectedDate(it) }
                        val endDate = effect.end?.let { SelectedDate(it) }
                        calendarState = CalendarState(startDate, endDate, effect.datesWithNotes)
                    }

                    is SearchEffect.OpenNoteViewScreen -> openNoteViewScreenState(
                        effect.noteId,
                        effect.identifier,
                        effect.queryData,
                    )

                    is SearchEffect.ShowConfirmNoteDeleteDialog -> {
                        showDeleteConfirmDialogState = effect.notesCount
                    }

                    is SearchEffect.ShowSyncProgressMessage -> {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = effect.message,
                            duration = SnackbarDuration.Short,
                        )
                    }
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
        snackBarHostState = snackBarHostState,
        onEvent = viewModel::onEvent,
        toolbarState = toolbarState,
    )

    calendarState?.let { state ->
        MultiChoiceCalendar(
            startDate = state.start,
            endDate = state.end,
            hazeState = hazeState,
            hasNotes = { state.datesWithNotes.contains(it) },
            onDismissRequest = { calendarState = null },
            onDateSelected = { startDate, endDate ->
                viewModel.onEvent(SearchEvent.OnDateRangeSelected(startDate.date, endDate?.date))
            },
        )
    }

    showDeleteConfirmDialogState?.let { notesCount ->
        ConfirmNotesDeleteDialog(
            notesCount = notesCount,
            hazeState = hazeState,
            onConfirmClick = { viewModel.onEvent(SearchEvent.OnConfirmDeleteSelectedNotesClick) },
            onDismissRequest = { showDeleteConfirmDialogState = null },
        )
    }
}

@Composable
private fun ScreenContent(
    uiState: SearchUiState,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onEvent: (event: SearchEvent) -> Unit = {},
    toolbarState: MovableToolbarState = remember { MovableToolbarState() },
) {
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.enableSelection) {
        if (uiState.enableSelection) {
            toolbarState.expand()
        }
    }

    MovableToolbarScaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        listState = if (uiState.state is SearchUiState.State.Empty) {
            rememberLazyListState()
        } else {
            listState
        },
        state = toolbarState,
        enabled = !uiState.enableSelection,
        toolbar = {
            val successState = uiState.state as? SearchUiState.State.Success
            Toolbar(
                notesCount = successState?.notesCount ?: 0,
                selectedNotesCount = successState?.selectedNotesCount ?: 0,
                selectedFilters = uiState.selectedFilters,
                queryState = uiState.searchQuery,
                onBackClick = { onEvent(SearchEvent.OnButtonBackClick) },
                onCalendarClick = { onEvent(SearchEvent.OnButtonCalendarClick) },
                onClearQueryClick = { onEvent(SearchEvent.OnButtonClearQueryClick) },
                onRemoveFilterClick = { onEvent(SearchEvent.OnRemoveFilterClick(it)) },
                onUnselectedTagClick = { onEvent(SearchEvent.OnTagClick(it.title)) },
                onDateFilterClick = { onEvent(SearchEvent.OnDateFilterClick(it)) },
                onDeleteClick = { onEvent(SearchEvent.OnDeleteSelectedNotesClick) },
                onCloseSelectionClick = { onEvent(SearchEvent.OnCloseSelectionClick) }
            )
        },
    ) { topPadding ->
        AnimatedContent(
            targetState = uiState.state,
            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
            contentKey = { it::class.simpleName },
        ) { targetState ->
            when (targetState) {
                is SearchUiState.State.Success -> SuccessContent(
                    uiState = targetState,
                    onEvent = onEvent,
                    listState = listState,
                    toolbarState = toolbarState,
                    toolbarHeight = topPadding,
                )

                is SearchUiState.State.Empty -> EmptyContent(
                    toolbarHeight = topPadding,
                )
            }
        }
        SnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            hostState = snackBarHostState,
            snackbar = { data ->
                SnackBar(
                    title = data.visuals.message,
                    icon = painterResource(uiR.drawable.ic_cloud_sync),
                    tonalColor = MaterialTheme.colorScheme.tertiaryContainer,
                )
            },
        )
    }
}

@Composable
private fun SuccessContent(
    uiState: SearchUiState.State.Success,
    onEvent: (event: SearchEvent) -> Unit,
    listState: LazyListState,
    toolbarState: MovableToolbarState,
    toolbarHeight: Dp,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    BackHandler(enabled = uiState.enableSelection) {
        onEvent(SearchEvent.OnCloseSelectionClick)
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(uiState.scrollToPosition) {
        if (uiState.scrollToPosition != null) {
            listState.requestScrollToItem( uiState.scrollToPosition)
            toolbarState.expand()
            onEvent(SearchEvent.OnScrolledToItem)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(
            top = toolbarHeight + 8.dp,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 16.dp,
        ),
    ) {
        items(
            count = uiState.items.count(),
            key = { uiState.items[it].id },
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
                    date = when (item.date) {
                        is SearchListItem.Note.Date.Today -> {
                            stringResource(uiR.string.today_title)
                        }

                        is SearchListItem.Note.Date.Yesterday -> {
                            stringResource(uiR.string.yesterday_title)
                        }

                        is SearchListItem.Note.Date.Other -> {
                            item.date.text
                        }
                    },
                    isSelected = item.isSelected,
                    fontColor = item.fontColor,
                    fontFamily = item.fontFamily,
                    fontSize = item.fontSize.sp,
                    onClick = { onEvent(SearchEvent.OnNoteItemClick(item.id)) },
                    onLongClick = { onEvent(SearchEvent.OnNoteLongClick(item.id)) },
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
    toolbarHeight: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(top = toolbarHeight)
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
            snackBarHostState = SnackbarHostState(),
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
                        date = SearchListItem.Note.Date.Other("19.06.2023"),
                        tags = persistentListOf(
                            UiNoteTag.Regular(title = "Programming", isRemovable = false),
                            UiNoteTag.Regular(title = "Android", isRemovable = false),
                        ),
                        isSelected = false,
                        fontColor = null,
                        fontFamily = null,
                        fontSize = 15,
                        content = persistentListOf(
                            UiNoteContent.Title(
                                id = "1",
                                state = NoteTitleState(
                                    fontFamily = UiNoteFontFamily.QuickSand,
                                    initialText = AnnotatedString(
                                        text = "Kotlin is a modern programming language with a " +
                                                "lot more syntactic sugar compared to Java, and as such " +
                                                "there is equally more black magic",
                                    ),
                                ),
                            ),
                            UiNoteContent.MediaBlock(
                                id = "1",
                                media = persistentListOf(
                                    UiNoteContent.MediaBlock.Image(
                                        id = "",
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
            snackBarHostState = SnackbarHostState(),
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
            snackBarHostState = SnackbarHostState(),
        )
    }
}
