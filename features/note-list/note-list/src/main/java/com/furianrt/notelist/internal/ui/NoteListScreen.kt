package com.furianrt.notelist.internal.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.core.buildImmutableList
import com.furianrt.notelist.internal.ui.composables.BottomNavigationBar
import com.furianrt.notelist.internal.ui.composables.Toolbar
import com.furianrt.notelist.internal.ui.entities.NoteListScreenNote
import com.furianrt.notelistui.composables.NoteListItem
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.uikit.components.ConfirmNotesDeleteDialog
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.DialogIdentifier
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import com.furianrt.uikit.R as uiR

private const val SHOW_SCROLL_TO_TOP_MIN_ITEM_INDEX = 3

@Composable
internal fun NoteListScreen(
    openNoteViewScreen: (noteId: String, identifier: DialogIdentifier) -> Unit,
    openNoteCreateScreen: (identifier: DialogIdentifier) -> Unit,
    openNoteSearchScreen: () -> Unit,
    openSettingsScreen: () -> Unit,
) {
    val viewModel: NoteListViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val hazeState = remember { HazeState() }

    val screenState = rememberMainState()

    var showDeleteConfirmDialogState: Int? by remember { mutableStateOf(null) }

    val openNoteViewScreenState by rememberUpdatedState(openNoteViewScreen)
    val openNoteCreateScreenState by rememberUpdatedState(openNoteCreateScreen)
    val openNoteSearchScreenState by rememberUpdatedState(openNoteSearchScreen)
    val openSettingsScreenState by rememberUpdatedState(openSettingsScreen)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is NoteListEffect.ScrollToTop -> screenState.scrollToTop()
                    is NoteListEffect.OpenSettingsScreen -> openSettingsScreenState()
                    is NoteListEffect.OpenNoteSearchScreen -> openNoteSearchScreenState()
                    is NoteListEffect.OpenNoteViewScreen -> {
                        openNoteViewScreenState(effect.noteId, effect.identifier)
                    }

                    is NoteListEffect.OpenNoteCreateScreen -> {
                        openNoteCreateScreenState(effect.identifier)
                    }

                    is NoteListEffect.ShowConfirmNoteDeleteDialog -> {
                        showDeleteConfirmDialogState = effect.notesCount
                    }
                }
            }
    }

    MainScreenContent(
        modifier = Modifier.haze(hazeState),
        uiState = uiState,
        screenState = screenState,
        onEvent = viewModel::onEvent,
    )

    showDeleteConfirmDialogState?.let { notesCount ->
        ConfirmNotesDeleteDialog(
            notesCount = notesCount,
            hazeState = hazeState,
            onConfirmClick = { viewModel.onEvent(NoteListEvent.OnConfirmDeleteSelectedNotesClick) },
            onDismissRequest = { showDeleteConfirmDialogState = null },
        )
    }
}

@Composable
private fun MainScreenContent(
    uiState: NoteListUiState,
    onEvent: (event: NoteListEvent) -> Unit,
    modifier: Modifier = Modifier,
    screenState: NoteListScreenState = rememberMainState(),
) {
    val needToShowScrollUpButton by remember {
        derivedStateOf {
            screenState.listState.firstVisibleItemIndex > SHOW_SCROLL_TO_TOP_MIN_ITEM_INDEX
        }
    }

    val successState = uiState as? NoteListUiState.Success

    LaunchedEffect(uiState.enableSelection) {
        if (uiState.enableSelection) {
            screenState.toolbarState.expand()
        }
    }

    BackHandler(enabled = uiState.enableSelection) {
        onEvent(NoteListEvent.OnCloseSelectionClick)
    }

    MovableToolbarScaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        listState = screenState.listState
            .takeIf { uiState is NoteListUiState.Success } ?: rememberLazyListState(),
        state = screenState.toolbarState,
        enabled = !uiState.enableSelection,
        toolbar = {
            Toolbar(
                notesCount = successState?.notes?.count() ?: 0,
                selectedNotesCount = successState?.selectedNotesCount ?: 0,
                onSettingsClick = { onEvent(NoteListEvent.OnSettingsClick) },
                onSearchClick = { onEvent(NoteListEvent.OnSearchClick) },
                onDeleteClick = { onEvent(NoteListEvent.OnDeleteSelectedNotesClick) },
                onCloseSelectionClick = { onEvent(NoteListEvent.OnCloseSelectionClick) },
            )
        },
    ) {
        when (uiState) {
            is NoteListUiState.Loading -> NoteListLoading()
            is NoteListUiState.Empty -> NoteListEmpty()
            is NoteListUiState.Success -> MainSuccess(
                uiState = uiState,
                screenState = screenState,
                onEvent = onEvent,
            )
        }

        BottomNavigationBar(
            modifier = Modifier.align(Alignment.BottomEnd),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
            onScrollToTopClick = { onEvent(NoteListEvent.OnScrollToTopClick) },
            needToHideNavigation = {
                uiState.hasNotes && screenState.listState.lastScrolledForward
            },
            needToShowScrollUpButton = { uiState.hasNotes && needToShowScrollUpButton },
            onAddNoteClick = { onEvent(NoteListEvent.OnAddNoteClick) },
        )
    }
}

