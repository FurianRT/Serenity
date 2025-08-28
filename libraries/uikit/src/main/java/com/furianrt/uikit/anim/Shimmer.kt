package com.furianrt.uikit.anim

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

private const val DEFAULT_SHIMMER_DURATION = 3000

private val gradient = listOf(
    Color.White.copy(alpha = 0.0f),
    Color.White.copy(alpha = 0.2f),
    Color.White.copy(alpha = 0.0f),
)

private class ShimmerModifierNode(
    val durationMillis: Int,
    val easing: Easing,
) : Modifier.Node(),
    DrawModifierNode {
    private val shimmerAnimation = Animatable(0f)
    private var brush = Brush.linearGradient(
        colors = gradient,
        start = Offset(0f, 0f),
        end = Offset(x = 0f, y = 0f),
    )
    override fun ContentDrawScope.draw() {
        drawRect(brush)
        drawContent()
    }

    override fun onReset() {
        super.onReset()
        coroutineScope.launch(NonCancellable) {
            shimmerAnimation.snapTo(0f)
        }
    }

    override fun onAttach() {
        coroutineScope.launch {
            shimmerAnimation.animateTo(
                2000f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = durationMillis,
                        easing = easing,
                    ),
                ),
            ) {
                brush = Brush.linearGradient(
                    colors = gradient,
                    start = Offset(0f, 0f),
                    end = Offset(
                        x = value,
                        y = value,
                    ),
                )
                invalidateDraw()
            }
        }
    }
}

private data class ShimmerModifierElement(
    val durationMillis: Int,
    val easing: Easing,
) : ModifierNodeElement<ShimmerModifierNode>() {
    override fun create(): ShimmerModifierNode = ShimmerModifierNode(durationMillis, easing)
    override fun update(node: ShimmerModifierNode) = Unit
    override fun InspectorInfo.inspectableProperties() {
        name = "com.furianrt.uikit.anim.shimmer"
    }
}

fun Modifier.shimmer(
    durationMillis: Int = DEFAULT_SHIMMER_DURATION,
    easing: Easing = LinearEasing,
): Modifier = this then ShimmerModifierElement(durationMillis, easing)
