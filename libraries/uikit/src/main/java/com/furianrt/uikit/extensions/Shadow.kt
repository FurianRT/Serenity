package com.furianrt.uikit.extensions

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun DrawScope.drawBottomShadow(
    color: Color = Color.Black,
    elevation: Dp = 6.dp,
) = drawRect(
    topLeft = Offset(0f, size.height),
    size = Size(size.width, elevation.toPx()),
    brush = Brush.verticalGradient(
        colors = listOf(color.copy(alpha = 0.2f), Color.Transparent),
        startY = size.height,
        endY = size.height + elevation.toPx(),
    ),
)

fun DrawScope.drawTopInnerShadow(
    color: Color = Color.Black,
    elevation: Dp = 6.dp,
) = drawRect(
    size = Size(size.width, elevation.toPx()),
    brush = Brush.verticalGradient(
        colors = listOf(color.copy(alpha = 0.2f), Color.Transparent),
        startY = 0f,
        endY = elevation.toPx(),
    ),
)
