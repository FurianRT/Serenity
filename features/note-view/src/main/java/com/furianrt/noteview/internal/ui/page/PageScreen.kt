package com.furianrt.noteview.internal.ui.page

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.furianrt.notecontent.composables.NoteContentMedia
import com.furianrt.notecontent.composables.NoteContentTitle
import com.furianrt.notecontent.composables.NoteTags
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.uikit.theme.SerenityTheme
import dagger.hilt.android.EntryPointAccessors
import kotlinx.collections.immutable.persistentListOf
import com.furianrt.uikit.R as uiR

@Composable
private fun pageViewModel(noteId: String): PageViewModel = viewModel(
    key = noteId,
    factory = PageViewModel.provideFactory(
        noteId = noteId,
        factory = EntryPointAccessors.fromActivity<PageViewModel.FactoryProvider>(
            activity = LocalContext.current as Activity,
        ).provide(),
    ),
)

@Composable
internal fun PageScreen(
    noteId: String,
    isInEditMode: Boolean,
    lazyListState: LazyListState,
    onTitleClick: () -> Unit,
    modifier: Modifier = Modifier,
    screenState: PageScreenState = rememberPageScreenState(),
) {
    val viewModel = pageViewModel(noteId = noteId)
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PageEffect.FocusTitle -> screenState.focusTitle(effect.index)
            }
        }
    }

    LaunchedEffect(isInEditMode) {
        viewModel.onEvent(PageEvent.OnEditModeStateChange(isInEditMode))
    }

    PageScreenContent(
        modifier = modifier,
        uiState = uiState,
        screenState = screenState,
        onEvent = viewModel::onEvent,
        onTitleClick = onTitleClick,
        lazyListState = lazyListState,
    )
}

@Composable
private fun PageScreenContent(
    uiState: PageUiState,
    screenState: PageScreenState,
    lazyListState: LazyListState,
    onEvent: (event: PageEvent) -> Unit,
    onTitleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is PageUiState.Success ->
            SuccessScreen(uiState, lazyListState, onEvent, onTitleClick, modifier, screenState)

        is PageUiState.Loading -> LoadingScreen(modifier)
        is PageUiState.Empty -> EmptyScreen(modifier)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun SuccessScreen(
    uiState: PageUiState.Success,
    lazyListState: LazyListState,
    onEvent: (event: PageEvent) -> Unit,
    onTitleClick: () -> Unit,
    modifier: Modifier = Modifier,
    screenState: PageScreenState = PageScreenState(),
) {
    val focusRequesters = remember { mutableStateMapOf<Int, FocusRequester>() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(screenState.focusedTitleIndex) {
        if (uiState.isInEditMode && screenState.focusedTitleIndex != null) {
            focusRequesters[screenState.focusedTitleIndex]?.requestFocus()
            return@LaunchedEffect
        }
        if (!uiState.isInEditMode || screenState.focusedTitleIndex == null) {
            keyboardController?.hide()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.tertiary),
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            count = uiState.content.count(),
            key = { uiState.content[it].id },
            contentType = { uiState.content[it].javaClass.name },
        ) { index ->
            when (val content = uiState.content[index]) {
                is UiNoteContent.Title -> {
                    val focusRequester = remember { FocusRequester() }
                    focusRequesters[index] = focusRequester
                    NoteContentTitle(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = if (index == 0) 8.dp else 0.dp)
                            .padding(horizontal = 8.dp)
                            .focusRequester(focusRequester)
                            .animateItemPlacement(),
                        title = content,
                        hint = if (index == 0) {
                            stringResource(id = uiR.string.note_title_hint_text)
                        } else {
                            stringResource(id = uiR.string.note_title_hint_write_more_here)
                        },
                        isEditable = uiState.isInEditMode,
                        onTitleChange = { onEvent(PageEvent.OnTitleTextChange(content.id, it)) },
                        onTitleClick = { titleId ->
                            onTitleClick()
                            onEvent(PageEvent.OnTitleClick(titleId))
                        },
                    )
                }

                is UiNoteContent.MediaBlock -> {
                    NoteContentMedia(
                        modifier = Modifier.animateItemPlacement(),
                        media = content.images,
                        isEditable = uiState.isInEditMode,
                    )
                }
            }
        }
        item(key = "tags") {
            NoteTags(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .padding(horizontal = 4.dp)
                    .animateItemPlacement(),
                tags = uiState.tags,
                isEditable = uiState.isInEditMode,
                onTagClick = { onEvent(PageEvent.OnTagClick(it)) },
                onTagRemoveClick = { onEvent(PageEvent.OnTagRemoved(it)) },
            )
        }
    }
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier)
}

@Composable
private fun EmptyScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier)
}

@Preview
@Composable
private fun SuccessScreenPreview() {
    SerenityTheme {
        SuccessScreen(
            uiState = PageUiState.Success(
                isInEditMode = false,
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
            onEvent = {},
            onTitleClick = {},
            lazyListState = rememberLazyListState(),
        )
    }
}
