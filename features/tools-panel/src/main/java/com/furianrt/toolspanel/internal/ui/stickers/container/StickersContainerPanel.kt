package com.furianrt.toolspanel.internal.ui.stickers.container

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeAnimationSource
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import coil3.compose.AsyncImage
import com.furianrt.domain.repositories.StickersRepository
import com.furianrt.mediaselector.api.MediaSelectorState
import com.furianrt.toolspanel.R
import com.furianrt.toolspanel.api.ToolsPanelConstants
import com.furianrt.toolspanel.api.entities.Sticker
import com.furianrt.toolspanel.internal.domain.StickersHolder
import com.furianrt.toolspanel.internal.ui.common.ButtonClose
import com.furianrt.toolspanel.internal.ui.common.ButtonKeyboard
import com.furianrt.toolspanel.internal.ui.font.cachedImeHeight
import com.furianrt.toolspanel.internal.ui.stickers.custom.CustomStickersPanel
import com.furianrt.toolspanel.internal.ui.stickers.extensions.toContainerPack
import com.furianrt.toolspanel.internal.ui.stickers.regular.RegularStickersPanel
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.drawLeftShadow
import com.furianrt.uikit.extensions.drawRightShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.max

private const val NOTE_STICKERS_TAG = "note_panel_stickers_container"
private val TITLE_LIST_ITEM_SIZE = 36.dp

@Composable
internal fun StickersTitleBar(
    noteId: String,
    requestTitleFocus: () -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<StickersContainerViewModel, StickersContainerViewModel.Factory>(
        key = NOTE_STICKERS_TAG + noteId,
        creationCallback = { it.create(noteId = noteId) },
    )
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val keyboardController = LocalSoftwareKeyboardController.current

    val onDoneClickState by rememberUpdatedState(onDoneClick)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                if (effect is StickersContainerEffect.ClosePanel) {
                    onDoneClickState()
                }
                if (effect is StickersContainerEffect.ShowKeyboard) {
                    requestTitleFocus()
                    keyboardController?.show()
                }
            }
    }

    TitleContent(
        modifier = modifier,
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun TitleContent(
    uiState: StickersContainerUiState,
    modifier: Modifier = Modifier,
    onEvent: (event: StickersContainerEvent) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = uiState.pagerState.currentPage,
        pageCount = { uiState.packs.size },
    )
    val shadowColor = MaterialTheme.colorScheme.surfaceDim

    LaunchedEffect(uiState.pagerState, pagerState) {
        snapshotFlow { uiState.pagerState.targetPage }
            .collectLatest {
                pagerState.animateScrollToPage(
                    page = uiState.pagerState.targetPage,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
            }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickableNoRipple {},
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ButtonKeyboard(
                modifier = Modifier.drawBehind {
                    if (pagerState.canScrollBackward) {
                        drawRightShadow(
                            color = shadowColor,
                            elevation = 1.dp,
                        )
                    }
                },
                onClick = { onEvent(StickersContainerEvent.OnKeyboardClick) },
            )

            HorizontalPager(
                modifier = Modifier.weight(1f),
                state = pagerState,
                pageSpacing = 12.dp,
                pageSize = PageSize.Fixed(TITLE_LIST_ITEM_SIZE),
                snapPosition = SnapPosition.Center,
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    pagerSnapDistance = PagerSnapDistance.atMost(5),
                ),
                contentPadding = PaddingValues(
                    horizontal = 4.dp,
                )
            ) { page ->
                TitleItem(
                    pack = uiState.packs[page],
                    isSelected = uiState.pagerState.currentPage == page,
                    onClick = { onEvent(StickersContainerEvent.OnTitleStickerPackClick(page)) },
                )
            }
            ButtonClose(
                modifier = Modifier.drawBehind {
                    if (pagerState.canScrollForward) {
                        drawLeftShadow(color = shadowColor)
                    }
                },
                onClick = { onEvent(StickersContainerEvent.OnCloseClick) },
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
private fun TitleItem(
    pack: StickersContainerUiState.Pack,
    isSelected: Boolean,
    onClick: (pack: StickersContainerUiState.Pack) -> Unit,
    modifier: Modifier = Modifier,
) {
    val underlineColor = MaterialTheme.colorScheme.primaryContainer
    Box(modifier = modifier.size(TITLE_LIST_ITEM_SIZE)) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .clickableNoRipple { onClick(pack) }
                .align(Alignment.CenterEnd)
                .applyIf(pack is StickersContainerUiState.Pack.Custom) {
                    Modifier.padding(vertical = 4.dp)
                },
            model = pack.icon,
            contentDescription = null,
            colorFilter = if (pack is StickersContainerUiState.Pack.Custom) {
                ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            } else {
                null
            }
        )
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = isSelected,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.6f)
                    .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                    .drawSelection(underlineColor)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StickersContent(
    noteId: String,
    visible: Boolean,
    onStickerSelected: (sticker: Sticker) -> Unit,
    openMediaSelector: (params: MediaSelectorState.Params) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<StickersContainerViewModel, StickersContainerViewModel.Factory>(
        key = NOTE_STICKERS_TAG + noteId,
        creationCallback = { it.create(noteId = noteId) },
    )
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val onStickerSelectedState by rememberUpdatedState(onStickerSelected)
    val openMediaSelectorState by rememberUpdatedState(openMediaSelector)

    val density = LocalDensity.current

    val imeTarget = WindowInsets.imeAnimationTarget.getBottom(density)
    val imeSource = WindowInsets.imeAnimationSource.getBottom(density)
    val isImeVisible = WindowInsets.isImeVisible
    val navigationBarsHeight = WindowInsets.navigationBars.getBottom(density)

    var imeHeight by remember { mutableStateOf(cachedImeHeight) }
    val contentHeight = imeHeight - density.run { navigationBarsHeight.toDp() }

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is StickersContainerEffect.ClosePanel -> Unit
                    is StickersContainerEffect.ShowKeyboard -> Unit
                    is StickersContainerEffect.ScrollContentToIndex -> {
                        uiState.pagerState.animateScrollToPage(effect.index)
                    }

                    is StickersContainerEffect.SelectSticker -> {
                        onStickerSelectedState(effect.sticker)
                    }

                    is StickersContainerEffect.OpenMediaSelector -> {
                        openMediaSelectorState(effect.params)
                    }
                }
            }
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
            )
        }
    }
}

