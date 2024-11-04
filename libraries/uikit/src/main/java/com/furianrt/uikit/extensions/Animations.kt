package com.furianrt.uikit.extensions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntOffset
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
            interactionSource = remember { MutableInteractionSource() },
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

fun Modifier.animatePlacement(
    animationSpec: AnimationSpec<IntOffset> = spring(stiffness = StiffnessMediumLow),
): Modifier = this.composed {
    val scope = rememberCoroutineScope()
    var targetOffset by remember { mutableStateOf(IntOffset.Zero) }
    var animatable by remember { mutableStateOf<Animatable<IntOffset, AnimationVector2D>?>(null) }
    onPlaced {
        targetOffset = it.positionInParent().round()
    }.offset {
        val anim = animatable ?: Animatable(targetOffset, IntOffset.VectorConverter)
            .also { animatable = it }
        if (anim.targetValue != targetOffset) {
            scope.launch { anim.animateTo(targetOffset, animationSpec) }
        }
        animatable?.let { it.value - targetOffset } ?: IntOffset.Zero
    }
}
