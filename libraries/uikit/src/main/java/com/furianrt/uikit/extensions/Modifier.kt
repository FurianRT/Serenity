package com.furianrt.uikit.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

inline fun Modifier.conditional(
    condition: Boolean,
    ifTrue: () -> Modifier,
    ifFalse: () -> Modifier,
) = this then if (condition) ifTrue() else ifFalse()

inline fun Modifier.applyIf(condition: Boolean, ifTrue: () -> Modifier) =
    conditional(condition, ifTrue, ifFalse = { Modifier })

fun Modifier.dashedRoundedRectBorder(
    color: Color,
    width: Dp = 1.dp,
    interval: Dp = 6.dp,
    cornerRadius: Dp = 16.dp,
): Modifier = this.then(
    Modifier.drawWithCache {
        val radius = CornerRadius(cornerRadius.toPx())
        val strokeInterval = interval.toPx()
        val stroke = Stroke(
            width = width.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(strokeInterval, strokeInterval),
                phase = 0f,
            )
        )
        onDrawBehind {
            drawRoundRect(
                color = color,
                style = stroke,
                cornerRadius = radius,
            )
        }
    }
)
