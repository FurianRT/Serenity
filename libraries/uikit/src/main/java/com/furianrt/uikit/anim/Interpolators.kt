package com.furianrt.uikit.anim

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberOvershootEasing(tension: Float): Easing {
    return remember(tension) {
        val interpolator = OvershootInterpolator(tension)
        Easing { interpolator.getInterpolation(it) }
    }
}