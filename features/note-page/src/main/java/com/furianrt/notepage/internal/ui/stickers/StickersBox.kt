package com.furianrt.notepage.internal.ui.stickers

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.furianrt.core.hasItem
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.isEmptyTitle
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay

private const val STICKER_ANIM_DELAY = 500L
private const val STICKER_ANIM_DURATION = 250

@Composable
internal fun StickersBox(
    noteContent: ImmutableList<UiNoteContent>,
    stickers: ImmutableList<StickerItem>,
    listState: LazyListState,
    toolbarHeight: Dp,
    onRemoveStickerClick: (sticker: StickerItem) -> Unit,
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
                    onRemoveStickerClick = onRemoveStickerClick,
                )
            }
        }
    }
}

@Composable
private fun StickerElement(
    noteContent: ImmutableList<UiNoteContent>,
    sticker: StickerItem,
    listState: LazyListState,
    toolbarHeightPx: Float,
    onRemoveStickerClick: (sticker: StickerItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val viewPortWidth by remember {
        derivedStateOf { listState.layoutInfo.viewportSize.width.toFloat() }
    }
    var stickerOffset by remember { mutableStateOf(IntOffset.Zero) }
    var stickerHeight by remember { mutableIntStateOf(0) }
    var isVisible by remember { mutableStateOf(false) }

    var prevContentCount by remember { mutableIntStateOf(noteContent.count { !it.isEmptyTitle() }) }
    LaunchedEffect(noteContent) {
        val newContentCount = noteContent.count { !it.isEmptyTitle() }
        if (newContentCount != prevContentCount) {
            sticker.calculateAnchor(
                noteContent = noteContent,
                listState = listState,
                toolbarHeight = toolbarHeightPx,
                stickerOffset = stickerOffset,
                stickerHeight = stickerHeight.toFloat(),
                density = density,
            )
            prevContentCount = newContentCount
        }
    }

    LaunchedEffect(sticker.state.anchors) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val firstAnchor = sticker.state.anchors.first()
                when {
                    firstAnchor is StickerState.Anchor.ViewPort -> {
                        val biasYOffset = density
                            .run { StickerItem.STUB_HEIGHT.toPx() } * firstAnchor.biasY
                        stickerOffset = IntOffset(
                            x = (viewPortWidth * firstAnchor.biasX).toInt(),
                            y = (biasYOffset + toolbarHeightPx).toInt(),
                        )
                        isVisible = true
                    }

                    !noteContent.hasSuitableContent(sticker) -> {
                        sticker.calculateAnchor(
                            noteContent = noteContent,
                            listState = listState,
                            toolbarHeight = toolbarHeightPx,
                            stickerOffset = stickerOffset,
                            stickerHeight = stickerHeight.toFloat(),
                            density = density,
                        )
                    }

                    else -> {
                        val info = layoutInfo.visibleItemsInfo.findInfoForAnchorId(
                            anchorIds = sticker.state.anchors
                                .filterIsInstance<StickerState.Anchor.Item>()
                                .map(StickerState.Anchor.Item::id),
                            stickerOffset = stickerOffset.y,
                            stickerHeight = stickerHeight,
                        )

                        if (info != null) {
                            val anchor = sticker.state.anchors.first {
                                it is StickerState.Anchor.Item && it.id == info.key
                            } as StickerState.Anchor.Item

                            val biasYOffset = info.size * anchor.biasY
                            stickerOffset = IntOffset(
                                x = (viewPortWidth * anchor.biasX).toInt(),
                                y = (biasYOffset + info.offset + toolbarHeightPx).toInt(),
                            )
                        }

                        isVisible = info != null
                    }
                }
            }
    }


    if (isVisible) {
        val draggableState = rememberDraggable2DState { delta ->
            stickerOffset = IntOffset(
                x = (stickerOffset.x + delta.x).toInt(),
                y = (stickerOffset.y + delta.y).coerceAtLeast(toolbarHeightPx / 2).toInt(),
            )
        }
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

        LaunchedEffect(listState.isScrollInProgress, isDragging) {
            if (listState.isScrollInProgress || isDragging) {
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
                .draggable2D(
                    state = draggableState,
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
                    },
                )
                .onSizeChanged { stickerHeight = it.height },
            item = sticker,
            onRemoveClick = onRemoveStickerClick,
        )
    }
}

private fun List<LazyListItemInfo>.findInfoForAnchorId(
    anchorIds: List<String>,
    stickerOffset: Int,
    stickerHeight: Int,
): LazyListItemInfo? {
    val list = filter { anchorIds.contains(it.key) }
    return list.maxByOrNull { info ->
        val stickerBottom = stickerOffset + stickerHeight
        val infoBottom = info.offset + info.size
        maxOf(0, minOf(stickerBottom, infoBottom) - maxOf(stickerOffset, info.offset))
    }
}

private fun List<UiNoteContent>.hasSuitableContent(sticker: StickerItem): Boolean {
    val content = filter { content ->
        sticker.state.anchors.hasItem { it is StickerState.Anchor.Item && it.id == content.id }
    }
    return content.any { !it.isEmptyTitle() }
}
