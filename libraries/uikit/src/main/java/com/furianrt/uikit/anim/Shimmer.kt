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
import kotlinx.coroutines.launch

private const val DEFAULT_SHIMMER_DURATION = 800

private class ShimmerModifierNode(
    val durationMillis: Int,
    val easing: Easing,
    val color: Color,
) : Modifier.Node(),
    DrawModifierNode {

    var cachedBrush: Brush? = null
    var cachedWidth = 0f
    var cachedHeight = 0f

    private val shimmerProgress = Animatable(0f)

    override fun ContentDrawScope.draw() {
        val progress = shimmerProgress.value

        if (cachedBrush == null || cachedWidth != size.width || cachedHeight != size.height) {
            cachedBrush = Brush.linearGradient(
                colors = listOf(Color.Transparent, color, Color.Transparent),
                start = Offset(
                    x = size.width * progress - size.width * 0.7f,
                    y = size.height * progress - size.height * 0.7f,
                ),
                end = Offset(
                    x = size.width * progress - size.width * 0.1f,
                    y = size.height * progress - size.height * 0.1f,
                ),
            )
        }

        cachedBrush?.let(::drawRect)
        drawContent()
    }

    override fun onAttach() {
        coroutineScope.launch {
            shimmerProgress.animateTo(
                targetValue = 2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        delayMillis = 1000,
                        durationMillis = durationMillis,
                        easing = easing,
                    ),
                ),
            ) {
                invalidateDraw()
            }
        }
    }

    override fun onReset() {
        super.onReset()
        coroutineScope.launch {
            shimmerProgress.snapTo(0f)
        }
    }
}

private data class ShimmerModifierElement(
    val durationMillis: Int,
    val easing: Easing,
    val color: Color,
) : ModifierNodeElement<ShimmerModifierNode>() {
    override fun create(): ShimmerModifierNode = ShimmerModifierNode(durationMillis, easing, color)
    override fun update(node: ShimmerModifierNode) = Unit
    override fun InspectorInfo.inspectableProperties() {
        name = "com.furianrt.uikit.anim.shimmer"
    }
}

fun Modifier.shimmer(
    durationMillis: Int = DEFAULT_SHIMMER_DURATION,
    easing: Easing = LinearEasing,
    color: Color = Color.White.copy(alpha = 0.1f),
): Modifier = this then ShimmerModifierElement(durationMillis, easing, color)
