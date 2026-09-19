package com.furianrt.uikit.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import com.furianrt.uikit.R
import kotlin.random.Random

private data class StarData(
    val sizeMultiplier: Float,
    val alphaAnim: State<Float>,
    val scaleAnim: State<Float>,
    val translationXMultiplier: Float,
    val translationYMultiplier: Float,
)

@Composable
fun StarsLayout(
    starCount: Int,
    modifier: Modifier = Modifier,
    starColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    starRotation: () -> Float = { 0f },
) {
    val starVector = ImageVector.vectorResource(R.drawable.ic_star)
    val starPainter = rememberVectorPainter(starVector)
    val starColorFilter = remember(starColor) { ColorFilter.tint(starColor) }

    val painterSize = starPainter.intrinsicSize.height

    val starPivot = remember(painterSize) { Offset(painterSize / 2f, painterSize / 2f) }
    val starSize = remember(painterSize) { Size(painterSize, painterSize) }

    val infiniteTransition = rememberInfiniteTransition()

    val stars = List(starCount) {
        val delay = remember { Random.nextInt(0, 2000) }
        val duration = remember { Random.nextInt(500, 2000) }
        StarData(
            alphaAnim = infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        delayMillis = delay,
                        durationMillis = duration,
                        easing = LinearEasing,
                    ),
                    repeatMode = RepeatMode.Reverse,
                ),
            ),
            scaleAnim = infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        delayMillis = delay,
                        durationMillis = duration,
                    ),
                    repeatMode = RepeatMode.Reverse,
                ),
            ),
            translationXMultiplier = remember { Random.nextFloat() },
            translationYMultiplier = remember { Random.nextFloat() },
            sizeMultiplier = remember { Random.nextDouble(0.2, 1.0).toFloat() },
        )
    }

    Canvas(
        modifier = modifier,
    ) {
        stars.forEach { star ->
            val scale = star.scaleAnim.value * star.sizeMultiplier
            withTransform({
                translate(
                    left = size.width * star.translationXMultiplier,
                    top = size.height * star.translationYMultiplier,
                )
                scale(
                    scaleX = scale,
                    scaleY = scale,
                    pivot = starPivot,
                )
                rotate(
                    degrees = 360f - starRotation(),
                    pivot = starPivot,
                )
            }) {
                with(starPainter) {
                    draw(
                        size = starSize,
                        alpha = star.alphaAnim.value,
                        colorFilter = starColorFilter,
                    )
                }
            }
        }
    }
}