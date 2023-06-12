package com.furianrt.uikit.extensions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

operator fun RippleAlpha.div(value: Float) = RippleAlpha(
    draggedAlpha = draggedAlpha / value,
    focusedAlpha = focusedAlpha / value,
    hoveredAlpha = hoveredAlpha / value,
    pressedAlpha = pressedAlpha / value,
)

fun Modifier.clickableNoRipple(
    onClick: () -> Unit,
) = composed {
    clickableNoRipple(
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() },
    )
}

fun Modifier.clickableNoRipple(
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
) = clickable(
    onClick = onClick,
    indication = null,
    interactionSource = interactionSource,
)
