package com.furianrt.notepage.internal.ui.stickers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun StickerScreenItem(
    item: StickerItem,
    onRemoveClick: (item: StickerItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(100.dp)
            .background(Color.Red)
            .border(2.dp, Color.Blue)
            .clickableNoRipple { onRemoveClick(item) }
    )
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        StickerScreenItem(
            item = StickerItem(
                id = "",
                type = 0,
                isEditing = true,
                state = StickerState(),
            )
        )
    }
}