@Composable
private fun MainSuccess(
    uiState: NoteListUiState.Success,
    screenState: NoteListScreenState,
    onEvent: (event: NoteListEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    LaunchedEffect(uiState.scrollToPosition) {
        if (uiState.scrollToPosition != null) {
            screenState.scrollToPosition(
                position = uiState.scrollToPosition,
                toolbarHeight = density.run { ToolbarConstants.bigToolbarHeight.toPx().toInt() },
            )
            onEvent(NoteListEvent.OnScrolledToItem)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = screenState.listState,
        contentPadding = PaddingValues(
            start = 8.dp,
            end = 8.dp,
            top = ToolbarConstants.bigToolbarHeight + 8.dp +
                    WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(count = uiState.notes.count(), key = { uiState.notes[it].id }) { index ->
            val note = uiState.notes[index]
            NoteListItem(
                modifier = Modifier
                    .animateItem()
                    .animateContentSize(),
                content = note.content,
                tags = note.tags,
                date = note.date.getTitle(),
                fontColor = note.fontColor,
                fontFamily = note.fontFamily,
                fontSize = note.fontSize.sp,
                isPinned = note.isPinned,
                isSelected = note.isSelected,
                onClick = { onEvent(NoteListEvent.OnNoteClick(note)) },
                onLongClick = { onEvent(NoteListEvent.OnNoteLongClick(note)) },
            )
        }
    }
}

@Composable
private fun NoteListEmpty(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize())
}

@Composable
private fun NoteListLoading(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize())
}

@Composable
private fun NoteListScreenNote.Date.getTitle() = when (this) {
    is NoteListScreenNote.Date.Today -> stringResource(uiR.string.today_title)
    is NoteListScreenNote.Date.Yesterday -> stringResource(uiR.string.yesterday_title)
    is NoteListScreenNote.Date.Other -> text
}

@Preview
@Composable
private fun SuccessPreview() {
    SerenityTheme {
        MainScreenContent(
            uiState = NoteListUiState.Success(
                notes = generatePreviewNotes(withSelected = false),
                scrollToPosition = null,
                selectedNotesCount = 0
            ),
            onEvent = {},
        )
    }
}

@Preview
@Composable
private fun SuccessWithSelectedPreview() {
    SerenityTheme {
        MainScreenContent(
            uiState = NoteListUiState.Success(
                notes = generatePreviewNotes(withSelected = true),
                scrollToPosition = null,
                selectedNotesCount = 3
            ),
            onEvent = {},
        )
    }
}

private fun generatePreviewNotes(withSelected: Boolean) = buildImmutableList {
    repeat(5) { index ->
        add(
            NoteListScreenNote(
                id = index.toString(),
                date = NoteListScreenNote.Date.Other("19.06.2023"),
                tags = persistentListOf(),
                fontColor = UiNoteFontColor.WHITE,
                fontFamily = UiNoteFontFamily.QUICK_SAND,
                fontSize = 15,
                isPinned = false,
                isSelected = withSelected && index % 2 == 0,
                content = persistentListOf(
                    UiNoteContent.Title(
                        id = index.toString(),
                        state = NoteTitleState(
                            initialText = AnnotatedString(
                                text = "Kotlin is a modern programming language with a " +
                                        "lot more syntactic sugar compared to Java, and as such " +
                                        "there is equally more black magic",
                            ),
                        ),
                    ),
                ),
            ),
        )
    }
}
