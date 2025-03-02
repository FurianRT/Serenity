package com.furianrt.notepage.internal.ui.stickers.entities

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.furianrt.notepage.internal.ui.stickers.StickerState
import java.util.UUID
import kotlin.random.Random

@Stable
internal data class StickerItem(
    val id: String,
    val typeId: String,
    @DrawableRes val icon: Int,
    val state: StickerState = StickerState(),
) {
    companion object {
        private const val X_OFFSET_PERCENT = 0.05
        private val Y_OFFSET_DP = 16.dp

        val DEFAULT_SIZE = 120.dp
        const val MIN_SIZE_PERCENT = 0.6f
        const val MAX_SIZE_PERCENT = 2f
        const val MIN_ANGLE = 3f

        fun build(
            typeId: String,
            @DrawableRes icon: Int,
            scrollOffset: Int,
            viewPortHeight: Int,
            toolsPanelHeight: Dp,
            toolBarHeight: Dp,
            density: Density,
        ): StickerItem {
            with(density) {
                val randXOffset = Random.nextDouble(
                    from = -X_OFFSET_PERCENT,
                    until = X_OFFSET_PERCENT,
                ).toFloat()

                val randYOffset = Random.nextDouble(
                    from = -Y_OFFSET_DP.value.toDouble(),
                    until = Y_OFFSET_DP.value.toDouble(),
                ).dp

                val halfViewPort = (viewPortHeight / 2f).toDp()
                return StickerItem(
                    id = UUID.randomUUID().toString(),
                    typeId = typeId,
                    icon = icon,
                    state = StickerState(
                        initialBiasX = 0.5f + randXOffset,
                        initialDpOffsetY = scrollOffset.toDp() +
                                toolBarHeight +
                                halfViewPort -
                                toolsPanelHeight +
                                randYOffset,
                    ),
                )
            }
        }
    }
}