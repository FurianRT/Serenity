package com.furianrt.uikit.extensions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.ui.Modifier

operator fun RippleAlpha.div(value: Float) = RippleAlpha(
    draggedAlpha = draggedAlpha / value,
    focusedAlpha = focusedAlpha / value,
    hoveredAlpha = hoveredAlpha / value,
    pressedAlpha = pressedAlpha / value,
)

operator fun RippleAlpha.times(value: Float) = RippleAlpha(
    draggedAlpha = draggedAlpha * value,
    focusedAlpha = focusedAlpha * value,
    hoveredAlpha = hoveredAlpha * value,
    pressedAlpha = pressedAlpha * value,
)

fun Modifier.clickableNoRipple(
    onClick: () -> Unit,
) = this.clickableNoRipple(
    onClick = onClick,
    interactionSource = null,
)

fun Modifier.clickableNoRipple(
    interactionSource: MutableInteractionSource?,
    onClick: () -> Unit,
) = this.clickable(
    onClick = onClick,
    indication = null,
    interactionSource = interactionSource,
)
