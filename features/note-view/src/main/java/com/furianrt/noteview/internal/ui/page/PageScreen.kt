package com.furianrt.noteview.internal.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.notecontent.composables.NoteContentMedia
import com.furianrt.notecontent.composables.NoteContentTitle
import com.furianrt.notecontent.composables.NoteTags
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.toolspanel.Panel
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.persistentListOf
import com.furianrt.uikit.R as uiR

private const val ANIM_PANEL_VISIBILITY_DURATION = 200

@Composable
internal fun PageScreen(
    noteId: String,
    isInEditMode: Boolean,
    onFocusChange: () -> Unit,
    modifier: Modifier = Modifier,
    screenState: PageScreenState = rememberPageScreenState(),
) {
    val viewModel = hiltViewModel<PageViewModel, PageViewModel.Factory>(
        key = noteId,
        creationCallback = { factory -> factory.create(noteId = noteId) }
    )
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
        onFocusChange = onFocusChange,
    )
}

@Composable
private fun PageScreenContent(
    uiState: PageUiState,
    screenState: PageScreenState,
    onEvent: (event: PageEvent) -> Unit,
    onFocusChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is PageUiState.Success ->
            SuccessScreen(
                uiState = uiState,
                onEvent = onEvent,
                onFocusChange = onFocusChange,
                modifier = modifier,
                screenState = screenState,
            )

        is PageUiState.Loading -> LoadingScreen(modifier)
        is PageUiState.Empty -> EmptyScreen(modifier)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SuccessScreen(
    uiState: PageUiState.Success,
    onEvent: (event: PageEvent) -> Unit,
    onFocusChange: () -> Unit,
    modifier: Modifier = Modifier,
    screenState: PageScreenState = rememberPageScreenState(),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            .imePadding()
            .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(8.dp)),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(
                count = uiState.content.count(),
                key = { uiState.content[it].id },
                contentType = { uiState.content[it].javaClass.name },
            ) { index ->

                when (val content = uiState.content[index]) {
                    is UiNoteContent.Title -> {
                        NoteContentTitle(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = if (index == 0) 8.dp else 0.dp)
                                .padding(horizontal = 8.dp)
                                .animateItemPlacement(),
                            title = content,
                            hint = if (index == 0) {
                                stringResource(id = uiR.string.note_title_hint_text)
                            } else {
                                stringResource(id = uiR.string.note_title_hint_write_more_here)
                            },
                            isInEditMode = uiState.isInEditMode,
                            onTitleChange = { text ->
                                onEvent(PageEvent.OnTitleTextChange(content.id, text))
                            },
                            onTitleFocused = { id ->
                                onEvent(PageEvent.OnTitleFocused(index))
                                onFocusChange()
                            },
                            isFocused = screenState.focusedTitleIndex == index,
                            focusOffset = {
                                0 // TODO
                            },
                        )
                    }

                    is UiNoteContent.MediaBlock -> {
                        NoteContentMedia(
                            modifier = Modifier.animateItemPlacement(),
                            block = content,
                            isEditable = uiState.isInEditMode,
                        )
                    }
                }
            }
            if (uiState.tags.isNotEmpty()) {
                item(key = "tags") {
                    NoteTags(
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp, top = 6.dp)
                            .animateItemPlacement(),
                        tags = uiState.tags,
                        isEditable = uiState.isInEditMode,
                        onTagClick = { onEvent(PageEvent.OnTagClick(it)) },
                        onTagRemoveClick = { onEvent(PageEvent.OnTagRemoved(it)) },
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = uiState.isInEditMode,
            enter = fadeIn(animationSpec = tween(durationMillis = ANIM_PANEL_VISIBILITY_DURATION)),
            exit = fadeOut(animationSpec = tween(durationMillis = ANIM_PANEL_VISIBILITY_DURATION)),
            content = { Panel() }
        )
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

@PreviewWithBackground
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
            onFocusChange = {},
        )
    }
}
