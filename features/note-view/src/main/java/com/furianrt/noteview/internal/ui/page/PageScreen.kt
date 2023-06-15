package com.furianrt.noteview.internal.ui.page

import android.app.Activity
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.furianrt.uikit.theme.SerenityTheme
import dagger.hilt.android.EntryPointAccessors

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
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val viewModel = pageViewModel(noteId = noteId)
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    PageScreenContent(
        modifier = modifier,
        uiState = uiState,
        onEvent = viewModel::onEvent,
        lazyListState = lazyListState,
    )
}

@Composable
private fun PageScreenContent(
    uiState: PageUiState,
    onEvent: (event: PageEvent) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    /*LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = lazyListState,
    ) {
        items(count = notes.count(), key = { notes[it].id }) { index ->
            NoteListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                note = notes[index],
                onClick = { onEvent(MainEvent.OnNoteClick(it)) },
                onTagClick = { onEvent(MainEvent.OnNoteTagClick(it)) },
            )
        }
        item(key = note.id) {
            NoteTags(
                tags = note.tags,
                date = "Sat 9:12 PM",
                onTagClick = { },
            )
        }
    }*/
}

@Preview
@Composable
private fun PageScreenContentPreview() {
    SerenityTheme {
        PageScreenContent(
            uiState = PageUiState.Success,
            onEvent = {},
        )
    }
}
