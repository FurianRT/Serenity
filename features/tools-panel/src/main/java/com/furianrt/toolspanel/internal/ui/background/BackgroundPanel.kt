package com.furianrt.toolspanel.internal.ui.background

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeAnimationSource
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.toolspanel.R
import com.furianrt.toolspanel.internal.domain.NoteBackgroundHolder
import com.furianrt.toolspanel.internal.ui.common.ButtonClose
import com.furianrt.toolspanel.internal.ui.common.ButtonKeyboard
import com.furianrt.toolspanel.internal.ui.font.cachedImeHeight
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.extensions.drawLeftShadow
import com.furianrt.uikit.extensions.drawRightShadow
import com.furianrt.uikit.extensions.drawTopInnerShadow
import com.furianrt.uikit.extensions.pxToDp
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlin.math.max

private const val NOTE_BACKGROUND_TAG = "note_panel_background"

@Composable
internal fun BackgroundTitleBar(
    noteId: String,
    noteBackground: UiNoteBackground?,
    showKeyBoardButton: Boolean,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<BackgroundViewModel, BackgroundViewModel.Factory>(
        key = NOTE_BACKGROUND_TAG + noteId,
        creationCallback = { it.create(initialBackground = noteBackground) },
    )
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val onDoneClickState by rememberUpdatedState(onDoneClick)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                if (effect is BackgroundPanelEffect.ClosePanel) {
                    onDoneClickState()
                }
            }
    }

    TitleContent(
        modifier = modifier,
        uiState = uiState,
        showKeyBoardButton = showKeyBoardButton,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun TitleContent(
    uiState: BackgroundPanelUiState,
    showKeyBoardButton: Boolean,
    modifier: Modifier = Modifier,
    onEvent: (event: BackgroundPanelEvent) -> Unit = {},
) {
    val showKeyBoardButtonState = remember { showKeyBoardButton }
    val listState: LazyListState = rememberLazyListState()
    val shadowColor = MaterialTheme.colorScheme.surfaceDim

    var isFirstComposition by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(uiState.selectedTabIndex) {
        if (isFirstComposition) {
            isFirstComposition = false
            listState.scrollToItem(index = uiState.selectedTabIndex)
        } else {
            listState.animateScrollToItem(index = uiState.selectedTabIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickableNoRipple {},
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showKeyBoardButtonState) {
                ButtonKeyboard(
                    modifier = Modifier.drawBehind {
                        if (listState.canScrollBackward) {
                            drawRightShadow(color = shadowColor, elevation = 1.dp)
                        }
                    },
                    onClick = { onEvent(BackgroundPanelEvent.OnKeyboardClick) },
                )
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = if (showKeyBoardButtonState) {
                    Alignment.Center
                } else {
                    Alignment.CenterStart
                },
            ) {
                LazyRow(
                    state = listState,
                    contentPadding = PaddingValues(
                        horizontal = if (showKeyBoardButtonState) 4.dp else 16.dp,
                    )
                ) {
                    itemsIndexed(items = uiState.tabs) { index, tab ->
                        TabItem(
                            tab = tab,
                            isSelected = uiState.selectedTabIndex == index,
                            onClick = { onEvent(BackgroundPanelEvent.OnTitleTabClick(index)) }
                        )
                        if (index != uiState.tabs.lastIndex) {
                            VerticalDivider(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .height(24.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                            )
                        }
                    }
                }
            }
            ButtonClose(
                modifier = Modifier.drawBehind {
                    if (listState.canScrollForward) {
                        drawLeftShadow(color = shadowColor)
                    }
                },
                onClick = { onEvent(BackgroundPanelEvent.OnCloseClick) },
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.BottomCenter),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

@Composable
private fun TabItem(
    tab: BackgroundPanelUiState.Tab,
    isSelected: Boolean,
    onClick: (tab: BackgroundPanelUiState.Tab) -> Unit,
    modifier: Modifier = Modifier,
) {
    var underlineWidth by remember { mutableIntStateOf(0) }
    Box(
        modifier = modifier
            .onSizeChanged { underlineWidth = it.width }
            .clickableNoRipple { onClick(tab) },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            text = when (tab) {
                is BackgroundPanelUiState.Tab.All -> {
                    stringResource(R.string.background_panel_tab_all_title)
                }

                is BackgroundPanelUiState.Tab.Dark -> {
                    stringResource(R.string.background_panel_tab_dark_title)
                }

                is BackgroundPanelUiState.Tab.Light -> {
                    stringResource(R.string.background_panel_tab_light_title)
                }
            },
            style = MaterialTheme.typography.bodyMedium,
        )
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = isSelected,
        ) {
            Box(
                modifier = Modifier
                    .width(underlineWidth.pxToDp() - 16.dp)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun BackgroundContent(
    noteId: String,
    noteBackground: UiNoteBackground?,
    visible: Boolean,
    onBackgroundSelected: (item: UiNoteBackground?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<BackgroundViewModel, BackgroundViewModel.Factory>(
        key = NOTE_BACKGROUND_TAG + noteId,
        creationCallback = { it.create(initialBackground = noteBackground) },
    )
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val density = LocalDensity.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val imeTarget = WindowInsets.imeAnimationTarget.getBottom(density)
    val imeSource = WindowInsets.imeAnimationSource.getBottom(density)
    val isImeVisible = WindowInsets.isImeVisible
    val navigationBarsHeight = WindowInsets.navigationBars.getBottom(density)

    var imeHeight by remember { mutableStateOf(cachedImeHeight) }
    val contentHeight = imeHeight - navigationBarsHeight.pxToDp()

    val pagerState = rememberPagerState(
        pageCount = uiState.tabs::count,
        initialPage = uiState.selectedTabIndex,
    )
    val contentPageStates = remember { mutableStateMapOf<Int, LazyGridState>() }

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is BackgroundPanelEffect.ClosePanel -> Unit
                    is BackgroundPanelEffect.ShowKeyboard -> keyboardController?.show()
                    is BackgroundPanelEffect.SelectBackground -> onBackgroundSelected(effect.item)
                    is BackgroundPanelEffect.ScrollContentToIndex -> {
                        pagerState.scrollToPage(effect.index)
                    }
                }
            }
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onEvent(BackgroundPanelEvent.OnContentPageChange(pagerState.currentPage))
    }

    LaunchedEffect(imeTarget, imeSource) {
        val imeMaxHeight = max(imeTarget, imeSource)
        if (imeMaxHeight > 0) {
            imeHeight = density.run { imeMaxHeight.toDp() }
            cachedImeHeight = imeHeight
        }
    }

    if (visible || !isImeVisible) {
        AnimatedVisibility(
            modifier = modifier,
            visible = visible,
            enter = if (isImeVisible) {
                EnterTransition.None
            } else {
                expandVertically(expandFrom = Alignment.Top)
            },
            exit = if (isImeVisible) {
                ExitTransition.None
            } else {
                shrinkVertically(shrinkTowards = Alignment.Top)
            },
        ) {
            Content(
                modifier = Modifier.height(contentHeight),
                uiState = uiState,
                onEvent = viewModel::onEvent,
                pagerState = pagerState,
                listStateProvider = { contentPageStates.getOrPut(it) { rememberLazyGridState() } },
            )
        }
    }
}

@Composable
private fun Content(
    uiState: BackgroundPanelUiState,
    modifier: Modifier = Modifier,
    onEvent: (event: BackgroundPanelEvent) -> Unit = {},
    pagerState: PagerState = rememberPagerState(pageCount = uiState.tabs::count),
    listStateProvider: @Composable (index: Int) -> LazyGridState,
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
    ) { index ->
        ContentPage(
            items = uiState.tabs[index].items,
            selectedItem = uiState.selectedBackground,
            onEvent = onEvent,
            listState = listStateProvider(index)
        )
    }
}

@Composable
private fun ContentPage(
    items: List<UiNoteBackground>,
    selectedItem: UiNoteBackground?,
    modifier: Modifier = Modifier,
    onEvent: (event: BackgroundPanelEvent) -> Unit,
    listState: LazyGridState,
) {
    val showShadow by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0
        }
    }
    val shadowColor = MaterialTheme.colorScheme.surfaceDim
    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .clickableNoRipple {}
            .drawBehind {
                if (showShadow) {
                    drawTopInnerShadow(color = shadowColor)
                }
            },
        state = listState,
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
    ) {
        item {
            ClearItem(
                onClick = { onEvent(BackgroundPanelEvent.OnClearBackgroundClick) },
            )
        }
        itemsIndexed(items = items) { _, item ->
            BackgroundItem(
                item = item,
                isSelected = item.id == selectedItem?.id,
                onClick = { onEvent(BackgroundPanelEvent.OnBackgroundSelected(it)) },
            )
        }
    }
}

@Composable
private fun BackgroundItem(
    item: UiNoteBackground,
    isSelected: Boolean,
    onClick: (item: UiNoteBackground) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .applyIf(isSelected) {
                Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(16.dp),
                    )
                    .padding(3.dp)
            }
            .clickableNoRipple { onClick(item) },
    ) {
        val color = (item as UiNoteBackground.Solid).colorScheme.surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = color,
                    shape = RoundedCornerShape(if (isSelected) 14.dp else 16.dp),
                )
        )
    }
}

