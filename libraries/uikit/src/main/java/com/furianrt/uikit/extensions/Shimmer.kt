package com.furianrt.uikit.extensions

import androidx.compose.animation.core.Animatable
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

private val gradient = listOf(
    Color.White.copy(alpha = 0.0f),
    Color.White.copy(alpha = 0.0f),
    Color.White.copy(alpha = 0.1f),
    Color.White.copy(alpha = 0.0f),
    Color.White.copy(alpha = 0.0f),
)

private class ShimmerModifierNode : Modifier.Node(), DrawModifierNode {
    private val shimmerAnimation = Animatable(0f)
    private var brush = Brush.linearGradient(
        colors = gradient,
        start = Offset(0f, 0f),
        end = Offset(x = 0f, y = 0f),
    )
    override fun ContentDrawScope.draw() {
        drawContent()
        drawRect(brush)
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
                10000f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 2500,
                        easing = LinearEasing,
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

private data object ShimmerModifierElement : ModifierNodeElement<ShimmerModifierNode>() {
    override fun create(): ShimmerModifierNode = ShimmerModifierNode()
    override fun update(node: ShimmerModifierNode) = Unit
    override fun InspectorInfo.inspectableProperties() {
        name = "com.furianrt.uikit.shimmer"
    }
}

fun Modifier.shimmer(): Modifier = this then ShimmerModifierElement
