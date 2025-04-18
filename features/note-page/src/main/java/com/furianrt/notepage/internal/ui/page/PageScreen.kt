package com.furianrt.notepage.internal.ui.page

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.core.findInstance
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediaselector.api.MediaSelectorBottomSheet
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.notelistui.composables.NoteContentMedia
import com.furianrt.notelistui.composables.NoteContentVoice
import com.furianrt.notelistui.composables.NoteTags
import com.furianrt.notelistui.composables.title.NoteContentTitle
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.notelistui.entities.isEmptyTitle
import com.furianrt.notepage.R
import com.furianrt.notepage.api.PageScreenState
import com.furianrt.notepage.api.rememberPageScreenState
import com.furianrt.notepage.internal.ui.stickers.StickersBox
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import com.furianrt.permissions.extensions.openAppSettingsScreen
import com.furianrt.permissions.ui.MediaPermissionDialog
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.toolspanel.api.ActionsPanel
import com.furianrt.toolspanel.api.ToolsPanelConstants
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.animatePlacementInScope
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.getStatusBarHeight
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.PreviewWithBackground
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

private const val ANIM_PANEL_VISIBILITY_DURATION = 200

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun NotePageScreenInternal(
    state: PageScreenState,
    noteId: String,
    isInEditMode: Boolean,
    isSelected: Boolean,
    isNoteCreationMode: Boolean,
    onFocusChange: () -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    openMediaViewScreen: (noteId: String, mediaName: String, identifier: DialogIdentifier) -> Unit,
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
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val storagePermissionsState = rememberMultiplePermissionsState(
        permissions = PermissionsUtils.getMediaPermissionList(),
        onPermissionsResult = { viewModel.onEvent(PageEvent.OnMediaPermissionsSelected) },
    )

    var showMediaPermissionDialog by remember { mutableStateOf(false) }

    state.setOnSaveContentListener { viewModel.onEvent(PageEvent.OnOnSaveContentRequest) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PageEffect.ShowPermissionsDeniedDialog -> showMediaPermissionDialog = true
                is PageEffect.OpenMediaSelector -> {
                    focusManager.clearFocus(force = true)
                    scope.launch { state.bottomScaffoldState.bottomSheetState.expand() }
                }

                is PageEffect.OpenMediaViewScreen -> {
                    keyboardController?.hide()
                    openMediaViewScreen(effect.noteId, effect.mediaName, effect.identifier)
                }

                is PageEffect.OpenMediaViewer -> openMediaViewer(effect.route)
                is PageEffect.UpdateContentChangedState -> state.setContentChanged(effect.isChanged)
                is PageEffect.FocusFirstTitle -> state.focusTitle(effect.index)
                is PageEffect.RequestStoragePermissions -> {
                    storagePermissionsState.launchMultiplePermissionRequest()
                }

                is PageEffect.BringContentToView -> state.bringContentToView(effect.index)
                is PageEffect.ClearFocus -> focusManager.clearFocus()
            }
        }
    }
    LaunchedEffect(isInEditMode) {
        viewModel.onEvent(PageEvent.OnEditModeStateChange(isInEditMode))
    }

    LaunchedEffect(isSelected) {
        viewModel.onEvent(PageEvent.OnIsSelectedChange(isSelected))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessScreen(
    state: PageScreenState,
    uiState: PageUiState.Success,
    onEvent: (event: PageEvent) -> Unit,
    onFocusChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    var isToolsPanelMenuVisible by remember { mutableStateOf(false) }
    var toolsPanelRect by remember { mutableStateOf(Rect.Zero) }
    val toolsPanelHeight by remember(
        uiState.isInEditMode,
        isToolsPanelMenuVisible,
        toolsPanelRect
    ) {
        mutableStateOf(
            when {
                !uiState.isInEditMode -> ToolsPanelConstants.PANEL_HEIGHT
                isToolsPanelMenuVisible -> density.run { toolsPanelRect.height.toDp() }
                else -> ToolsPanelConstants.PANEL_HEIGHT
            } + 24.dp
        )
    }
    var focusedTitleId: String? by remember { mutableStateOf(null) }
    val hazeState = remember { HazeState() }
    val focusRequesters = remember { mutableMapOf<Int, FocusRequester>() }
    val statusBarHeight = rememberSaveable(state) { view.getStatusBarHeight() }
    val statusBarHeightDp = density.run { statusBarHeight.toDp() }
    val toolbarMargin = statusBarHeightDp + ToolbarConstants.toolbarHeight + 8.dp
    val toolbarMarginPx = density.run { toolbarMargin.toPx() }
    var listSize by remember { mutableStateOf(IntSize.Zero) }
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
        .calculateBottomPadding() + 8.dp

    var topEmptyTitleHeight by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(focusedTitleId) {
        val title = uiState.content.findInstance<UiNoteContent.Title> { it.id == focusedTitleId }
        if (title != null) {
            snapshotFlow { title.state.selection }
                .collect { onEvent(PageEvent.OnFocusedTitleSelectionChange) }
        }
    }

    state.setOnTitleFocusRequestListener { focusRequesters[it]?.requestFocus() }
    state.setOnBringContentToViewListener { position ->
        scope.launch {
            state.scrollToPosition(position = position, topOffset = toolbarMarginPx.toInt())
        }
    }

    MediaSelectorBottomSheet(
        modifier = modifier,
        state = state.bottomScaffoldState,
        openMediaViewer = remember {
            { route: MediaViewerRoute -> onEvent(PageEvent.OnOpenMediaViewerRequest(route)) }
        },
        onMediaSelected = remember {
            { result: MediaResult -> onEvent(PageEvent.OnMediaSelected(result)) }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .clipToBounds()
                .applyIf(uiState.isInEditMode) {
                    Modifier.clipPanel(toolsPanelTop = { toolsPanelRect.top })
                }
                .haze(hazeState)
                .verticalScroll(state.listState)
                .clickableNoRipple { onEvent(PageEvent.OnClickOutside) }
                .padding(top = toolbarMargin, bottom = navBarPadding)
                .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(8.dp))
                .padding(bottom = toolsPanelHeight)
                .imePadding(),
        ) {
            ContentItems(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { listSize = it },
                uiState = uiState,
                hazeState = hazeState,
                titleFocusRequester = { focusRequesters.getOrPut(it) { FocusRequester() } },
                onEvent = onEvent,
                onTitleFocusChange = { id ->
                    focusedTitleId = id
                    onFocusChange()
                },
                onEmptyTitleHeightChange = { topEmptyTitleHeight = it },
            )
            if (uiState.stickers.isNotEmpty()) {
                val showStickersPadding = uiState.isInEditMode &&
                        uiState.content.firstOrNull().isEmptyTitle()
                StickersBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = StickerItem.DEFAULT_SIZE * 2)
                        .height(density.run { listSize.height.toDp() }),
                    stickers = uiState.stickers,
                    emptyTitleHeight = if (showStickersPadding) topEmptyTitleHeight else 0f,
                    containerSize = listSize,
                    onStickerClick = { onEvent(PageEvent.OnStickerClick(it)) },
                    onRemoveStickerClick = { onEvent(PageEvent.OnRemoveStickerClick(it)) },
                    onStickerChanged = { onEvent(PageEvent.OnStickerChanged(it)) },
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .onGloballyPositioned { toolsPanelRect = it.boundsInParent() }
                .applyIf(state.isVoiceRecordActive) { Modifier.zIndex(1f) },
        ) {
            AnimatedVisibility(
                visible = uiState.isInEditMode,
                enter = fadeIn(tween(durationMillis = ANIM_PANEL_VISIBILITY_DURATION)),
                exit = fadeOut(tween(durationMillis = ANIM_PANEL_VISIBILITY_DURATION)),
                content = {
                    val titleState = remember(uiState.content, focusedTitleId) {
                        uiState.content
                            .findInstance<UiNoteContent.Title> { it.id == focusedTitleId }
                            ?.state
                    }
                    ActionsPanel(
                        noteId = uiState.noteId,
                        fontFamily = uiState.fontFamily,
                        fontColor = uiState.fontColor,
                        fontSize = uiState.fontSize,
                        hazeState = hazeState,
                        titleState = titleState ?: NoteTitleState(),
                        onMenuVisibilityChange = { isToolsPanelMenuVisible = it },
                        onSelectMediaClick = { onEvent(PageEvent.OnSelectMediaClick) },
                        onVoiceRecordStart = {
                            onEvent(PageEvent.OnVoiceStarted)
                            state.isVoiceRecordActive = true
                        },
                        onRecordComplete = { record ->
                            state.isVoiceRecordActive = false
                            onEvent(PageEvent.OnVoiceRecorded(record))
                        },
                        onVoiceRecordCancel = { state.isVoiceRecordActive = false },
                        onFontFamilySelected = { onEvent(PageEvent.OnFontFamilySelected(it)) },
                        onFontColorSelected = { onEvent(PageEvent.OnFontColorSelected(it)) },
                        onFontSizeSelected = { onEvent(PageEvent.OnFontSizeSelected(it)) },
                        onFontStyleClick = { onEvent(PageEvent.OnSelectFontClick) },
                        onOnStickersClick = { onEvent(PageEvent.OnSelectStickersClick) },
                        onStickerSelected = { selectedSticker ->
                            onEvent(
                                PageEvent.OnStickerSelected(
                                    sticker = StickerItem.build(
                                        typeId = selectedSticker.id,
                                        icon = selectedSticker.icon,
                                        scrollOffset = state.listState.value,
                                        viewPortHeight = state.listState.viewportSize,
                                        toolsPanelHeight = density.run {
                                            toolsPanelRect.height.toDp()
                                        },
                                        toolBarHeight = toolbarMargin,
                                        density = density,
                                    ),
                                )
                            )
                        },
                    )
                }
            )
        }
        AnimatedVisibility(
            visible = state.dimSurface,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim)
                    .clickableNoRipple {},
            )
        }
    }
}

