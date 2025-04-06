package com.furianrt.toolspanel.internal.ui.stickers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.core.DispatchersProvider
import com.furianrt.toolspanel.api.entities.Sticker
import com.furianrt.toolspanel.internal.domain.StickersHolder
import com.furianrt.toolspanel.internal.entities.StickerPack
import com.furianrt.toolspanel.internal.ui.font.cachedImeHeight
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.drawLeftShadow
import com.furianrt.uikit.extensions.drawTopInnerShadow
import com.furianrt.uikit.extensions.visibleItemsInfo
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.math.max
import com.furianrt.uikit.R as uiR

@Composable
internal fun StickersTitleBar(
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: StickersViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is StickersPanelEffect.ClosePanel -> onDoneClick()
                    is StickersPanelEffect.ScrollContentToIndex -> Unit
                    is StickersPanelEffect.SelectSticker -> Unit
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
    uiState: StickersPanelUiState,
    modifier: Modifier = Modifier,
    onEvent: (event: StickersPanelEvent) -> Unit = {},
) {
    val listState: LazyListState = rememberLazyListState()
    LaunchedEffect(uiState.selectedPackIndex) {
        delay(200)
        val visibleIndexes = listState.layoutInfo
            .visibleItemsInfo(itemVisiblePercentThreshold = 100f)
            .map(LazyListItemInfo::index)
        if (uiState.selectedPackIndex !in visibleIndexes) {
            listState.animateScrollToItem(uiState.selectedPackIndex)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickableNoRipple {},
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LazyRow(
            modifier = Modifier.weight(1f),
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            items(
                count = uiState.packs.count(),
                // key = { uiState.packs[it].id }
            ) { index ->
                TitleItem(
                    pack = uiState.packs[index],
                    isSelected = uiState.selectedPackIndex == index,
                    onClick = { onEvent(StickersPanelEvent.OnTitleStickerPackClick(index)) },
                )
            }
        }
        ButtonClose(
            modifier = Modifier.drawBehind {
                if (listState.canScrollForward) {
                    drawLeftShadow(elevation = 2.dp)
                }
            },
            onClick = { onEvent(StickersPanelEvent.OnCloseClick) },
        )
    }
}

@Composable
private fun TitleItem(
    pack: StickerPack,
    isSelected: Boolean,
    onClick: (pack: StickerPack) -> Unit,
    modifier: Modifier = Modifier,
) {
    val underlineColor = MaterialTheme.colorScheme.primaryContainer
    Box(
        modifier = modifier.size(36.dp)
    ) {
        IconButton(
            modifier = Modifier.fillMaxSize(),
            onClick = { onClick(pack) },
        ) {
            Icon(
                painter = painterResource(pack.icon),
                tint = Color.Unspecified,
                contentDescription = null,
            )
        }
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

@Composable
private fun ButtonClose(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(uiR.drawable.ic_exit),
            tint = Color.Unspecified,
            contentDescription = null,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StickersContent(
    visible: Boolean,
    onStickerSelected: (sticker: Sticker) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: StickersViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val density = LocalDensity.current
    val isImeVisible = WindowInsets.isImeVisible
    val imeTarget = WindowInsets.imeAnimationTarget.getBottom(density)
    val imeSource = WindowInsets.imeAnimationSource.getBottom(density)
    val navigationBarsHeight = WindowInsets.navigationBars.getBottom(density)

    var imeHeight by remember { mutableStateOf(cachedImeHeight) }
    val contentHeight = imeHeight - density.run { navigationBarsHeight.toDp() }

    val pagerState = rememberPagerState(
        pageCount = uiState.packs::count,
        initialPage = uiState.selectedPackIndex,
    )
    val pageScreensStates = remember { mutableStateMapOf<Int, LazyGridState>() }

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is StickersPanelEffect.ClosePanel -> Unit
                    is StickersPanelEffect.SelectSticker -> {
                        onStickerSelected(effect.sticker)
                    }

                    is StickersPanelEffect.ScrollContentToIndex -> {
                        pagerState.scrollToPage(effect.index)
                    }
                }
            }
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onEvent(StickersPanelEvent.OnStickersPageChange(pagerState.currentPage))
    }

    LaunchedEffect(imeTarget, imeSource) {
        val imeMaxHeight = max(imeTarget, imeSource)
        if (imeMaxHeight > 0) {
            imeHeight = density.run { imeMaxHeight.toDp() }
            cachedImeHeight = imeHeight
        }
    }

    if (isImeVisible && visible) {
        Content(
            modifier = modifier.height(contentHeight),
            uiState = uiState,
            onEvent = viewModel::onEvent,
            pagerState = pagerState,
            listStateProvider = { pageScreensStates.getOrPut(it) { rememberLazyGridState() } },
        )
    } else {
        AnimatedVisibility(
            visible = visible,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            Content(
                modifier = modifier.height(contentHeight),
                uiState = uiState,
                onEvent = viewModel::onEvent,
                pagerState = pagerState,
                listStateProvider = { pageScreensStates.getOrPut(it) { rememberLazyGridState() } },
            )
        }
    }
}

@Composable
private fun Content(
    uiState: StickersPanelUiState,
    modifier: Modifier = Modifier,
    onEvent: (event: StickersPanelEvent) -> Unit = {},
    pagerState: PagerState = rememberPagerState(pageCount = uiState.packs::count),
    listStateProvider: @Composable (index: Int) -> LazyGridState,
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
    ) { index ->
        ContentPage(
            stickers = uiState.packs[index].stickers,
            onEvent = onEvent,
            listState = listStateProvider(index)
        )
    }
}

@Composable
private fun ContentPage(
    stickers: ImmutableList<Sticker>,
    modifier: Modifier = Modifier,
    onEvent: (event: StickersPanelEvent) -> Unit,
    listState: LazyGridState,
) {
    val showShadow by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0
        }
    }
    LazyVerticalGrid(
        modifier = modifier
            .fillMaxWidth()
            .clickableNoRipple {}
            .drawBehind {
                if (showShadow) {
                    drawTopInnerShadow(elevation = 2.dp)
                }
            },
        state = listState,
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
    ) {
        items(
            count = stickers.count(),
            key = { stickers[it].id },
        ) { index ->
            ContentItem(
                sticker = stickers[index],
                onClick = { onEvent(StickersPanelEvent.OnStickerSelected(it)) },
            )
        }
    }
}

@Composable
private fun ContentItem(
    sticker: Sticker,
    onClick: (sticker: Sticker) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick(sticker) }
            .padding(horizontal = 2.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(sticker.icon),
            tint = Color.Unspecified,
            contentDescription = null,
        )
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
    SerenityTheme {
        TitleContent(
            uiState = StickersPanelUiState(
                packs = runBlocking { stickersHolderMock().getStickersPacks() }
            ),
        )
    }
}

@Composable
@PreviewWithBackground
private fun ContentPreview() {
    val listState = rememberLazyGridState()
    SerenityTheme {
        Content(
            uiState = StickersPanelUiState(
                packs = runBlocking { stickersHolderMock().getStickersPacks() },
            ),
            listStateProvider = { listState },
        )
    }
}

private fun stickersHolderMock() = StickersHolder(
    dispatchers = object : DispatchersProvider {
        override val default: CoroutineDispatcher = Dispatchers.Main
        override val io: CoroutineDispatcher = Dispatchers.Main
        override val main: CoroutineDispatcher = Dispatchers.Main
        override val unconfined: CoroutineDispatcher = Dispatchers.Main
    },
)
