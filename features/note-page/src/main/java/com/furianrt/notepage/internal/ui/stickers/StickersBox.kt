package com.furianrt.notepage.internal.ui.stickers

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun StickersBox(
    stickers: ImmutableList<StickerItem>,
    emptyTitleHeight: Float,
    containerSize: IntSize,
    onStickerClick: (sticker: StickerItem) -> Unit,
    onRemoveStickerClick: (sticker: StickerItem) -> Unit,
    onStickerChanged: (sticker: StickerItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        stickers.forEach { sticker ->
            key(sticker.id) {
                StickerElement(
                    sticker = sticker,
                    emptyTitleHeight = emptyTitleHeight,
                    containerSize = containerSize,
                    onStickerClick = onStickerClick,
                    onRemoveStickerClick = onRemoveStickerClick,
                    onStickerChanged = onStickerChanged,
                )
            }
        }
    }
}

@Composable
private fun StickerElement(
    sticker: StickerItem,
    emptyTitleHeight: Float,
    containerSize: IntSize,
    onStickerChanged: (sticker: StickerItem) -> Unit,
    onStickerClick: (sticker: StickerItem) -> Unit,
    onRemoveStickerClick: (sticker: StickerItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val maxWidthPx = containerSize.width
    val maxHeightPx = containerSize.height
    val density = LocalDensity.current
    val addOffset by animateFloatAsState(
        targetValue = emptyTitleHeight,
        animationSpec = tween(250),
    )
    StickerScreenItem(
        modifier = modifier.offset {
            val stickerSize = StickerItem.DEFAULT_SIZE.toPx()
            IntOffset(
                x = (maxWidthPx * sticker.state.biasX - stickerSize / 2f).toInt(),
                y = (sticker.state.dpOffsetY.toPx() - stickerSize / 2f + addOffset).toInt(),
            )
        },
        item = sticker,
        onRemoveClick = onRemoveStickerClick,
        onDragged = { delta ->
            sticker.state.biasX = if (maxHeightPx == 0) {
                0f
            } else {
                val stickerOffset = maxWidthPx * sticker.state.biasX + delta.x
                stickerOffset.coerceIn(0f, maxWidthPx.toFloat()) / maxWidthPx
            }
            sticker.state.dpOffsetY = density.run {
                (sticker.state.dpOffsetY + delta.y.toDp()).coerceAtLeast(0.dp)
            }
        },
        onTransformed = { onStickerChanged(sticker) },
        onClick = onStickerClick,
    )
}
