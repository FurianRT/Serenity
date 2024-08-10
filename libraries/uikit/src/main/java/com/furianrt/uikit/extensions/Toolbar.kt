package com.furianrt.uikit.extensions

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import kotlin.math.abs
import kotlin.math.absoluteValue

val CollapsingToolbarScaffoldState.isExpanded
    get() = offsetY == 0

val CollapsingToolbarScaffoldState.isCollapsed
    get() = offsetY.absoluteValue == toolbarState.minHeight

val CollapsingToolbarScaffoldState.isInMiddleState
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

val CollapsingToolbarScaffoldState.offsetYInverted
    get() = toolbarState.minHeight + offsetY

// Грязный хак с рефлексией для получения стейта офсета туллбара
@Suppress("UNCHECKED_CAST")
private fun CollapsingToolbarScaffoldState.getPrivateOffsetYState(): MutableState<Int> =
    this::class.java.getDeclaredMethod("getOffsetYState\$lib_release")
        .invoke(this) as MutableState<Int>
