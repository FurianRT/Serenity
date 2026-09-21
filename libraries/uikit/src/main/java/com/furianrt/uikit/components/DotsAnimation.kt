package com.furianrt.uikit.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.extensions.dpToPx

@Composable
fun DotsAnimation(
    modifier: Modifier = Modifier,
    circleSize: Float = 10.dp.dpToPx(),
    circleSpacing: Float = 10.dp.dpToPx(),
    circleCount: Int = 3,
    circleColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    animationDuration: Int = 450,
) {
    val minCircleAlpha = 0.3f
    val maxCircleAlpha = 1f
    val infiniteTransition = rememberInfiniteTransition()
    val animations: List<State<Float>> = buildList {
        repeat(circleCount) { i ->
            add(
                infiniteTransition.animateFloat(
                    initialValue = minCircleAlpha,
                    targetValue = maxCircleAlpha,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = animationDuration),
                        repeatMode = RepeatMode.Reverse,
                        initialStartOffset = StartOffset(i * (animationDuration / circleCount)),
                    ),
                ),
            )
        }
    }
    Canvas(modifier = modifier) {
        val canvasHeight = size.height
        val canvasWidth = size.width
        val startPos = canvasWidth / 2f - circleSize - circleSpacing
        for (circleNumber in 0 until circleCount) {
            drawCircle(
                color = circleColor,
                radius = circleSize / 2,
                alpha = animations[circleNumber].value,
                center = Offset(
                    startPos + (circleSize + circleSpacing) * circleNumber,
                    canvasHeight / 2,
                ),
            )
        }
    }
}