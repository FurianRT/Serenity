package com.furianrt.uikit.anim

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry

fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultEnterTransition(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        initialOffset = { it / 2 },
        animationSpec = tween(
            durationMillis = 450,
            easing = FastOutSlowInEasing,
        ),
    ) + fadeIn(animationSpec = tween(450))
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultExitTransition(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        targetOffset = { (it * 0.1f).toInt() },
        animationSpec = tween(
            durationMillis = 400,
            easing = LinearEasing,
        ),
    ) + fadeOut(animationSpec = tween(400), targetAlpha = 0.2f)
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultPopExitTransition(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        targetOffset = { (it * 0.8f).toInt() },
        animationSpec = tween(
            durationMillis = 400,
            easing = LinearEasing,
        ),
    ) + fadeOut(animationSpec = tween(300))
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultPopEnterTransition(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        initialOffset = { (it * 0.1f).toInt() },
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing,
        ),
    ) + fadeIn(animationSpec = tween(400), initialAlpha = 0.2f)
}