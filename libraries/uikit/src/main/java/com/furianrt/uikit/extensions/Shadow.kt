package com.furianrt.uikit.extensions

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun DrawScope.drawBottomShadow(
    color: Color,
    elevation: Dp = 8.dp,
) = drawRect(
    topLeft = Offset(0f, size.height),
    size = Size(size.width, elevation.toPx()),
    brush = Brush.verticalGradient(
        colors = listOf(color, Color.Transparent),
        startY = size.height,
        endY = size.height + elevation.toPx(),
    ),
)

fun DrawScope.drawTopInnerShadow(
    color: Color,
    elevation: Dp = 2.dp,
) = drawRect(
    size = Size(size.width, elevation.toPx()),
    brush = Brush.verticalGradient(
        colors = listOf(color, Color.Transparent),
        startY = 0f,
        endY = elevation.toPx(),
    ),
)

fun DrawScope.drawLeftShadow(
    color: Color,
    elevation: Dp = 2.dp,
) = drawRect(
    topLeft = Offset(-elevation.toPx(), 0f),
    size = Size(elevation.toPx(), size.height),
    brush = Brush.horizontalGradient(
        colors = listOf(color, Color.Transparent),
        startX = elevation.toPx(),
        endX = -elevation.toPx(),
    ),
)

fun DrawScope.drawRightShadow(
    color: Color,
    elevation: Dp = 2.dp,
) = drawRect(
    topLeft = Offset(size.width, 0f),
    size = Size(elevation.toPx(), size.height),
    brush = Brush.horizontalGradient(
        colors = listOf(color, Color.Transparent),
        startX = size.width,
        endX = size.width + elevation.toPx(),
    ),
)
