package com.furianrt.notepage.internal.ui.stickers.entities

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.furianrt.notepage.internal.ui.stickers.StickerState
import com.furianrt.uikit.extensions.pxToDp
import java.util.UUID
import kotlin.random.Random

@Stable
internal data class StickerItem(
    val id: String,
    val typeId: String,
    @param:DrawableRes val icon: Int,
    val animate: Boolean = false,
    val state: StickerState = StickerState(),
) {
    companion object {
        private const val X_OFFSET_PERCENT = 0.1
        private val Y_OFFSET_DP = 20.dp

        val DEFAULT_SIZE = 120.dp
        const val MIN_SIZE_PERCENT = 0.3f
        const val MAX_SIZE_PERCENT = 2.5f
        const val MIN_ANGLE = 4f

        fun build(
            typeId: String,
            @DrawableRes icon: Int,
            scrollOffset: Int,
            viewPortHeight: Int,
            toolsPanelHeight: Dp,
            toolBarHeight: Dp,
            density: Density,
        ): StickerItem {
            val randXOffset = Random.nextDouble(
                from = -X_OFFSET_PERCENT,
                until = X_OFFSET_PERCENT,
            ).toFloat()

            val randYOffset = Random.nextDouble(
                from = -Y_OFFSET_DP.value.toDouble(),
                until = Y_OFFSET_DP.value.toDouble(),
            ).dp

            val halfViewPort = (viewPortHeight / 2f).pxToDp(density)
            return StickerItem(
                id = UUID.randomUUID().toString(),
                typeId = typeId,
                icon = icon,
                animate = true,
                state = StickerState(
                    initialBiasX = 0.5f + randXOffset,
                    initialDpOffsetY = scrollOffset.pxToDp(density) +
                            toolBarHeight +
                            halfViewPort -
                            toolsPanelHeight +
                            randYOffset,
                ),
            )
        }
    }
}