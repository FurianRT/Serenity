package com.furianrt.noteview.internal.ui.page

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
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
    modifier: Modifier = Modifier,
) {
    val viewModel = pageViewModel(noteId = noteId)
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(isInEditMode) {
        viewModel.onEvent(PageEvent.OnEditModeStateChange(isInEditMode))
    }

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
    lazyListState: LazyListState,
    onEvent: (event: PageEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is PageUiState.Success -> SuccessScreen(uiState, lazyListState, onEvent, modifier)
        is PageUiState.Loading -> LoadingScreen(modifier)
    }
}

@Composable
private fun SuccessScreen(
    uiState: PageUiState.Success,
    lazyListState: LazyListState,
    onEvent: (event: PageEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = MaterialTheme.colorScheme.tertiary),
            state = lazyListState,
            contentPadding = PaddingValues(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                count = uiState.content.count(),
                key = { uiState.content[it].id },
                contentType = { uiState.content[it].javaClass.name },
            ) { index ->
                when (val content = uiState.content[index]) {
                    is UiNoteContent.Title -> {
                        NoteContentTitle(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            title = content,
                            isEditable = uiState is PageUiState.Success.Edit,
                        )
                    }

                    is UiNoteContent.MediaBlock -> {
                        NoteContentMedia(
                            modifier = Modifier,
                            media = content.images,
                            isEditable = uiState is PageUiState.Success.Edit,
                        )
                    }
                }
            }
            item(key = "tags") {
                NoteTags(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .padding(top = 4.dp),
                    tags = uiState.tags,
                    isEditable = uiState is PageUiState.Success.Edit,
                    onTagClick = { onEvent(PageEvent.OnTagClick(it)) },
                    onTagRemoveClick = { onEvent(PageEvent.OnTagRemoved(it)) },
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier)
}

@Preview
@Composable
private fun PageScreenContentPreview() {
    SerenityTheme {
        PageScreenContent(
            uiState = PageUiState.Success.View(
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
            lazyListState = rememberLazyListState(),
        )
    }
}
