package com.furianrt.serenity.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.furianrt.notelist.api.NoteListRoute

@Composable
internal fun SerenityNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    builder: NavGraphBuilder.() -> Unit,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NoteListRoute,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                initialOffset = { it / 2 },
                animationSpec = tween(
                    durationMillis = 450,
                    easing = FastOutSlowInEasing,
                ),
            ) + fadeIn(animationSpec = tween(450))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                targetOffset = { (it * 0.1f).toInt() },
                animationSpec = tween(
                    durationMillis = 400,
                    easing = LinearEasing,
                ),
            ) + fadeOut(animationSpec = tween(400), targetAlpha = 0.2f)
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                targetOffset = { (it * 0.8f).toInt() },
                animationSpec = tween(
                    durationMillis = 400,
                    easing = LinearEasing,
                ),
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                initialOffset = { (it * 0.1f).toInt() },
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing,
                ),
            ) + fadeIn(animationSpec = tween(400), initialAlpha = 0.2f)
        },
        builder = builder,
    )
}