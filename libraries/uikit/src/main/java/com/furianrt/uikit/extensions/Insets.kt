package com.furianrt.uikit.extensions

import android.view.View
import android.view.Window
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.IntState
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun rememberKeyboardOffsetState(minOffset: Int = 0): IntState =
    rememberUpdatedState(
        WindowInsets.ime.getBottom(LocalDensity.current).coerceAtLeast(minOffset),
    ).asIntState()

fun Window.showSystemUi() {
    with(WindowCompat.getInsetsController(this, decorView)) {
        show(WindowInsetsCompat.Type.statusBars())
        show(WindowInsetsCompat.Type.navigationBars())
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
    }
}

fun Window.hideSystemUi() {
    with(WindowCompat.getInsetsController(this, decorView)) {
        hide(WindowInsetsCompat.Type.statusBars())
        hide(WindowInsetsCompat.Type.navigationBars())
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun View.getStatusBarHeight() = ViewCompat.getRootWindowInsets(this)
    ?.getInsets(WindowInsetsCompat.Type.systemBars())
    ?.top ?: 0
