package com.furianrt.notelist.internal.ui

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.furianrt.uikit.R as uiR
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelist.internal.ui.composables.BottomNavigationBar
import com.furianrt.notelist.internal.ui.composables.Toolbar
import com.furianrt.notelist.internal.ui.entities.NoteListScreenNote
import com.furianrt.notelistui.composables.NoteListItem
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.DialogIdentifier
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest

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

    val screenState = rememberMainState()

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is NoteListEffect.ScrollToTop -> screenState.scrollToTop()
                    is NoteListEffect.OpenSettingsScreen -> openSettingsScreen()
                    is NoteListEffect.OpenNoteSearchScreen -> openNoteSearchScreen()
                    is NoteListEffect.OpenNoteViewScreen -> {
                        openNoteViewScreen(effect.noteId, effect.identifier)
                    }

                    is NoteListEffect.OpenNoteCreateScreen -> {
                        openNoteCreateScreen(effect.identifier)
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
    uiState: NoteListUiState,
    onEvent: (event: NoteListEvent) -> Unit,
    screenState: NoteListScreenState = rememberMainState(),
) {
    val needToShowScrollUpButton by remember {
        derivedStateOf {
            screenState.listState.firstVisibleItemIndex > SHOW_SCROLL_TO_TOP_MIN_ITEM_INDEX
        }
    }

    MovableToolbarScaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        listState = screenState.listState,
        state = screenState.toolbarState,
        toolbar = {
            Toolbar(
                onSettingsClick = { onEvent(NoteListEvent.OnSettingsClick) },
                onSearchClick = { onEvent(NoteListEvent.OnSearchClick) },
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
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                bottom = 24.dp,
            ),
            onScrollToTopClick = { onEvent(NoteListEvent.OnScrollToTopClick) },
            needToHideNavigation = {
                uiState.hasNotes && screenState.listState.lastScrolledForward
            },
            needToShowScrollUpButton = { needToShowScrollUpButton },
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
                date = when (note.date) {
                    is NoteListScreenNote.Date.Today -> {
                        stringResource(uiR.string.today_title)
                    }

                    is NoteListScreenNote.Date.Yesterday -> {
                        stringResource(uiR.string.yesterday_title)
                    }

                    is NoteListScreenNote.Date.Other -> {
                        note.date.text
                    }
                },
                fontColor = note.fontColor,
                fontFamily = note.fontFamily,
                fontSize = note.fontSize.sp,
                showBorder = note.isPinned,
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

@Preview
@Composable
private fun SuccessPreview() {
    SerenityTheme {
        MainScreenContent(
            uiState = NoteListUiState.Success(
                notes = generatePreviewNotes(),
                scrollToPosition = null,
            ),
            onEvent = {},
        )
    }
}

private fun generatePreviewNotes() = buildImmutableList {
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
