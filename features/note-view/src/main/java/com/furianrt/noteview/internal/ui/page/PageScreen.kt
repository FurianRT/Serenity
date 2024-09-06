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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.furianrt.core.findInstance
import com.furianrt.notecontent.composables.NoteContentMedia
import com.furianrt.notecontent.composables.NoteContentTitle
import com.furianrt.notecontent.composables.NoteTags
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.noteview.R
import com.furianrt.permissions.extensions.openAppSettingsScreen
import com.furianrt.permissions.ui.MediaPermissionDialog
import com.furianrt.toolspanel.api.ActionsPanel
import com.furianrt.uikit.extensions.offsetYInverted
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.collections.immutable.persistentListOf
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

private const val ANIM_PANEL_VISIBILITY_DURATION = 200
private const val TAGS_ITEM_KEY = "tags"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun PageScreen(
    noteId: String,
    isInEditMode: Boolean,
    onFocusChange: () -> Unit,
    toolbarState: CollapsingToolbarScaffoldState,
    listState: LazyListState,
    titleScrollState: ScrollState,
    navHostController: NavHostController,
) {
    val viewModel = hiltViewModel<PageViewModel, PageViewModel.Factory>(
        key = noteId,
        creationCallback = { factory -> factory.create(noteId = noteId) },
    )
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    val context = LocalContext.current

    val storagePermissionsState = rememberMultiplePermissionsState(
        permissions = uiState.mediaPermissionsList,
        onPermissionsResult = { viewModel.onEvent(PageEvent.OnMediaPermissionsSelected) },
    )

    var showMediaPermissionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PageEffect.RequestStoragePermissions -> {
                    storagePermissionsState.launchMultiplePermissionRequest()
                }

                is PageEffect.ShowPermissionsDeniedDialog -> {
                    showMediaPermissionDialog = true
                }

                is PageEffect.OpenMediaSelector -> navHostController.navigate(
                    route = "Sheet",
                    navOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
                )
            }
        }
    }
    LaunchedEffect(isInEditMode) {
        viewModel.onEvent(PageEvent.OnEditModeStateChange(isInEditMode))
        val isListAtTop = with(listState) {
            firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0
        }
        if (isInEditMode && isListAtTop) {
            listState.requestScrollToItem(0)
        }
    }

    PageScreenContent(
        uiState = uiState,
        toolbarState = toolbarState,
        listState = listState,
        titleScrollState = titleScrollState,
        onEvent = viewModel::onEvent,
        onFocusChange = onFocusChange,
    )

    if (showMediaPermissionDialog) {
        MediaPermissionDialog(
            onDismissRequest = { showMediaPermissionDialog = false },
            onSettingsClick = context::openAppSettingsScreen,
        )
    }
}

@Composable
private fun PageScreenContent(
    uiState: PageUiState,
    toolbarState: CollapsingToolbarScaffoldState,
    listState: LazyListState,
    titleScrollState: ScrollState,
    onEvent: (event: PageEvent) -> Unit,
    onFocusChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is PageUiState.Success ->
            SuccessScreen(
                modifier = modifier,
                uiState = uiState,
                onEvent = onEvent,
                onFocusChange = onFocusChange,
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
    var focusedTitleId: String? by remember { mutableStateOf(null) }
    val isListAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            .drawNoteBackground(
                shape = if (isListAtTop) {
                    RoundedCornerShape(8.dp)
                } else {
                    RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp)
                },
                color = MaterialTheme.colorScheme.tertiary,
                density = LocalDensity.current,
                height = {
                    if (uiState.isInEditMode) {
                        toolsPanelRect.bottom - toolbarState.offsetYInverted
                    } else {
                        size.height
                    }
                }
            )
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
            itemsIndexed(
                items = uiState.content,
                key = { index, item -> item.id },
                contentType = { _, item -> item.javaClass.name },
            ) { index, item ->
                when (item) {
                    is UiNoteContent.Title -> NoteContentTitle(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = if (index == 0) 8.dp else 0.dp,
                                start = 8.dp,
                                end = 8.dp,
                            )
                            .animateItem(),
                        title = item,
                        hint = if (index == 0) {
                            stringResource(id = R.string.note_title_hint_text)
                        } else {
                            stringResource(id = R.string.note_title_hint_write_more_here)
                        },
                        isInEditMode = uiState.isInEditMode,
                        onTitleFocused = { id ->
                            focusedTitleId = id
                            onFocusChange()
                        },
                        scrollState = titleScrollState,
                        focusOffset = toolbarState.toolbarState.minHeight,
                    )

                    is UiNoteContent.MediaBlock -> NoteContentMedia(
                        modifier = Modifier.animateItem(),
                        block = item,
                        isEditable = uiState.isInEditMode,
                    )
                }
            }
            if (uiState.tags.isNotEmpty()) {
                item(key = TAGS_ITEM_KEY) {
                    NoteTags(
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp, top = 6.dp)
                            .fillMaxWidth()
                            .animateItem(),
                        tags = uiState.tags,
                        isEditable = uiState.isInEditMode,
                        toolbarHeight = toolbarState.toolbarState.minHeight,
                        onTagRemoveClick = { onEvent(PageEvent.OnTagRemoveClick(it)) },
                        onDoneEditing = { onEvent(PageEvent.OnTagDoneEditing(it)) },
                        onTextEntered = { onEvent(PageEvent.OnTagTextEntered) },
                        onTextCleared = { onEvent(PageEvent.OnTagTextCleared) },
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
            content = {
                val titleState = remember(uiState.content, focusedTitleId) {
                    uiState.content
                        .findInstance<UiNoteContent.Title> { it.id == focusedTitleId }?.state
                }
                ActionsPanel(
                    textFieldState = titleState ?: TextFieldState(),
                    onSelectMediaClick = { onEvent(PageEvent.OnSelectMediaClick) },
                )
            }
        )
    }
}

private fun Modifier.drawNoteBackground(
    shape: Shape,
    color: Color,
    density: Density,
    height: CacheDrawScope.() -> Float,
) = drawWithCache {
    val resultSize = size.copy(height = height())
    onDrawBehind {
        drawOutline(
            outline = shape.createOutline(
                size = resultSize,
                layoutDirection = layoutDirection,
                density = density
            ),
            color = color
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
                        state = TextFieldState(
                            initialText = "Kotlin is a modern programming language with a " +
                                    "lot more syntactic sugar compared to Java, and as such " +
                                    "there is equally more black magic",
                        ),
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
