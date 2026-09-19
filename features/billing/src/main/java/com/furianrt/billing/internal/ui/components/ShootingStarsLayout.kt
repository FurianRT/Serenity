package com.furianrt.billing.internal.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import com.furianrt.billing.R
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
internal fun ShootingStarsLayout(
    modifier: Modifier = Modifier,
    angle: Float = 152f,
    starColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) {
    val starVector = ImageVector.vectorResource(R.drawable.ic_shooting_star)
    val starPainter = rememberVectorPainter(starVector)
    val starColorFilter = remember(starColor) { ColorFilter.tint(starColor) }
    val starSize = starPainter.intrinsicSize
    val starPivot = remember(starSize) { Offset(starSize.width / 2f, starSize.height / 2f) }

    val radians = Math.toRadians(angle.toDouble())
    val direction = remember(radians) { Offset(cos(radians).toFloat(), sin(radians).toFloat()) }

    val infiniteTransition = rememberInfiniteTransition()

    val anim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 10000,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
    )

    Canvas(modifier) {
        val distance = hypot(size.width, size.height)
        val center = Offset(size.width / 2f, size.height / 2f)

        val start = center - direction * distance
        val end = center + direction * distance

        val position = lerp(start, end, anim)

        withTransform({
            translate(
                left = position.x - starSize.width / 2f,
                top = position.y - starSize.height / 2f,
            )
            rotate(
                degrees = angle,
                pivot = starPivot,
            )
        }) {
            with(starPainter) {
                draw(
                    size = starSize,
                    colorFilter = starColorFilter,
                )
            }
        }
    }
}
