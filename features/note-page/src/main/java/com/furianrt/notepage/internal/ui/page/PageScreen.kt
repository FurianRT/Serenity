package com.furianrt.notepage.internal.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.core.findInstance
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
import com.furianrt.toolspanel.api.VoiceRecord
import com.furianrt.toolspanel.api.entities.Sticker
import com.furianrt.uikit.components.SkipFirstEffect
import com.furianrt.uikit.components.SnackBar
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.animatePlacementInScope
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.bringIntoView
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.getStatusBarHeight
import com.furianrt.uikit.extensions.rememberKeyboardOffsetState
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.PreviewWithBackground
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.collections.immutable.persistentListOf
import com.furianrt.uikit.R as uiR

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
    openMediaViewScreen: (noteId: String, mediaId: String, identifier: DialogIdentifier) -> Unit,
    openMediaSortingScreen: (noteId: String, blockId: String, identifier: DialogIdentifier) -> Unit,
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
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val snackBarHostState = remember { SnackbarHostState() }

    val storagePermissionsState = rememberMultiplePermissionsState(
        permissions = PermissionsUtils.getMediaPermissionList(),
        onPermissionsResult = { viewModel.onEvent(PageEvent.OnMediaPermissionsSelected) },
    )

    var showMediaPermissionDialog by remember { mutableStateOf(false) }

    val view = LocalView.current
    val density = LocalDensity.current
    val hazeState = remember { HazeState() }
    val keyboardOffset by rememberKeyboardOffsetState()
    val statusBarHeight = rememberSaveable(state) { view.getStatusBarHeight() }
    val statusBarHeightDp = density.run { statusBarHeight.toDp() }
    val toolbarMargin = statusBarHeightDp + ToolbarConstants.toolbarHeight + 16.dp
    val toolbarMarginPx = density.run { toolbarMargin.toPx() }
    val bottomFocusMargin = with(LocalDensity.current) { 90.dp.toPx().toInt() }

    val openMediaViewScreenState by rememberUpdatedState(openMediaViewScreen)
    val openMediaViewerState by rememberUpdatedState(openMediaViewer)
    val openMediaSortingScreenState by rememberUpdatedState(openMediaSortingScreen)

    LifecycleStartEffect(Unit) {
        onStopOrDispose { viewModel.onEvent(PageEvent.OnScreenStopped) }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PageEffect.ShowPermissionsDeniedDialog -> showMediaPermissionDialog = true
                is PageEffect.OpenMediaSelector -> {
                    focusManager.clearFocus(force = true)
                    state.bottomScaffoldState.bottomSheetState.expand()
                }

                is PageEffect.OpenMediaViewScreen -> {
                    keyboardController?.hide()
                    openMediaViewScreenState(effect.noteId, effect.mediaId, effect.identifier)
                }

                is PageEffect.OpenMediaViewer -> openMediaViewerState(effect.route)
                is PageEffect.UpdateContentChangedState -> state.setContentChanged(effect.isChanged)
                is PageEffect.RequestStoragePermissions -> {
                    storagePermissionsState.launchMultiplePermissionRequest()
                }

                is PageEffect.BringContentToView -> {
                    effect.content.bringIntoViewRequester.bringIntoView(
                        additionalTopOffset = toolbarMarginPx,
                        additionalBottomOffset = keyboardOffset.toFloat() + bottomFocusMargin,
                    )
                }

                is PageEffect.ClearFocus -> focusManager.clearFocus()
                is PageEffect.ShowMessage -> {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short,
                    )
                }

                is PageEffect.OpenMediaSortingScreen -> openMediaSortingScreenState(
                    effect.noteId,
                    effect.mediaBlockId,
                    effect.identifier
                )
            }
        }
    }
    LaunchedEffect(isInEditMode) {
        viewModel.onEvent(PageEvent.OnEditModeStateChange(isInEditMode))
    }

    SkipFirstEffect(isSelected) {
        viewModel.onEvent(PageEvent.OnIsSelectedChange(isSelected))
    }

    PageScreenContent(
        state = state,
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        hazeState = hazeState,
        isSelected = isSelected,
        onEvent = viewModel::onEvent,
        onFocusChange = onFocusChange,
    )

    if (showMediaPermissionDialog) {
        MediaPermissionDialog(
            hazeState = hazeState,
            onDismissRequest = { showMediaPermissionDialog = false },
            onSettingsClick = context::openAppSettingsScreen,
        )
    }
}

