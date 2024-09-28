package com.furianrt.notepage.internal.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.focus.FocusRequester
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.core.findInstance
import com.furianrt.notecontent.composables.NoteContentMedia
import com.furianrt.notecontent.composables.NoteContentTitle
import com.furianrt.notecontent.composables.NoteTags
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notepage.R
import com.furianrt.notepage.api.PageScreenState
import com.furianrt.notepage.api.rememberPageScreenState
import com.furianrt.permissions.extensions.openAppSettingsScreen
import com.furianrt.permissions.ui.MediaPermissionDialog
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.toolspanel.api.ActionsPanel
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.offsetYInverted
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.PreviewWithBackground
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.collections.immutable.persistentListOf

private const val ANIM_PANEL_VISIBILITY_DURATION = 200
private const val TAGS_ITEM_KEY = "tags"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun NotePageScreenInternal(
    state: PageScreenState,
    noteId: String,
    isInEditMode: Boolean,
    isNoteCreationMode: Boolean,
    onFocusChange: () -> Unit,
    openMediaViewScreen: (noteId: String, mediaName: String, identifier: DialogIdentifier) -> Unit,
    openMediaSelectorScreen: (identifier: DialogIdentifier) -> Unit,
) {
    val viewModel = hiltViewModel<PageViewModel, PageViewModel.Factory>(
        key = noteId,
        creationCallback = { factory ->
            factory.create(
                noteId = noteId,
                isNoteCreationMode = isNoteCreationMode,
            )
        },
    )
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    val context = LocalContext.current

    val storagePermissionsState = rememberMultiplePermissionsState(
        permissions = PermissionsUtils.getMediaPermissionList(),
        onPermissionsResult = { viewModel.onEvent(PageEvent.OnMediaPermissionsSelected) },
    )

    var showMediaPermissionDialog by remember { mutableStateOf(false) }

    state.setOnSaveContentListener { viewModel.onEvent(PageEvent.OnOnSaveContentRequest) }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PageEffect.ShowPermissionsDeniedDialog -> showMediaPermissionDialog = true
                is PageEffect.OpenMediaSelector -> openMediaSelectorScreen(effect.identifier)
                is PageEffect.OpenMediaViewScreen -> {
                    openMediaViewScreen(effect.noteId, effect.mediaName, effect.identifier)
                }

                is PageEffect.UpdateContentChangedState -> state.setContentChanged(effect.isChanged)
                is PageEffect.FocusFirstTitle -> state.focusFirstTitle()
                is PageEffect.RequestStoragePermissions -> {
                    storagePermissionsState.launchMultiplePermissionRequest()
                }

                /*is PageEffect.OpenMediaSelector -> navHostController.navigate(
                    route = "Sheet/${effect.dialogId}/${effect.requestId}",
                    navOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
                )*/


                /*is PageEffect.OpenMediaViewScreen -> navHostController.navigate(
                    route = "MediaView/${effect.noteId}/${effect.mediaName}/${effect.dialogId}/${effect.requestId}",
                    navOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
                )*/
            }
        }
    }
    LaunchedEffect(isInEditMode) {
        viewModel.onEvent(PageEvent.OnEditModeStateChange(isInEditMode))
        val isListAtTop = with(state.listState) {
            firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0
        }
        if (isInEditMode && isListAtTop) {
            state.listState.requestScrollToItem(0)
        }
    }

    PageScreenContent(
        state = state,
        uiState = uiState,
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
    state: PageScreenState,
    uiState: PageUiState,
    onEvent: (event: PageEvent) -> Unit,
    onFocusChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is PageUiState.Success ->
            SuccessScreen(
                modifier = modifier,
                state = state,
                uiState = uiState,
                onEvent = onEvent,
                onFocusChange = onFocusChange,
            )

        is PageUiState.Loading -> LoadingScreen()
        is PageUiState.Empty -> EmptyScreen()
    }
}

@Composable
private fun SuccessScreen(
    state: PageScreenState,
    uiState: PageUiState.Success,
    onEvent: (event: PageEvent) -> Unit,
    onFocusChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var toolsPanelRect by remember { mutableStateOf(Rect.Zero) }
    var focusedTitleId: String? by remember { mutableStateOf(null) }
    val isListAtTop by remember {
        derivedStateOf {
            state.listState.firstVisibleItemIndex == 0 &&
                    state.listState.firstVisibleItemScrollOffset == 0
        }
    }
    val hazeState = remember { HazeState() }
    val focusManager = LocalFocusManager.current
    val focusRequesters = remember { mutableMapOf<Int, FocusRequester>() }
    state.setOnFirstTitleFocusRequestListener {
        focusRequesters.values.firstOrNull()?.requestFocus()
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
                        toolsPanelRect.bottom - state.toolbarState.offsetYInverted
                    } else {
                        size.height
                    }
                }
            )
            .haze(hazeState)
            .imePadding(),
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .drawWithContent {
                    clipRect(
                        bottom = if (uiState.isInEditMode) {
                            toolsPanelRect.top - state.toolbarState.offsetYInverted
                        } else {
                            size.height
                        },
                        block = { this@drawWithContent.drawContent() }
                    )
                }
                .clickableNoRipple { focusManager.clearFocus() },
            state = state.listState,
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding() + 90.dp
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            itemsIndexed(
                items = uiState.content,
                key = { _, item -> item.id },
                contentType = { _, item -> item.javaClass.simpleName },
            ) { index, item ->
                when (item) {
                    is UiNoteContent.Title -> NoteContentTitle(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = if (index == 0) 8.dp else 14.dp,
                                bottom = 14.dp,
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
                        focusRequester = focusRequesters.getOrPut(index) { FocusRequester() },
                        onTitleFocused = { id ->
                            onEvent(PageEvent.OnTitleFocusChange(id))
                            focusedTitleId = id
                            onFocusChange()
                        },
                        onTitleTextChange = { onEvent(PageEvent.OnTitleTextChange(it)) },
                        scrollState = state.titleScrollState,
                        focusOffset = state.toolbarState.toolbarState.minHeight,
                    )

                    is UiNoteContent.MediaBlock -> NoteContentMedia(
                        modifier = Modifier.animateItem(),
                        block = item,
                        dropDownHazeState = hazeState,
                        clickable = true,
                        onClick = { onEvent(PageEvent.OnMediaClick(it)) },
                        onRemoveClick = { onEvent(PageEvent.OnMediaRemoveClick(it)) },
                        onShareClick = { onEvent(PageEvent.OnMediaShareClick(it)) },
                    )
                }
            }
            if (uiState.tags.isNotEmpty()) {
                item(key = TAGS_ITEM_KEY) {
                    NoteTags(
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp, top = 20.dp)
                            .fillMaxWidth()
                            .animateItem(),
                        tags = uiState.tags,
                        isEditable = uiState.isInEditMode,
                        toolbarHeight = state.toolbarState.toolbarState.minHeight,
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
                .graphicsLayer { translationY = -state.toolbarState.offsetYInverted.toFloat() },
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
    Box(modifier = modifier.fillMaxSize())
}

@Composable
private fun EmptyScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize())
}

@PreviewWithBackground
@Composable
private fun SuccessScreenPreview() {
    SerenityTheme {
        SuccessScreen(
            state = rememberPageScreenState(),
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
        )
    }
}
