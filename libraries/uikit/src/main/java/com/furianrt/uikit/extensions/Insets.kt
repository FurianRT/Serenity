package com.furianrt.uikit.extensions

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.IntState
import androidx.compose.runtime.State
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity

@Composable
fun rememberKeyboardVisibilityState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@Composable
fun rememberKeyboardOffsetState(minOffset: Int = 0): IntState =
    rememberUpdatedState(
        WindowInsets.ime.getBottom(LocalDensity.current).coerceAtLeast(minOffset),
    ).asIntState()
