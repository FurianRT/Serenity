package com.furianrt.uikit.extensions

import androidx.compose.material.ripple.RippleAlpha

operator fun RippleAlpha.div(value: Float) = RippleAlpha(
    draggedAlpha = draggedAlpha / value,
    focusedAlpha = focusedAlpha / value,
    hoveredAlpha = hoveredAlpha / value,
    pressedAlpha = pressedAlpha / value,
)