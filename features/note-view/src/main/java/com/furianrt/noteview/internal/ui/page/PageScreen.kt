package com.furianrt.noteview.internal.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.notecontent.composables.NoteContentMedia
import com.furianrt.notecontent.composables.NoteContentTitle
import com.furianrt.notecontent.composables.NoteTags
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.toolspanel.Panel
import com.furianrt.uikit.extensions.offsetYInverted
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.persistentListOf
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import com.furianrt.uikit.R as uiR

private const val ANIM_PANEL_VISIBILITY_DURATION = 200
private const val TAGS_ITEM_KEY = "tags"

@Composable
internal fun PageScreen(
    noteId: String,
    isInEditMode: Boolean,
    onFocusChange: () -> Unit,
    toolbarState: CollapsingToolbarScaffoldState,
    listState: LazyListState,
    titleScrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<PageViewModel, PageViewModel.Factory>(
        key = noteId,
        creationCallback = { factory -> factory.create(noteId = noteId) }
    )
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->

        }
    }
    LaunchedEffect(isInEditMode) {
        viewModel.onEvent(PageEvent.OnEditModeStateChange(isInEditMode))
    }

    PageScreenContent(
        modifier = modifier,
        uiState = uiState,
        toolbarState = toolbarState,
        listState = listState,
        titleScrollState = titleScrollState,
        onEvent = viewModel::onEvent,
        onFocusChange = onFocusChange,
    )
}

@Composable
private fun PageScreenContent(
    modifier: Modifier,
    uiState: PageUiState,
    toolbarState: CollapsingToolbarScaffoldState,
    listState: LazyListState,
    titleScrollState: ScrollState,
    onEvent: (event: PageEvent) -> Unit,
    onFocusChange: () -> Unit,
) {
    when (uiState) {
        is PageUiState.Success ->
            SuccessScreen(
                modifier = modifier,
                uiState = uiState,
                onEvent = onEvent,
                onFocusChange = onFocusChange,
                // screenState = screenState,
                toolbarState = toolbarState,
                listState = listState,
                titleScrollState = titleScrollState,
            )

        is PageUiState.Loading -> LoadingScreen()
        is PageUiState.Empty -> EmptyScreen()
    }
}

@Composable
private fun SuccessScreen(
    uiState: PageUiState.Success,
    toolbarState: CollapsingToolbarScaffoldState,
    listState: LazyListState,
    titleScrollState: ScrollState,
    onEvent: (event: PageEvent) -> Unit,
    onFocusChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var toolsPanelRect by remember { mutableStateOf(Rect.Zero) }
    val isListAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val background = MaterialTheme.colorScheme.tertiary
    val roundedShape = if (isListAtTop) {
        RoundedCornerShape(8.dp)
    } else {
        RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomEnd = 8.dp,
            bottomStart = 8.dp,
        )
    }
    Column(
        modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            .drawBehind {
                val resultSize = size.copy(
                    height = if (uiState.isInEditMode) {
                        toolsPanelRect.bottom - toolbarState.offsetYInverted
                    } else {
                        size.height
                    }
                )
                drawOutline(
                    outline = roundedShape.createOutline(
                        size = resultSize,
                        layoutDirection = layoutDirection,
                        density = density
                    ),
                    color = background
                )
            }
            .imePadding(),
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .drawWithContent {
                    clipRect(
                        bottom = if (uiState.isInEditMode) {
                            toolsPanelRect.top - toolbarState.offsetYInverted
                        } else {
                            size.height
                        },
                        block = { this@drawWithContent.drawContent() }
                    )
                },
            state = listState,
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding() + 90.dp
            ),
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
                                .padding(
                                    top = if (index == 0) 8.dp else 0.dp,
                                    start = 8.dp,
                                    end = 8.dp
                                )
                                .animateItem(),
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
                                onFocusChange()
                            },
                            scrollState = titleScrollState,
                            toolbarHeight = toolbarState.toolbarState.minHeight,
                        )
                    }

                    is UiNoteContent.MediaBlock -> {
                        NoteContentMedia(
                            modifier = Modifier.animateItem(),
                            block = content,
                            isEditable = uiState.isInEditMode,
                        )
                    }
                }
            }
            if (uiState.tags.isNotEmpty()) {
                item(key = TAGS_ITEM_KEY) {
                    NoteTags(
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp, top = 6.dp)
                            .animateItem(),
                        tags = uiState.tags,
                        isEditable = uiState.isInEditMode,
                        onTagClick = { onEvent(PageEvent.OnTagClick(it)) },
                        onTagRemoveClick = { onEvent(PageEvent.OnTagRemoved(it)) },
                    )
                }
            }
        }
        AnimatedVisibility(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .onGloballyPositioned { toolsPanelRect = it.boundsInParent() }
                .graphicsLayer { translationY = -toolbarState.offsetYInverted.toFloat() },
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
            toolbarState = rememberCollapsingToolbarScaffoldState(),
            listState = rememberLazyListState(),
            titleScrollState = rememberScrollState(),
        )
    }
}
