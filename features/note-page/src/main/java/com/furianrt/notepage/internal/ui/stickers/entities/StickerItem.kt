package com.furianrt.notepage.internal.ui.stickers.entities

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.notepage.internal.ui.stickers.StickerState
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
    companion object {
        private const val OFFSET_PERCENT = 0.06f

        fun build(
            type: Int,
            noteContent: List<UiNoteContent>,
            listState: LazyListState,
            toolbarHeightPx: Int,
        ): StickerItem {
            val viewPortSize = listState.layoutInfo.viewportSize.height
            val viewportCenter = viewPortSize / 2
            val anchor = listState.layoutInfo.visibleItemsInfo
                .filter { info ->
                    val content = noteContent.getOrNull(info.index)
                    val emptyTitle = content is UiNoteContent.Title && content.state.text.isEmpty()
                    val isTagsBlock = info.key == UiNoteTag.BLOCK_ID
                    !emptyTitle && !isTagsBlock
                }
                .minByOrNull { info ->
                    val itemCenter = info.offset + info.size + toolbarHeightPx
                    (viewportCenter - itemCenter).absoluteValue
                }

            val stickerId = UUID.randomUUID().toString()
            val randomOffset = Random.nextDouble(
                from = 1.0 - OFFSET_PERCENT,
                until = 1.0 + OFFSET_PERCENT,
            ).toFloat()

            return if (anchor != null) {
                val itemStart = (anchor.offset + toolbarHeightPx)
                val itemEnd = itemStart + anchor.size
                val visibleStart = max(0, min(viewPortSize, itemEnd))
                val visibleEnd = max(0, min(viewPortSize, itemStart))
                val visibleMiddle = (visibleStart + visibleEnd) / 2f
                val minVisibleMiddle = visibleMiddle.coerceAtLeast(viewportCenter * 0.8f)
                StickerItem(
                    id = stickerId,
                    type = type,
                    state = StickerState(
                        initialAnchorId = noteContent[anchor.index].id,
                        initialBiasX = 0.5f * randomOffset,
                        initialBiasY = (minVisibleMiddle * randomOffset - itemStart) / anchor.size,
                    ),
                )
            } else {
                StickerItem(
                    id = stickerId,
                    type = type,
                    state = StickerState(
                        initialBiasX = 0.5f * randomOffset,
                        initialBiasY = 0.4f * randomOffset,
                    ),
                )
            }
        }
    }
}