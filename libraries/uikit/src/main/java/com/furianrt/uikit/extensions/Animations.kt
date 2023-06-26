package com.furianrt.uikit.extensions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch

inline fun Modifier.clickableWithScaleAnim(
    duration: Int = 250,
    maxScale: Float = 1.1f,
    indication: Indication? = null,
    crossinline onClick: () -> Unit = {},
) = composed {
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