@Composable
private fun PageScreenContent(
    state: PageScreenState,
    uiState: PageUiState,
    snackBarHostState: SnackbarHostState,
    hazeState: HazeState,
    isSelected: Boolean,
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
                snackBarHostState = snackBarHostState,
                hazeState = hazeState,
                isSelected = isSelected,
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
    snackBarHostState: SnackbarHostState,
    hazeState: HazeState,
    isSelected: Boolean,
    onEvent: (event: PageEvent) -> Unit,
    onFocusChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    val density = LocalDensity.current
    var isToolsPanelMenuVisible by remember { mutableStateOf(false) }
    var toolsPanelRect by remember { mutableStateOf(Rect.Zero) }
    val toolsPanelHeight by remember(
        uiState.isInEditMode,
        isToolsPanelMenuVisible,
        toolsPanelRect.height,
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
    val statusBarHeight = rememberSaveable(state) { view.getStatusBarHeight() }
    val statusBarHeightDp = density.run { statusBarHeight.toDp() }
    val toolbarMargin = statusBarHeightDp + ToolbarConstants.toolbarHeight + 8.dp
    var listSize by remember { mutableStateOf(IntSize.Zero) }
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
        .calculateBottomPadding() + 8.dp
    val listPanelPadding by animateDpAsState(
        targetValue = toolsPanelHeight,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    )

    var topEmptyTitleHeight by remember { mutableFloatStateOf(0f) }
    val showStickersPadding = uiState.isInEditMode && uiState.content.firstOrNull().isEmptyTitle()

    LaunchedEffect(focusedTitleId) {
        val title = uiState.content.findInstance<UiNoteContent.Title> { it.id == focusedTitleId }
        if (title != null) {
            snapshotFlow { title.state.selection }
                .collect { onEvent(PageEvent.OnFocusedTitleSelectionChange) }
        }
    }

    MediaSelectorBottomSheet(
        modifier = modifier.fillMaxSize(),
        state = state.bottomScaffoldState,
        openMediaViewer = { route ->
            if (isSelected) {
                onEvent(PageEvent.OnOpenMediaViewerRequest(route))
            }
        },
        onMediaSelected = { result ->
            if (isSelected) {
                onEvent(PageEvent.OnMediaSelected(result))
            }
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
                .hazeSource(hazeState)
                .verticalScroll(state.listState)
                .clickableNoRipple { onEvent(PageEvent.OnClickOutside) }
                .padding(top = toolbarMargin, bottom = navBarPadding)
                .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(8.dp))
                .padding(bottom = listPanelPadding)
                .imePadding(),
        ) {
            ContentItems(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { listSize = it },
                uiState = uiState,
                hazeState = hazeState,
                onEvent = onEvent,
                onTitleFocusChange = { id ->
                    focusedTitleId = id
                    onFocusChange()
                },
                onEmptyTitleHeightChange = { topEmptyTitleHeight = it },
            )
            if (uiState.stickers.isNotEmpty()) {
                StickersBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = StickerItem.DEFAULT_SIZE * 2)
                        .height(density.run { listSize.height.toDp() }),
                    stickers = uiState.stickers,
                    emptyTitleHeight = { if (showStickersPadding) topEmptyTitleHeight else 0f },
                    containerSize = { listSize },
                    onStickerClick = { onEvent(PageEvent.OnStickerClick(it)) },
                    onRemoveStickerClick = { onEvent(PageEvent.OnRemoveStickerClick(it)) },
                    onStickerChanged = { onEvent(PageEvent.OnStickerChanged(it)) },
                )
            }
        }
        Panel(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .onGloballyPositioned { toolsPanelRect = it.boundsInParent() }
                .applyIf(state.isVoiceRecordActive) { Modifier.zIndex(1f) },
            hazeState = hazeState,
            uiState = uiState,
            focusedTitleId = { focusedTitleId },
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
                            toolsPanelHeight = density.run { toolsPanelRect.height.toDp() },
                            toolBarHeight = toolbarMargin,
                            density = density,
                        ),
                    )
                )
            },
        )
        DimSurfaceOverlay(
            visible = state.dimSurface,
        )
        SnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            hostState = snackBarHostState,
            snackbar = { data ->
                SnackBar(
                    title = data.visuals.message,
                    icon = painterResource(uiR.drawable.ic_info),
                    tonalColor = MaterialTheme.colorScheme.tertiaryContainer,
                )
            },
        )
    }
}

