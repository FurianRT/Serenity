package com.furianrt.noteview.internal.ui.page

import android.app.Activity
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.furianrt.notecontent.composables.NoteContentMedia
import com.furianrt.notecontent.composables.NoteContentTitle
import com.furianrt.notecontent.composables.NoteTags
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.toolspanel.Panel
import com.furianrt.uikit.extensions.isCollapsed
import com.furianrt.uikit.extensions.offsetYInverted
import com.furianrt.uikit.theme.SerenityTheme
import dagger.hilt.android.EntryPointAccessors
import kotlinx.collections.immutable.persistentListOf
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import com.furianrt.uikit.R as uiR

private const val ANIM_PANEL_VISIBILITY_DURATION = 250

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
    toolbarState: CollapsingToolbarScaffoldState,
    onFocusChange: () -> Unit,
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
        onFocusChange = onFocusChange,
        lazyListState = lazyListState,
        toolbarState = toolbarState,
    )
}

@Composable
private fun PageScreenContent(
    uiState: PageUiState,
    screenState: PageScreenState,
    lazyListState: LazyListState,
    toolbarState: CollapsingToolbarScaffoldState,
    onEvent: (event: PageEvent) -> Unit,
    onFocusChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is PageUiState.Success ->
            SuccessScreen(
                uiState = uiState,
                lazyListState = lazyListState,
                toolbarState = toolbarState,
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
    lazyListState: LazyListState,
    toolbarState: CollapsingToolbarScaffoldState,
    onEvent: (event: PageEvent) -> Unit,
    onFocusChange: () -> Unit,
    modifier: Modifier = Modifier,
    screenState: PageScreenState = rememberPageScreenState(),
) {
    LaunchedEffect(screenState.focusedTitleIndex) {
        if (!uiState.isInEditMode) {
            return@LaunchedEffect
        }
        screenState.focusedTitleIndex?.let { focusedTitleIndex ->
            lazyListState.animateScrollToItem(focusedTitleIndex)
        }
    }

    var toolsPanelHeight by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            .imePadding(),
    ) {
        val background = MaterialTheme.colorScheme.tertiary
        LazyColumn(
            modifier = Modifier.weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .drawWithContent {
                    clipRect(
                        bottom = if (uiState.isInEditMode) {
                            toolsPanelHeight - toolbarState.offsetYInverted
                        } else {
                            size.height
                        },
                    ) {
                        drawRect(color = background)
                        this@drawWithContent.drawContent()
                    }
                },
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
                                if (toolbarState.isCollapsed) {
                                    0
                                } else {
                                    toolbarState.toolbarState.minHeight
                                }
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
            item(key = "tags") {
                NoteTags(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .animateItemPlacement(),
                    tags = uiState.tags,
                    isEditable = uiState.isInEditMode,
                    onTagClick = { onEvent(PageEvent.OnTagClick(it)) },
                    onTagRemoveClick = { onEvent(PageEvent.OnTagRemoved(it)) },
                )
            }
        }
        AnimatedVisibility(
            modifier = Modifier
                .onGloballyPositioned { toolsPanelHeight = it.boundsInParent().top }
                .graphicsLayer { translationY = -toolbarState.offsetYInverted.toFloat() },
            visible = uiState.isInEditMode,
            enter = fadeIn(animationSpec = tween(durationMillis = ANIM_PANEL_VISIBILITY_DURATION)),
            exit = fadeOut(animationSpec = tween(durationMillis = ANIM_PANEL_VISIBILITY_DURATION)),
        ) {
            Panel()
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
            onFocusChange = {},
            lazyListState = rememberLazyListState(),
            toolbarState = rememberCollapsingToolbarScaffoldState(),
        )
    }
}
