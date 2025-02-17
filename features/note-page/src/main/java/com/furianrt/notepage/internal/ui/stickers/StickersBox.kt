package com.furianrt.notepage.internal.ui.stickers

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imeAnimationSource
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.isEmptyTitle
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private const val STICKER_ANIM_DELAY = 500L
private const val STICKER_ANIM_DURATION = 300

@Composable
internal fun StickersBox(
    noteContent: ImmutableList<UiNoteContent>,
    stickers: ImmutableList<StickerItem>,
    listState: LazyListState,
    toolbarHeight: Dp,
    onStickerClick: (sticker: StickerItem) -> Unit,
    onRemoveStickerClick: (sticker: StickerItem) -> Unit,
    onStickerChanged: (sticker: StickerItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val toolbarHeightPx = LocalDensity.current.run { toolbarHeight.toPx() }
    Box(modifier = modifier) {
        stickers.forEach { sticker ->
            key(sticker.id) {
                StickerElement(
                    noteContent = noteContent,
                    sticker = sticker,
                    toolbarHeightPx = toolbarHeightPx,
                    listState = listState,
                    onStickerClick = onStickerClick,
                    onRemoveStickerClick = onRemoveStickerClick,
                    onStickerChanged = onStickerChanged,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StickerElement(
    noteContent: ImmutableList<UiNoteContent>,
    sticker: StickerItem,
    listState: LazyListState,
    toolbarHeightPx: Float,
    onStickerChanged: (sticker: StickerItem) -> Unit,
    onStickerClick: (sticker: StickerItem) -> Unit,
    onRemoveStickerClick: (sticker: StickerItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    var stickerOffset by remember { mutableStateOf(IntOffset.Zero) }
    var stickerHeight by remember { mutableIntStateOf(0) }
    var isVisible by remember { mutableStateOf(false) }

    var prevContentCount by remember { mutableIntStateOf(noteContent.count { !it.isEmptyTitle() }) }
    LaunchedEffect(noteContent) {
        val newContentCount = noteContent.count { !it.isEmptyTitle() }
        if (newContentCount != prevContentCount) {
            val isAnchorChanged = sticker.state.anchors.any { anchor ->
                !noteContent.any { (anchor as? StickerState.Anchor.Item)?.id == it.id }
            }
            if (isAnchorChanged) {
                sticker.calculateAnchor(
                    noteContent = noteContent,
                    listState = listState,
                    toolbarHeight = toolbarHeightPx,
                    stickerOffset = stickerOffset,
                    stickerHeight = stickerHeight.toFloat(),
                    density = density,
                )
            }
            prevContentCount = newContentCount
        }
    }

    LaunchedEffect(sticker.state.anchors, noteContent) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                sticker.calculatePosition(
                    noteContent = noteContent,
                    stickerOffset = stickerOffset,
                    stickerHeight = stickerHeight,
                    toolbarHeight = toolbarHeightPx,
                    listState = listState,
                    layoutInfo = layoutInfo,
                    density = density,
                    changeStickerOffset = { stickerOffset = it },
                    changeStickerVisibility = { isVisible = it },
                )
            }
    }

    if (isVisible) {
        val valueX by animateIntAsState(
            targetValue = stickerOffset.x,
            animationSpec = tween(STICKER_ANIM_DURATION),
            label = "valueX",
        )
        val valueY by animateIntAsState(
            targetValue = stickerOffset.y,
            animationSpec = tween(STICKER_ANIM_DURATION),
            label = "valueY",
        )

        var isDragging by remember { mutableStateOf(false) }
        var animateOffset by remember { mutableStateOf(false) }

        val imeTargetBottom = WindowInsets.imeAnimationTarget.getBottom(density)
        val imeSourceBottom = WindowInsets.imeAnimationSource.getBottom(density)
        val isImeInProgress = imeTargetBottom != imeSourceBottom

        LaunchedEffect(listState.isScrollInProgress, isDragging, isImeInProgress) {
            if (listState.isScrollInProgress || isDragging || isImeInProgress) {
                animateOffset = false
            } else {
                delay(STICKER_ANIM_DELAY)
                animateOffset = true
            }
        }

        StickerScreenItem(
            modifier = modifier
                .offset {
                    if (animateOffset) {
                        IntOffset(valueX, valueY)
                    } else {
                        stickerOffset
                    }
                }
                .onSizeChanged { stickerHeight = it.height },
            item = sticker,
            onRemoveClick = onRemoveStickerClick,
            onDragged = { delta ->
                stickerOffset = IntOffset(
                    x = (stickerOffset.x + delta.x).roundToInt(),
                    y = (stickerOffset.y + delta.y).coerceAtLeast(toolbarHeightPx / 2f)
                        .roundToInt(),
                )
            },
            onDragStarted = { isDragging = true },
            onDragStopped = {
                isDragging = false
                sticker.calculateAnchor(
                    noteContent = noteContent,
                    listState = listState,
                    toolbarHeight = toolbarHeightPx,
                    stickerOffset = stickerOffset,
                    stickerHeight = stickerHeight.toFloat(),
                    density = density,
                )
                onStickerChanged(sticker)
            },
            onTransformed = { onStickerChanged(sticker) },
            onClick = onStickerClick,
        )
    }
}

private fun List<LazyListItemInfo>.findInfoForAnchorId(
    anchorIds: List<String>,
): LazyListItemInfo? {
    val list = filter { anchorIds.contains(it.key) }
    return list.getOrNull(list.lastIndex / 2)
}

private fun List<UiNoteContent>.hasSuitableContent(sticker: StickerItem): Boolean {
    val content = filter { content ->
        sticker.state.anchors.any { it is StickerState.Anchor.Item && it.id == content.id }
    }
    return content.any { !it.isEmptyTitle() }
}

private fun StickerItem.calculatePosition(
    noteContent: List<UiNoteContent>,
    stickerOffset: IntOffset,
    stickerHeight: Int,
    toolbarHeight: Float,
    listState: LazyListState,
    layoutInfo: LazyListLayoutInfo,
    density: Density,
    changeStickerOffset: (IntOffset) -> Unit,
    changeStickerVisibility: (Boolean) -> Unit,
) {
    val viewPortWidth = layoutInfo.viewportSize.width
    val firstAnchor = state.anchors.first()
    when {
        firstAnchor is StickerState.Anchor.ViewPort -> {
            val biasYOffset = density
                .run { StickerItem.STUB_HEIGHT.toPx() } * firstAnchor.biasY
            changeStickerOffset(
                IntOffset(
                    x = (viewPortWidth * firstAnchor.biasX).roundToInt(),
                    y = (biasYOffset + toolbarHeight).roundToInt(),
                )
            )
            changeStickerVisibility(true)
        }

        !noteContent.hasSuitableContent(this) -> {
            calculateAnchor(
                noteContent = noteContent,
                listState = listState,
                toolbarHeight = toolbarHeight,
                stickerOffset = stickerOffset,
                stickerHeight = stickerHeight.toFloat(),
                density = density,
            )
        }

        else -> {
            val info = layoutInfo.visibleItemsInfo.findInfoForAnchorId(
                anchorIds = state.anchors
                    .filterIsInstance<StickerState.Anchor.Item>()
                    .map(StickerState.Anchor.Item::id),
            )

            if (info != null) {
                val anchor = state.anchors.first {
                    it is StickerState.Anchor.Item && it.id == info.key
                } as StickerState.Anchor.Item

                val biasYOffset = info.size * anchor.biasY
                changeStickerOffset(
                    IntOffset(
                        x = (viewPortWidth * anchor.biasX).roundToInt(),
                        y = (biasYOffset + info.offset + toolbarHeight).roundToInt(),
                    )
                )
            }
            changeStickerVisibility(info != null)
        }
    }
}
