package com.furianrt.notepage.internal.ui.stickers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.furianrt.notepage.R
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.sqrt
import com.furianrt.uikit.R as uiR

@OptIn(FlowPreview::class)
@Composable
internal fun StickerScreenItem(
    item: StickerItem,
    onRemoveClick: (item: StickerItem) -> Unit = {},
    onDragged: (delta: Offset) -> Unit = {},
    onTransformed: () -> Unit = {},
    onClick: (tem: StickerItem) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var parentCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var childCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var stickerCenter by remember { mutableStateOf<Offset?>(null) }
    var initialDragOffset by remember { mutableStateOf<Offset?>(null) }
    var initialAngle by remember { mutableFloatStateOf(item.state.rotation) }
    var initialScale by remember { mutableFloatStateOf(item.state.scale) }

    var transformTrigger by remember { mutableIntStateOf(0) }
    var isFirstLaunch by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        snapshotFlow { transformTrigger }
            .debounce(100)
            .collect {
                if (!isFirstLaunch) {
                    onTransformed()
                }
                isFirstLaunch = false
            }
    }

    Box(
        modifier = modifier
            .applyIf(item.state.isEditing) {
                Modifier.pointerInput(Unit) {
                    detectTransformGestures { _, pan, scale, rotation ->
                        val resultRotation = (item.state.rotation + rotation) % 360
                        val resultScale = (item.state.scale * scale).coerceIn(
                            minimumValue = StickerItem.MIN_SIZE_PERCENT,
                            maximumValue = StickerItem.MAX_SIZE_PERCENT,
                        )
                        initialAngle = resultRotation
                        initialScale = resultScale
                        item.state.rotation = resultRotation
                        item.state.scale = resultScale
                        onDragged(pan)
                        transformTrigger++
                    }
                }
            }
            .onGloballyPositioned { parentCoordinates = it }
            .graphicsLayer {
                rotationZ = item.state.rotation
                scaleX = item.state.scale
                scaleY = item.state.scale
            },
    ) {
        Sticker(
            modifier = Modifier
                .padding(8.dp)
                .applyIf(item.state.isEditing) {
                    Modifier.border(
                        width = 1.5.dp / item.state.scale,
                        color = MaterialTheme.colorScheme.primaryContainer,
                    )
                }
                .onGloballyPositioned { stickerCenter = it.boundsInParent().center },
            icon = painterResource(item.icon),
            isFlipped = item.state.isFlipped,
            onClick = { onClick(item) },
        )

        if (item.state.isEditing) {
            val buttonSize = 24.dp
            val scaledSize = buttonSize * item.state.scale
            val offsetPx = with(LocalDensity.current) { (scaledSize - buttonSize).toPx() / 6 }
            val sizeModifier = Modifier.size(buttonSize)

            ButtonClose(
                modifier = sizeModifier
                    .graphicsLayer {
                        val scale = 1f / item.state.scale
                        scaleX = scale
                        scaleY = scale
                    }
                    .offset { IntOffset(-offsetPx.toInt(), -offsetPx.toInt()) },
                onClick = { onRemoveClick(item) },
            )

            ButtonResize(
                modifier = sizeModifier
                    .align(Alignment.BottomEnd)
                    .graphicsLayer {
                        val scale = 1f / item.state.scale
                        scaleX = scale
                        scaleY = scale
                    }
                    .onGloballyPositioned { coordinates ->
                        childCoordinates = coordinates
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                initialDragOffset = getOffsetInParent(
                                    childCoordinates ?: return@detectDragGestures,
                                    parentCoordinates ?: return@detectDragGestures,
                                    offset,
                                )
                            },
                            onDragEnd = {
                                initialDragOffset = null
                                initialAngle = item.state.rotation
                                initialScale = item.state.scale
                                onTransformed()
                            }
                        ) { change, _ ->
                            change.consume()
                            val center = stickerCenter ?: return@detectDragGestures
                            val initialOffset = initialDragOffset ?: return@detectDragGestures
                            val currentOffset = getOffsetInParent(
                                childCoordinates = childCoordinates ?: return@detectDragGestures,
                                parentCoordinates = parentCoordinates ?: return@detectDragGestures,
                                offset = change.position,
                            )

                            val newAngle = calculateRotationAngle(
                                center = center,
                                start = initialOffset,
                                current = currentOffset,
                            )

                            val angle = initialAngle + newAngle
                            val gip = hypot(currentOffset.x - center.x, currentOffset.y - center.y)
                            val newSize = sqrt(gip * gip * 4 / 2)
                            val defaultSize = StickerItem.DEFAULT_SIZE.toPx()

                            if (angle.absoluteValue >= StickerItem.MIN_ANGLE) {
                                item.state.rotation = angle
                            } else {
                                item.state.rotation = 0f
                            }
                            item.state.scale = (newSize / defaultSize).coerceIn(
                                minimumValue = StickerItem.MIN_SIZE_PERCENT,
                                maximumValue = StickerItem.MAX_SIZE_PERCENT,
                            )
                        }
                    }
                    .offset { IntOffset(offsetPx.toInt(), offsetPx.toInt()) }
                    .systemGestureExclusion(),
            )

            ButtonFlip(
                modifier = sizeModifier
                    .align(Alignment.TopEnd)
                    .graphicsLayer {
                        val scale = 1f / item.state.scale
                        scaleX = scale
                        scaleY = scale
                    }
                    .offset { IntOffset(offsetPx.toInt(), -offsetPx.toInt()) },
                onClick = {
                    item.state.isFlipped = !item.state.isFlipped
                    onTransformed()
                },
            )
        }
    }
}

private fun getOffsetInParent(
    childCoordinates: LayoutCoordinates,
    parentCoordinates: LayoutCoordinates,
    offset: Offset,
): Offset = parentCoordinates.localPositionOf(childCoordinates, offset)

private fun calculateRotationAngle(center: Offset, start: Offset, current: Offset): Float {
    val angleStart = atan2(start.y - center.y, start.x - center.x)
    val angleCurrent = atan2(current.y - center.y, current.x - center.x)
    return Math.toDegrees(angleCurrent - angleStart.toDouble()).toFloat()
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