@Composable
private fun ContentItems(
    uiState: PageUiState.Success,
    hazeState: HazeState,
    titleFocusRequester: (index: Int) -> FocusRequester,
    onTitleFocusChange: (id: String) -> Unit,
    onEmptyTitleHeightChange: (height: Float) -> Unit,
    onEvent: (event: PageEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    val density = LocalDensity.current
    val topTitlePadding = 8.dp
    val topPadding = density.run { topTitlePadding.toPx() + 6.dp.toPx() }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        LookaheadScope {
            uiState.content.forEachIndexed { index, item ->
                val nextItem = uiState.content.getOrNull(index + 1)
                key(item.id) {
                    when (item) {
                        is UiNoteContent.Title -> NoteContentTitle(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = when {
                                        index == 0 -> topTitlePadding
                                        item.isEmptyTitle() -> 0.dp
                                        else -> 14.dp
                                    },
                                    bottom = when {
                                        index == 0 && item.isEmptyTitle() -> 4.dp
                                        item.isEmptyTitle() -> 0.dp
                                        else -> 14.dp
                                    },
                                    start = 8.dp,
                                    end = 8.dp,
                                )
                                .applyIf(index == 0 && item.state.text.isEmpty()) {
                                    Modifier.onSizeChanged { size ->
                                        density.run {
                                            onEmptyTitleHeightChange(size.height + topPadding)
                                        }
                                    }
                                }
                                .animatePlacementInScope(this@LookaheadScope),
                            title = item,
                            color = uiState.fontColor.value,
                            fontFamily = uiState.fontFamily.value,
                            fontSize = uiState.fontSize.sp,
                            hint = if (index == 0) {
                                stringResource(R.string.note_title_hint_text)
                            } else {
                                stringResource(R.string.note_title_hint_write_more_here)
                            },
                            isInEditMode = uiState.isInEditMode,
                            focusRequester = titleFocusRequester(index),
                            onTitleFocused = { id ->
                                onEvent(PageEvent.OnTitleFocusChange(id))
                                onTitleFocusChange(id)
                            },
                            onTitleTextChange = { onEvent(PageEvent.OnTitleTextChange(it)) },
                        )

                        is UiNoteContent.MediaBlock -> NoteContentMedia(
                            modifier = Modifier.animatePlacementInScope(this@LookaheadScope),
                            block = item,
                            dropDownHazeState = hazeState,
                            clickable = true,
                            onClick = { onEvent(PageEvent.OnMediaClick(it)) },
                            onRemoveClick = { media ->
                                onEvent(PageEvent.OnMediaRemoveClick(media))
                                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            },
                            onShareClick = { onEvent(PageEvent.OnMediaShareClick(it)) },
                        )

                        is UiNoteContent.Voice -> NoteContentVoice(
                            modifier = Modifier
                                .padding(
                                    start = 8.dp,
                                    end = 8.dp,
                                    top = 0.dp,
                                    bottom = if (nextItem.isEmptyTitle()) 0.dp else 4.dp,
                                )
                                .animatePlacementInScope(this@LookaheadScope),
                            voice = item,
                            isPlaying = item.id == uiState.playingVoiceId,
                            isRemovable = uiState.isInEditMode,
                            onRemoveClick = { voice ->
                                onEvent(PageEvent.OnVoiceRemoveClick(voice))
                                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            },
                            onPlayClick = { onEvent(PageEvent.OnVoicePlayClick(it)) },
                            onProgressSelected = { voice, value ->
                                onEvent(PageEvent.OnVoiceProgressSelected(voice, value))
                            },
                        )
                    }
                }
            }

            key(UiNoteTag.BLOCK_ID + uiState.noteId) {
                NoteTags(
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp, top = 20.dp)
                        .fillMaxWidth()
                        .animatePlacementInScope(this@LookaheadScope),
                    tags = uiState.tags,
                    isEditable = uiState.isInEditMode,
                    animateItemsPlacement = true,
                    showStub = true,
                    onTagRemoveClick = {
                        onEvent(PageEvent.OnTagRemoveClick(it))
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    },
                    onDoneEditing = { onEvent(PageEvent.OnTagDoneEditing(it)) },
                    onTextEntered = { onEvent(PageEvent.OnTagTextEntered) },
                    onTextCleared = { onEvent(PageEvent.OnTagTextCleared) },
                    onFocusChanged = { onEvent(PageEvent.OnTagFocusChanged) },
                )
            }
        }
    }
}

