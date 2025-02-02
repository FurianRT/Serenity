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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.absoluteValue
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
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(noteContent, sticker.state.anchorId, sticker.state.biasX, sticker.state.biasY) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val anchorId = sticker.state.anchorId
                if (anchorId == null) {
                    val biasYOffset = density.run { STUB_HEIGHT.toPx() } * sticker.state.biasY
                    stickerOffset = IntOffset(
                        x = (viewPortWidth * sticker.state.biasX).toInt(),
                        y = (biasYOffset + layoutInfo.viewportStartOffset + toolbarHeightPx).toInt(),
                    )
                    isVisible = true
                } else {
                    if (noteContent.none { it.id == sticker.state.anchorId }) {
                        sticker.calculateAnchor(
                            noteContent = noteContent,
                            listState = listState,
                            toolbarHeightPx = toolbarHeightPx,
                            stickerOffset = stickerOffset,
                            density = density,
                        )
                        return@collect
                    }
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
                        )
                    },
                ),
            item = sticker,
            onRemoveClick = onRemoveStickerClick,
        )
    }


    /* var anchorOffset by remember { mutableFloatStateOf(0f) }
     var anchorSize by remember { mutableIntStateOf(0) }
     var isVisible by remember { mutableStateOf(false) }
     var stickerSize by remember { mutableStateOf(IntSize.Zero) }

     LaunchedEffect(noteContent, sticker.state.anchorId) {
         snapshotFlow { listState.layoutInfo }
             .collect { layoutInfo ->
                 val anchorId = sticker.state.anchorId
                 if (anchorId == null) {
                     anchorOffset = toolbarHeightPx
                     anchorSize = layoutInfo.viewportSize.height
                     isVisible = true
                 } else {
                     val info = layoutInfo.visibleItemsInfo.findInfoForAnchorId(
                         noteContent = noteContent,
                         anchorId = sticker.state.anchorId,
                     )

                     if (info != null) {
                         anchorOffset = info.offset.toFloat()
                         anchorSize = info.size
                     }

                     isVisible = info != null
                 }
             }
     }

     if (isVisible) {
         val viewPortWidth by remember {
             derivedStateOf { listState.layoutInfo.viewportSize.width.toFloat() }
         }
         val draggableState = rememberDraggable2DState { delta ->
             sticker.state.biasX += delta.x / viewPortWidth.coerceAtLeast(1f)
             sticker.state.biasY += delta.y / anchorSize.coerceAtLeast(1)
         }
         StickerScreenItem(
             modifier = modifier
                 .offset {
                     val biasX = sticker.state.biasX
                     val biasY = sticker.state.biasY
                     IntOffset(
                         x = (viewPortWidth * biasX).toInt(),
                         y = (anchorSize * biasY + anchorOffset + toolbarHeightPx).toInt(),
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
     }*/
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

    val suitableAnchor = availableAnchors.minByOrNull { anchor ->
        val itemStart = anchor.offset
        val itemEnd = itemStart + anchor.size
        val visibleStart = max(0, min(viewPortHeight, itemEnd))
        (visibleStart - stickerOffset.y).absoluteValue
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
