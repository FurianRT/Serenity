package com.furianrt.uikit.extensions

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.CollapsingToolbarState
import me.onebone.toolbar.ExperimentalToolbarApi
import kotlin.math.abs

val CollapsingToolbarScaffoldState.isExpanded
    get() = offsetY == 0

val CollapsingToolbarScaffoldState.isCollapsed
    get() = abs(offsetY) == toolbarState.minHeight

val CollapsingToolbarScaffoldState.isInMiddleState
    get() = !isExpanded && !isCollapsed

val CollapsingToolbarState.isExpanded
    get() = run {
        val slippage = 0.005f
        progress >= 1f - slippage
    }

val CollapsingToolbarState.isCollapsed
    get() = run {
        val slippage = 0.005f
        progress <= slippage
    }

val CollapsingToolbarState.isInMiddleState
    get() = !isExpanded && !isCollapsed

suspend fun CollapsingToolbarScaffoldState.expand(duration: Int = 250) {
    val offsetYState = getPrivateOffsetYState()
    AnimationState(offsetY.toFloat()).animateTo(0f, tween(duration)) {
        offsetYState.value = value.toInt()
    }
}

suspend fun CollapsingToolbarScaffoldState.collapse(duration: Int = 250) {
    val offsetYState = getPrivateOffsetYState()
    AnimationState(offsetY.toFloat()).animateTo(
        -toolbarState.minHeight.toFloat(),
        tween(duration),
    ) {
        offsetYState.value = value.toInt()
    }
}

suspend fun CollapsingToolbarScaffoldState.performSnap(
    duration: Int = 250,
) {
    val toolbarHeight = toolbarState.minHeight.toFloat()
    val center = toolbarHeight / 2f
    val offset = abs(offsetY).toFloat()
    if (offset > center && offset < toolbarHeight) {
        collapse(duration)
    } else if (offset <= center && offset > 0f) {
        expand(duration)
    }
}

@OptIn(ExperimentalToolbarApi::class)
suspend fun CollapsingToolbarState.performSnap(duration: Int = 350) {
    val center = 0.5f
    if (progress > center && progress < 1f) {
        expand(duration)
    } else if (progress <= center && progress > 0f) {
        collapse(duration)
    }
}

val CollapsingToolbarScaffoldState.offsetYInverted
    get() = toolbarState.minHeight + offsetY

// Грязный хак с рефлексией для получения стейта офсета туллбара
@Suppress("UNCHECKED_CAST")
private fun CollapsingToolbarScaffoldState.getPrivateOffsetYState(): MutableState<Int> =
    this::class.java.getDeclaredMethod("getOffsetYState\$lib_release")
        .invoke(this) as MutableState<Int>
