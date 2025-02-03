package com.furianrt.notepage.internal.ui.stickers

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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.notelistui.entities.isEmptyTitle
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.max
import kotlin.math.min

private val STUB_HEIGHT = 300.dp

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

    val anchorContent = remember(sticker.state.anchorId) {
        noteContent.find { it.id == sticker.state.anchorId }
    }

    val isEmptyTitleAnchor =
        (anchorContent as? UiNoteContent.Title)?.state?.text?.isEmpty() ?: false

    LaunchedEffect(
        noteContent,
        sticker.state.anchorId,
        sticker.state.biasX,
        sticker.state.biasY,
        isEmptyTitleAnchor,
    ) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                when {
                    sticker.state.anchorId == null -> {
                        val biasYOffset = density.run { STUB_HEIGHT.toPx() } * sticker.state.biasY
                        stickerOffset = IntOffset(
                            x = (viewPortWidth * sticker.state.biasX).toInt(),
                            y = (biasYOffset + layoutInfo.viewportStartOffset + toolbarHeightPx).toInt(),
                        )
                        isVisible = true
                    }

                    !noteContent.hasSuitableContent(sticker) || isEmptyTitleAnchor -> {
                        sticker.calculateAnchor(
                            noteContent = noteContent,
                            listState = listState,
                            toolbarHeightPx = toolbarHeightPx,
                            stickerOffset = stickerOffset,
                            density = density,
                            stickerHeight = stickerHeight,
                        )
                    }

                    else -> {
                        val info = layoutInfo.visibleItemsInfo.findInfoForAnchorId(
                            noteContent = noteContent,
                            anchorId = sticker.state.anchorId,
                        )

                        if (info != null) {
                            val biasYOffset = info.size * sticker.state.biasY
                            stickerOffset = IntOffset(
                                x = (viewPortWidth * sticker.state.biasX).toInt(),
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
                y = (stickerOffset.y + delta.y).toInt(),
            )
        }
        StickerScreenItem(
            modifier = modifier
                .offset { stickerOffset }
                .draggable2D(
                    state = draggableState,
                    onDragStopped = {
                        sticker.calculateAnchor(
                            noteContent = noteContent,
                            listState = listState,
                            toolbarHeightPx = toolbarHeightPx,
                            stickerOffset = stickerOffset,
                            density = density,
                            stickerHeight = stickerHeight,
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
    noteContent: List<UiNoteContent>,
    anchorId: String?,
): LazyListItemInfo? {
    val anchorIndex = noteContent.indexOfFirstOrNull { it.id == anchorId }
    return find { it.index == anchorIndex }
}

private fun StickerItem.calculateAnchor(
    noteContent: List<UiNoteContent>,
    listState: LazyListState,
    toolbarHeightPx: Float,
    stickerOffset: IntOffset,
    stickerHeight: Int,
    density: Density,
) {
    val viewPortHeight = listState.layoutInfo.viewportSize.height
    val viewPortWidth = listState.layoutInfo.viewportSize.width

    val availableAnchors = listState.layoutInfo.visibleItemsInfo.filter { info ->
        val content = noteContent.getOrNull(info.index)
        val emptyTitle = content is UiNoteContent.Title && content.state.text.isEmpty()
        val isTagsBlock = info.key == UiNoteTag.BLOCK_ID
        !emptyTitle && !isTagsBlock
    }

    val suitableAnchor = availableAnchors.maxByOrNull { anchor ->
        val itemStart = anchor.offset + toolbarHeightPx.toInt()
        val itemEnd = itemStart + anchor.size
        val visibleStart = max(0, min(viewPortHeight, itemStart))
        val visibleEnd = max(0, min(viewPortHeight, itemEnd))
        val stickerBottom = stickerOffset.y + stickerHeight
        maxOf(0, min(stickerBottom, visibleEnd) - max(stickerOffset.y, visibleStart))
    }

    state.biasX = stickerOffset.x / viewPortWidth.toFloat()
    if (suitableAnchor != null) {
        state.anchorId = noteContent[suitableAnchor.index].id
        state.biasY =
            (stickerOffset.y - suitableAnchor.offset - toolbarHeightPx) / suitableAnchor.size.toFloat()

    } else {
        state.anchorId = null
        state.biasY = stickerOffset.y / density.run { STUB_HEIGHT.toPx() }
    }
}

private fun List<UiNoteContent>.hasSuitableContent(sticker: StickerItem): Boolean {
    val content = find { it.id == sticker.state.anchorId }
    return content != null && !content.isEmptyTitle()
}
