package com.furianrt.uikit.extensions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.DeferredTargetAnimation
import androidx.compose.animation.core.ExperimentalAnimatableApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ApproachLayoutModifierNode
import androidx.compose.ui.layout.ApproachMeasureScope
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import kotlinx.coroutines.launch

fun Modifier.clickableWithScaleAnim(
    duration: Int = 250,
    maxScale: Float = 1.1f,
    indication: Indication? = null,
    onClick: () -> Unit = {},
) = this.composed {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val resultModifier = graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
    val scaleAction = {
        scope.launch {
            scale.animateTo(
                targetValue = maxScale,
                animationSpec = tween(durationMillis = duration / 2),
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = duration / 2),
            )
        }
    }
    if (indication != null) {
        resultModifier.clickable(
            onClick = {
                if (scale.isRunning) {
                    return@clickable
                }
                scaleAction()
                onClick()
            },
            indication = indication,
            interactionSource = null,
        )
    } else {
        resultModifier.clickableNoRipple {
            if (scale.isRunning) {
                return@clickableNoRipple
            }
            scaleAction()
            onClick()
        }
    }
}


fun Modifier.animatePlacementInScope(lookaheadScope: LookaheadScope): Modifier {
    return this.then(AnimatePlacementNodeElement(lookaheadScope))
}

@OptIn(ExperimentalAnimatableApi::class)
private class AnimatedPlacementModifierNode(
    var lookaheadScope: LookaheadScope,
) : ApproachLayoutModifierNode, Modifier.Node() {

    private val animationSpec = spring<IntOffset>(stiffness = Spring.StiffnessMediumLow)

    private val offsetAnimation: DeferredTargetAnimation<IntOffset, AnimationVector2D> =
        DeferredTargetAnimation(IntOffset.VectorConverter)

    override fun isMeasurementApproachInProgress(lookaheadSize: IntSize) = false

    override fun Placeable.PlacementScope.isPlacementApproachInProgress(
        lookaheadCoordinates: LayoutCoordinates,
    ): Boolean {
        offsetAnimation.updateTarget(
            target = with(lookaheadScope) {
                lookaheadScopeCoordinates.localLookaheadPositionOf(lookaheadCoordinates).round()
            },
            coroutineScope = coroutineScope,
            animationSpec = animationSpec,
        )
        return !offsetAnimation.isIdle
    }

    override fun ApproachMeasureScope.approachMeasure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            val coordinates = coordinates
            if (coordinates != null) {
                val animatedOffset = offsetAnimation.updateTarget(
                    target = with(lookaheadScope) {
                        lookaheadScopeCoordinates.localLookaheadPositionOf(coordinates).round()
                    },
                    coroutineScope = coroutineScope,
                    animationSpec = animationSpec,
                )

                val placementOffset = with(lookaheadScope) {
                    lookaheadScopeCoordinates.localPositionOf(coordinates, Offset.Zero).round()
                }

                val (x, y) = animatedOffset - placementOffset
                placeable.place(x, y)
            } else {
                placeable.place(0, 0)
            }
        }
    }
}

private data class AnimatePlacementNodeElement(
    val lookaheadScope: LookaheadScope,
) : ModifierNodeElement<AnimatedPlacementModifierNode>() {

    override fun update(node: AnimatedPlacementModifierNode) {
        node.lookaheadScope = lookaheadScope
    }

    override fun create(): AnimatedPlacementModifierNode {
        return AnimatedPlacementModifierNode(lookaheadScope)
    }
}
