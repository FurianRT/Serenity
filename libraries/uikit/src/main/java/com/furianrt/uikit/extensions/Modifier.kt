package com.furianrt.uikit.extensions

import androidx.compose.ui.Modifier

inline fun Modifier.conditional(
    condition: Boolean,
    ifTrue: () -> Modifier,
    ifFalse: () -> Modifier,
) = this then if (condition) ifTrue() else ifFalse()

inline fun Modifier.applyIf(condition: Boolean, ifTrue: () -> Modifier) =
    conditional(condition, ifTrue, ifFalse = { Modifier })
