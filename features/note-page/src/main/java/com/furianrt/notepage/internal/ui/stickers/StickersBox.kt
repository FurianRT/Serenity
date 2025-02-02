package com.furianrt.notepage.internal.ui.stickers

import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.IntSize
import com.furianrt.core.hasItem
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import kotlinx.collections.immutable.ImmutableList

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
    BoxWithConstraints(
        modifier = modifier,
    ) {
        stickers.forEach { sticker ->
            key(sticker.id) {
                StickerElement(
                    noteContent = noteContent,
                    sticker = sticker,
                    toolbarHeightPx = toolbarHeightPx,
                    listState = listState,
                    parentWidthPx = LocalDensity.current.run { maxWidth.toPx() },
                    onRemoveStickerClick = onRemoveStickerClick,
                    onDragStopped = {
                        sticker.calculateAnchor(noteContent, listState, toolbarHeightPx)
                    },
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
    parentWidthPx: Float,
    onRemoveStickerClick: (sticker: StickerItem) -> Unit,
    onDragStopped: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var anchorOffset by remember { mutableFloatStateOf(0f) }
    var anchorSize by remember { mutableIntStateOf(0) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(noteContent, sticker.state.anchorId) {
        if (sticker.state.anchorId != null && !noteContent.hasSuitableBlock()) {

        }

        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                if (!noteContent.hasSuitableBlock()) {


                    anchorOffset = toolbarHeightPx
                    anchorSize = layoutInfo.viewportSize.height
                }


                val info = layoutInfo.visibleItemsInfo.findInfoForAnchorId(
                    noteContent = noteContent,
                    anchorId = sticker.state.anchorId,
                )


                val hasAnchorContent = noteContent.hasItem { it.id == sticker.state.anchorId }
                if (!hasAnchorContent && !noteContent.hasSuitableBlock()) {

                } else {
                    anchorOffset = (info?.offset ?: 0) + toolbarHeightPx
                    anchorSize = info?.size ?: 0
                }

                isVisible = info != null || sticker.state.anchorId == null
            }
    }

    if (isVisible) {
        var stickerSize by remember { mutableStateOf(IntSize.Zero) }
        val draggableState = rememberDraggable2DState { delta ->
            sticker.state.biasX += delta.x / parentWidthPx.coerceAtLeast(1f)
            sticker.state.biasY += delta.y / anchorSize.coerceAtLeast(1)
        }
        StickerScreenItem(
            modifier = modifier
                .offset {
                    val biasX = sticker.state.biasX
                    val biasY = sticker.state.biasY
                    val stickerCenterX = stickerSize.width / 2
                    val stickerCenterY = stickerSize.height / 2
                    IntOffset(
                        x = (parentWidthPx * biasX - stickerCenterX).toInt(),
                        y = (anchorOffset + anchorSize * biasY - stickerCenterY).toInt(),
                    )
                }
                .draggable2D(
                    state = draggableState,
                    onDragStopped = { onDragStopped() },
                )
                .onSizeChanged { stickerSize = it },
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
    noteContent: ImmutableList<UiNoteContent>,
    listState: LazyListState,
    toolbarHeightPx: Float,
) {
    val biasX = state.biasX
    val biasY = state.biasY
    val anchorId = state.anchorId

    val oldAnchorInfo = listState.layoutInfo.visibleItemsInfo.findInfoForAnchorId(
        noteContent = noteContent,
        anchorId = anchorId,
    )!!

    val stickerOffset = oldAnchorInfo.offset * biasY

}

private fun List<UiNoteContent>.hasSuitableBlock(): Boolean {
    return any { it !is UiNoteContent.Title || it.state.text.isNotEmpty() }
}
