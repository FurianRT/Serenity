package com.furianrt.notepage.internal.ui.entities

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notepage.internal.ui.stickers.StickerState
import java.util.UUID
import kotlin.math.absoluteValue
import kotlin.random.Random

@Stable
internal data class StickerItem(
    val id: String,
    val type: Int,
    val isEditing: Boolean = false,
    val state: StickerState = StickerState(),
) {
    companion object {
        fun build(
            type: Int,
            noteContent: List<UiNoteContent>,
            listState: LazyListState,
        ): StickerItem {
            val viewportCenter = listState.layoutInfo.viewportSize.height / 2
            val anchor = listState.layoutInfo.visibleItemsInfo
                .filterNot { info ->
                    val content = noteContent.getOrNull(info.index)
                    content is UiNoteContent.Title && content.state.text.isEmpty()
                }
                .minByOrNull { info ->
                    val itemCenter = info.offset + info.size
                    (viewportCenter - itemCenter).absoluteValue
                }

            val stickerId = UUID.randomUUID().toString()

            return if (anchor != null) {
                val randomOffset = Random.nextInt(-100, 100) / 1000f
                StickerItem(
                    id = stickerId,
                    type = type,
                    state = StickerState(
                        initialAnchorId = noteContent[anchor.index].id,
                        initialBiasX = 0.5f + randomOffset,
                        initialBiasY = 0.5f + randomOffset,
                    ),
                )
            } else {
                StickerItem(id = stickerId, type = type)
            }
        }
    }
}