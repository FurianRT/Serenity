package com.furianrt.uikit.extensions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch

fun Modifier.clickableWithScaleAnim(
    duration: Int,
    maxScale: Float = 1.1f,
    onClick: () -> Unit = {},
) = composed {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }.clickableNoRipple {
        if (scale.isRunning) {
            return@clickableNoRipple
        }
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
        onClick()
    }
}
