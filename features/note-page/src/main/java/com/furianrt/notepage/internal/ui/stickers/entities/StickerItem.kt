package com.furianrt.notepage.internal.ui.stickers.entities

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.furianrt.core.mapImmutable
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.notepage.internal.ui.stickers.StickerState
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

@Stable
internal data class StickerItem(
    val id: String,
    val type: Int,
    val isEditing: Boolean = false,
    val state: StickerState = StickerState(),
) {
    fun calculateAnchor(
        noteContent: List<UiNoteContent>,
        listState: LazyListState,
        stickerOffset: IntOffset,
        stickerHeight: Float,
        toolbarHeight: Float,
        density: Density,
        randomOffset: Float = 1f,
    ) {
        val viewPortSize = listState.layoutInfo.viewportSize
        val stickerBottom = stickerOffset.y + stickerHeight

        var anchors = listState.layoutInfo.visibleItemsInfo.filter { info ->
            val content = noteContent.getOrNull(info.index)
            val emptyTitle = content is UiNoteContent.Title && content.state.text.isEmpty()
            val isTagsBlock = info.key == UiNoteTag.BLOCK_ID
            !emptyTitle && !isTagsBlock
        }

        val inBoundsAnchors = anchors.filter { info ->
            val infoTop = info.offset + toolbarHeight
            val infoBottom = infoTop + info.size
            stickerOffset.y <= infoBottom && stickerBottom >= infoTop
        }

        if (inBoundsAnchors.isEmpty()) {
            val closestAnchor = anchors.minByOrNull { info ->
                val itemStart = info.offset + toolbarHeight.toInt()
                val itemEnd = itemStart + info.size
                val visibleStart = max(0, min(viewPortSize.height, itemEnd))
                val visibleEnd = max(0, min(viewPortSize.height, itemStart))
                val visibleMiddle = (visibleStart + visibleEnd) / 2f
                (stickerOffset.y - visibleMiddle).absoluteValue
            }
            anchors = closestAnchor?.let { listOf(it) } ?: emptyList()
        } else {
            anchors = inBoundsAnchors
        }

        state.anchors = if (anchors.isNotEmpty()) {
            anchors.mapImmutable { anchor ->
                StickerState.Anchor.Item(
                    id = noteContent[anchor.index].id,
                    biasX = (stickerOffset.x.toFloat() / viewPortSize.width) * randomOffset,
                    biasY = (stickerOffset.y - anchor.offset - toolbarHeight) / anchor.size.toFloat(),
                )
            }
        } else {
            val stubHeightPx = density.run { STUB_HEIGHT.toPx() }
            persistentListOf(
                StickerState.Anchor.ViewPort(
                    biasX = stickerOffset.x.toFloat() / viewPortSize.width * randomOffset,
                    biasY = (stickerOffset.y.toFloat() - toolbarHeight) / stubHeightPx * randomOffset,
                ),
            )
        }
    }

    companion object {
        private const val OFFSET_PERCENT = 0.06f

        val DEFAULT_SIZE = 100.dp
        val MIN_SIZE = 40.dp
        val MAX_SIZE = 300.dp

        val STUB_HEIGHT = 300.dp

        fun build(
            type: Int,
            noteContent: List<UiNoteContent>,
            listState: LazyListState,
            stickerSize: Float,
            toolbarHeight: Float,
            density: Density,
        ): StickerItem {
            val viewPortSize = listState.layoutInfo.viewportSize
            val viewPortCenter = viewPortSize.height / 2
            val item = StickerItem(
                id = UUID.randomUUID().toString(),
                type = type,
            )

            item.calculateAnchor(
                noteContent = noteContent,
                listState = listState,
                stickerHeight = stickerSize,
                stickerOffset = IntOffset(
                    x = viewPortSize.width / 2 - stickerSize.toInt() / 2,
                    y = (viewPortCenter - stickerSize / 2).toInt(),
                ),
                toolbarHeight = toolbarHeight,
                density = density,
                 randomOffset = Random.nextDouble(
                     from = 1.0 - OFFSET_PERCENT,
                     until = 1.0 + OFFSET_PERCENT,
                 ).toFloat(),
            )

            return item
        }
    }
}