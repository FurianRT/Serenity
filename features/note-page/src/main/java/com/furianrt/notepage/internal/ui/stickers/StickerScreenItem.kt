package com.furianrt.notepage.internal.ui.stickers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.notepage.R
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.R as uiR

@Composable
internal fun StickerScreenItem(
    item: StickerItem,
    onRemoveClick: (item: StickerItem) -> Unit = {},
    onDragged: (delta: Offset) -> Unit = {},
    onDragStarted: () -> Unit = {},
    onDragStopped: () -> Unit = {},
    onFlipped: () -> Unit = {},
    onClick: (tem: StickerItem) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .applyIf(item.state.isEditing) {
                    Modifier
                        .border(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
                        .draggable2D(
                            state = rememberDraggable2DState(onDragged),
                            onDragStarted = { onDragStarted() },
                            onDragStopped = { onDragStopped() },
                        )
                },
        ) {
            Sticker(
                icon = painterResource(item.icon),
                isFlipped = item.state.isFlipped,
                onClick = { onClick(item) },
            )
        }

        if (item.state.isEditing) {
            ButtonClose(
                onClick = { onRemoveClick(item) },
            )

            ButtonResize(
                modifier = Modifier.align(Alignment.BottomEnd),
            )

            ButtonFlip(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = {
                    item.state.isFlipped = !item.state.isFlipped
                    onFlipped()
                },
            )
        }
    }
}

@Composable
private fun Sticker(
    icon: Painter,
    isFlipped: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier
            .size(StickerItem.DEFAULT_SIZE)
            .graphicsLayer { scaleX = if (isFlipped) -1f else 1f }
            .clickableNoRipple(onClick),
        painter = icon,
        tint = Color.Unspecified,
        contentDescription = null
    )
}

@Composable
private fun ButtonClose(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.errorContainer)
            .clickable(onClick = onClick)
            .padding(2.dp),
        painter = painterResource(uiR.drawable.ic_exit),
        tint = Color.Unspecified,
        contentDescription = null
    )
}

@Composable
private fun ButtonResize(
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        painter = painterResource(R.drawable.ic_rotate_sticker),
        tint = Color.Unspecified,
        contentDescription = null
    )
}

@Composable
private fun ButtonFlip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick)
            .padding(2.dp),
        painter = painterResource(R.drawable.ic_flip_sticker),
        tint = Color.Unspecified,
        contentDescription = null
    )
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        StickerScreenItem(
            item = StickerItem(
                id = "",
                typeId = "",
                icon = uiR.drawable.ic_folder,
                state = StickerState(initialIsEditing = true),
            )
        )
    }
}