@Composable
private fun ContentItems(
    uiState: PageUiState.Success,
    hazeState: HazeState,
    onTitleFocusChange: (id: String) -> Unit,
    onEmptyTitleHeightChange: (height: Float) -> Unit,
    onEvent: (event: PageEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
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
                            color = uiState.fontColor?.value,
                            fontFamily = uiState.fontFamily?.regular,
                            fontSize = uiState.fontSize.sp,
                            hint = if (index == 0) {
                                stringResource(R.string.note_title_hint_text)
                            } else {
                                stringResource(R.string.note_title_hint_write_more_here)
                            },
                            isInEditMode = uiState.isInEditMode,
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
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                            },
                            onSortingClick = { onEvent(PageEvent.OnMediaSortingClick(item.id)) },
                        )

                        is UiNoteContent.Voice -> NoteContentVoice(
                            modifier = Modifier
                                .padding(
                                    start = 8.dp,
                                    end = 8.dp,
                                    top = if (index == 0) 8.dp else 0.dp,
                                    bottom = if (nextItem.isEmptyTitle()) 0.dp else 4.dp,
                                )
                                .animatePlacementInScope(this@LookaheadScope),
                            voice = item,
                            isPayable = true,
                            isPlaying = item.id == uiState.playingVoiceId,
                            isRemovable = uiState.isInEditMode,
                            onRemoveClick = { voice ->
                                onEvent(PageEvent.OnVoiceRemoveClick(voice))
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
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
                    onTagRemoveClick = { tag ->
                        onEvent(PageEvent.OnTagRemoveClick(tag))
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
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

@Composable
private fun Panel(
    uiState: PageUiState.Success,
    hazeState: HazeState,
    focusedTitleId: () -> String?,
    onMenuVisibilityChange: (visible: Boolean) -> Unit,
    onSelectMediaClick: () -> Unit,
    onVoiceRecordStart: () -> Unit,
    onRecordComplete: (record: VoiceRecord) -> Unit,
    onVoiceRecordCancel: () -> Unit,
    onFontFamilySelected: (family: UiNoteFontFamily?) -> Unit,
    onFontColorSelected: (color: UiNoteFontColor?) -> Unit,
    onFontSizeSelected: (size: Int) -> Unit,
    onStickerSelected: (sticker: Sticker) -> Unit,
    onFontStyleClick: () -> Unit,
    onOnStickersClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = uiState.isInEditMode,
            enter = fadeIn(tween(durationMillis = ANIM_PANEL_VISIBILITY_DURATION)),
            exit = fadeOut(tween(durationMillis = ANIM_PANEL_VISIBILITY_DURATION)),
            content = {
                val focusedTitle = focusedTitleId()
                val titleState = remember(uiState.content, focusedTitle) {
                    uiState.content
                        .findInstance<UiNoteContent.Title> { it.id == focusedTitle }
                        ?.state
                }
                ActionsPanel(
                    noteId = uiState.noteId,
                    fontFamily = uiState.fontFamily,
                    fontColor = uiState.fontColor,
                    fontSize = uiState.fontSize,
                    hazeState = hazeState,
                    titleState = titleState ?: NoteTitleState(
                        fontFamily = UiNoteFontFamily.QuickSand,
                    ),
                    onMenuVisibilityChange = onMenuVisibilityChange,
                    onSelectMediaClick = onSelectMediaClick,
                    onVoiceRecordStart = onVoiceRecordStart,
                    onRecordComplete = onRecordComplete,
                    onVoiceRecordCancel = onVoiceRecordCancel,
                    onFontFamilySelected = onFontFamilySelected,
                    onFontColorSelected = onFontColorSelected,
                    onFontSizeSelected = onFontSizeSelected,
                    onFontStyleClick = onFontStyleClick,
                    onOnStickersClick = onOnStickersClick,
                    onStickerSelected = onStickerSelected,
                )
            }
        )
    }
}

@Composable
private fun DimSurfaceOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
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

private fun Modifier.clipPanel(
    toolsPanelTop: () -> Float,
): Modifier = then(
    Modifier.drawWithCache {
        val path = Path()
        val rect = size.toRect()
        val cornerRadius = CornerRadius(8.dp.toPx())
        val panelHeightPx = ToolsPanelConstants.PANEL_HEIGHT.toPx()
        path.addRoundRect(
            RoundRect(
                rect = rect.copy(bottom = toolsPanelTop() + panelHeightPx),
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
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    )
}

@Composable
private fun EmptyScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewWithBackground
@Composable
private fun SuccessScreenPreview() {
    SerenityTheme {
        SuccessScreen(
            state = rememberPageScreenState(),
            snackBarHostState = SnackbarHostState(),
            uiState = PageUiState.Success(
                noteId = "123",
                isInEditMode = false,
                tags = persistentListOf(),
                stickers = persistentListOf(),
                playingVoiceId = null,
                fontFamily = UiNoteFontFamily.QuickSand,
                fontColor = UiNoteFontColor.WHITE,
                fontSize = 15,
                content = persistentListOf(
                    UiNoteContent.Title(
                        id = "1",
                        state = NoteTitleState(
                            fontFamily = UiNoteFontFamily.QuickSand,
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
            hazeState = HazeState(),
            isSelected = true,
        )
    }
}