private fun Modifier.clipPanel(
    toolsPanelTop: () -> Float,
): Modifier = then(
    Modifier.drawWithCache {
        val path = Path()
        val rect = size.toRect()
        val cornerRadius = CornerRadius(8.dp.toPx())
        path.addRoundRect(
            RoundRect(
                rect = rect.copy(
                    bottom = toolsPanelTop() + ToolsPanelConstants.PANEL_HEIGHT.toPx(),
                ),
                bottomLeft = cornerRadius,
                bottomRight = cornerRadius,
            )
        )
        onDrawWithContent {
            clipPath(path) {
                this@onDrawWithContent.drawContent()
            }
        }
    }
)

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize())
}

@Composable
private fun EmptyScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize())
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewWithBackground
@Composable
private fun SuccessScreenPreview() {
    SerenityTheme {
        SuccessScreen(
            state = rememberPageScreenState(),
            uiState = PageUiState.Success(
                noteId = "123",
                isInEditMode = false,
                tags = persistentListOf(),
                stickers = persistentListOf(),
                playingVoiceId = null,
                fontFamily = UiNoteFontFamily.QUICK_SAND,
                fontColor = UiNoteFontColor.WHITE,
                fontSize = 15,
                content = persistentListOf(
                    UiNoteContent.Title(
                        id = "1",
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
            onEvent = {},
            onFocusChange = {},
        )
    }
}
