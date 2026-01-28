package com.furianrt.toolspanel.internal.ui.background.container

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.toolspanel.R
import com.furianrt.toolspanel.internal.ui.background.image.ImageBackgroundContent
import com.furianrt.toolspanel.internal.ui.background.pattern.PatternBackgroundContent
import com.furianrt.toolspanel.internal.ui.background.solid.SolidBackgroundContent
import com.furianrt.toolspanel.internal.ui.common.ButtonClose
import com.furianrt.toolspanel.internal.ui.common.ButtonKeyboard
import com.furianrt.toolspanel.internal.ui.font.cachedImeHeight
import com.furianrt.uikit.components.SkipFirstEffect
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.drawLeftShadow
import com.furianrt.uikit.extensions.drawRightShadow
import com.furianrt.uikit.extensions.pxToDp
import kotlin.math.max

private const val NOTE_BACKGROUND_TAG = "note_panel_background_container"

@Composable
internal fun BackgroundTitleBar(
    noteId: String,
    noteTheme: UiNoteTheme?,
    showKeyBoardButton: Boolean,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel =
        hiltViewModel<BackgroundContainerViewModel, BackgroundContainerViewModel.Factory>(
            key = NOTE_BACKGROUND_TAG + noteId,
            creationCallback = { it.create(noteId = noteId, initialTheme = noteTheme) },
        )
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val onDoneClickState by rememberUpdatedState(onDoneClick)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                if (effect is BackgroundContainerEffect.ClosePanel) {
                    onDoneClickState()
                }
            }
    }
    when (uiState) {
        is BackgroundContainerUiState.Success -> TitleContent(
            modifier = modifier,
            uiState = uiState,
            showKeyBoardButton = showKeyBoardButton,
            onEvent = viewModel::onEvent,
        )

        is BackgroundContainerUiState.Loading -> Box(
            modifier = modifier
                .fillMaxWidth()
                .clickableNoRipple {},
        )
    }
}

@Composable
private fun TitleContent(
    uiState: BackgroundContainerUiState.Success,
    showKeyBoardButton: Boolean,
    modifier: Modifier = Modifier,
    onEvent: (event: BackgroundContainerEvent) -> Unit = {},
) {
    val showKeyBoardButtonState = remember { showKeyBoardButton }
    val listState: LazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = uiState.selectedTabIndex,
    )
    val shadowColor = MaterialTheme.colorScheme.surfaceDim

    SkipFirstEffect(uiState.selectedTabIndex) {
        listState.animateScrollToItem(index = uiState.selectedTabIndex)
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
                    onClick = { onEvent(BackgroundContainerEvent.OnKeyboardClick) },
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
                            onClick = { onEvent(BackgroundContainerEvent.OnTitleTabClick(index)) }
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
                onClick = { onEvent(BackgroundContainerEvent.OnCloseClick) },
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
    tab: BackgroundContainerUiState.Success.Tab,
    isSelected: Boolean,
    onClick: (tab: BackgroundContainerUiState.Success.Tab) -> Unit,
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
                is BackgroundContainerUiState.Success.Tab.Solid -> {
                    stringResource(R.string.background_panel_tab_solid_title)
                }

                is BackgroundContainerUiState.Success.Tab.Picture -> {
                    stringResource(R.string.background_panel_tab_picture_title)
                }

                is BackgroundContainerUiState.Success.Tab.Pattern -> {
                    stringResource(R.string.background_panel_tab_pattern_title)
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
    noteTheme: UiNoteTheme?,
    visible: Boolean,
    onThemeSelected: (theme: UiNoteTheme?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel =
        hiltViewModel<BackgroundContainerViewModel, BackgroundContainerViewModel.Factory>(
            key = NOTE_BACKGROUND_TAG + noteId,
            creationCallback = { it.create(noteId = noteId, initialTheme = noteTheme) },
        )
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val density = LocalDensity.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val imeTarget = WindowInsets.imeAnimationTarget.getBottom(density)
    val imeSource = WindowInsets.imeAnimationSource.getBottom(density)
    val isImeVisible = WindowInsets.isImeVisible
    val navigationBarsHeight = WindowInsets.navigationBars.getBottom(density)

    var imeHeight by remember { mutableStateOf(cachedImeHeight) }
    val contentHeight = imeHeight - navigationBarsHeight.pxToDp()

    val onThemeSelectedState by rememberUpdatedState(onThemeSelected)

    LaunchedEffect(imeTarget, imeSource) {
        val imeMaxHeight = max(imeTarget, imeSource)
        if (imeMaxHeight > 0) {
            imeHeight = density.run { imeMaxHeight.toDp() }
            cachedImeHeight = imeHeight
        }
    }

    if (uiState is BackgroundContainerUiState.Success) {
        SkipFirstEffect(uiState.selectedTheme) {
            onThemeSelectedState(uiState.selectedTheme)
        }
    }

    if (uiState is BackgroundContainerUiState.Success && (visible || !isImeVisible)) {
        val pagerState = rememberPagerState(
            pageCount = uiState.tabs::count,
            initialPage = uiState.selectedTabIndex,
        )

        LaunchedEffect(Unit) {
            viewModel.effect
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { effect ->
                    when (effect) {
                        is BackgroundContainerEffect.ClosePanel -> Unit
                        is BackgroundContainerEffect.ShowKeyboard -> keyboardController?.show()

                        is BackgroundContainerEffect.ScrollToPage -> {
                            pagerState.scrollToPage(effect.index)
                        }
                    }
                }
        }

        LaunchedEffect(pagerState.currentPage) {
            viewModel.onEvent(BackgroundContainerEvent.OnContentPageChange(pagerState.currentPage))
        }

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
            )
        }
    }
}

@Composable
private fun Content(
    uiState: BackgroundContainerUiState.Success,
    modifier: Modifier = Modifier,
    onEvent: (event: BackgroundContainerEvent) -> Unit = {},
    pagerState: PagerState,
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        verticalAlignment = Alignment.Top,
        pageSpacing = 16.dp,
    ) { index ->
        when (uiState.tabs[index]) {
            is BackgroundContainerUiState.Success.Tab.Solid -> SolidBackgroundContent(
                noteId = uiState.noteId,
                selectedThemeProvider = uiState.selectedThemeProvider,
                onThemeSelected = { onEvent(BackgroundContainerEvent.OnThemeSelected(it)) },
            )

            is BackgroundContainerUiState.Success.Tab.Pattern -> PatternBackgroundContent(
                noteId = uiState.noteId,
                selectedThemeProvider = uiState.selectedThemeProvider,
                onThemeSelected = { onEvent(BackgroundContainerEvent.OnThemeSelected(it)) },
            )

            is BackgroundContainerUiState.Success.Tab.Picture -> ImageBackgroundContent(
                noteId = uiState.noteId,
                selectedThemeProvider = uiState.selectedThemeProvider,
                onThemeSelected = { onEvent(BackgroundContainerEvent.OnThemeSelected(it)) },
            )
        }
    }
}