@Composable
private fun ClearItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .alpha(0.3f)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp),
            )
            .clip(RoundedCornerShape(16.dp))
            .clickableWithScaleAnim(onClick = onClick, maxScale = 1.2f),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_background_clear),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
        )
    }
}

@Composable
@PreviewWithBackground
private fun PanelPreview() {
    val holder = NoteBackgroundHolder()
    SerenityTheme {
        TitleContent(
            uiState = BackgroundPanelUiState(
                tabs = listOf(
                    BackgroundPanelUiState.Tab.All(items = holder.getDarkBackgrounds()),
                    BackgroundPanelUiState.Tab.Dark(items = emptyList()),
                    BackgroundPanelUiState.Tab.Light(items = emptyList()),
                ),
                selectedTabIndex = 0,
                selectedBackground = holder.getDarkBackgrounds().first(),
            ),
            showKeyBoardButton = false,
        )
    }
}

@Composable
@PreviewWithBackground
private fun ContentPreview() {
    val listState = rememberLazyGridState()
    val holder = NoteBackgroundHolder()
    SerenityTheme {
        Content(
            uiState = BackgroundPanelUiState(
                tabs = listOf(
                    BackgroundPanelUiState.Tab.All(items = holder.getDarkBackgrounds()),
                    BackgroundPanelUiState.Tab.Dark(items = emptyList()),
                    BackgroundPanelUiState.Tab.Light(items = emptyList()),
                ),
                selectedTabIndex = 0,
                selectedBackground = holder.getDarkBackgrounds().first(),
            ),
            listStateProvider = { listState },
        )
    }
}