@Composable
private fun Content(
    uiState: StickersContainerUiState,
    onEvent: (event: StickersContainerEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        modifier = modifier,
        state = uiState.pagerState,
    ) { index ->
        when (val pack = uiState.packs[index]) {
            is StickersContainerUiState.Pack.Regular -> RegularStickersPanel(
                noteId = uiState.noteId,
                packId = pack.id,
                onStickerSelected = { onEvent(StickersContainerEvent.OnStickerSelected(it)) },
            )

            is StickersContainerUiState.Pack.Custom -> CustomStickersPanel(
                noteId = uiState.noteId,
                onStickerSelected = { onEvent(StickersContainerEvent.OnStickerSelected(it)) },
                openMediaSelector = {
                    onEvent(StickersContainerEvent.OnOpenMediaSelectorRequest(it))
                },
            )
        }
    }
}

private fun Modifier.drawSelection(
    color: Color,
): Modifier = then(
    Modifier.drawWithCache {
        val strokeWidth = 2.dp.toPx()
        val strokeStart = Offset(x = 0f, y = size.height - strokeWidth)
        val strokeEnd = Offset(x = size.width, y = size.height - strokeWidth)
        val gradientOffset = Offset.Zero
        val gradientBrush = Brush.verticalGradient(
            colors = listOf(Color.Transparent, color),
            startY = 0f,
            endY = size.height,
        )
        val path = Path()
        val cornerRadius = CornerRadius(16.dp.toPx())
        path.addRoundRect(
            RoundRect(
                rect = Rect(gradientOffset, size),
                topLeft = cornerRadius,
                topRight = cornerRadius,
            )
        )
        onDrawWithContent {
            drawContent()
            clipPath(path) {
                drawRect(
                    topLeft = gradientOffset,
                    size = size,
                    brush = gradientBrush,
                    alpha = 0.5f,
                )
                drawLine(
                    color = color,
                    start = strokeStart,
                    end = strokeEnd,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
            }
        }
    },
)

@Composable
@PreviewWithBackground
private fun PanelPreview() {
    val packs = StickersHolder(StickersRepository.mock()).getStickersPacks()
    SerenityTheme {
        TitleContent(
            modifier = Modifier.height(ToolsPanelConstants.PANEL_HEIGHT),
            uiState = StickersContainerUiState(
                noteId = "",
                packs = buildList {
                    add(
                        StickersContainerUiState.Pack.Custom(
                            icon = R.drawable.ic_custom_sticker,
                        )
                    )
                    addAll(packs.map { it.toContainerPack() })
                },
                pagerState = rememberPagerState(
                    pageCount = { packs.size },
                    initialPage = 1,
                )
            ),
            onEvent = {},
        )
    }
}
