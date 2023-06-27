package com.furianrt.serenity.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.furianrt.noteview.api.NoteViewScreen
import com.furianrt.uikit.theme.SerenityTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint

private const val SPLASH_SCREEN_EXIT_ANIM_DURATION = 450L

@AndroidEntryPoint
internal class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setOnExitAnimationListener { splashScreenViewProvider ->
            splashScreenViewProvider.view
                .animate()
                .alpha(0f)
                .duration = SPLASH_SCREEN_EXIT_ANIM_DURATION
        }
        setContent {
            SerenityTheme {
                val navController = rememberAnimatedNavController()
                AnimatedNavHost(
                    navController = navController,
                    startDestination = "Main",
                ) {
                    composable(
                        route = "Main",
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentScope.SlideDirection.Left,
                                targetOffset = { (it * 0.1f).toInt() },
                                animationSpec = tween(
                                    durationMillis = 400,
                                    easing = LinearEasing,
                                ),
                            ) + fadeOut(animationSpec = tween(400), targetAlpha = 0.2f)
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentScope.SlideDirection.Right,
                                initialOffset = { (it * 0.1f).toInt() },
                                animationSpec = tween(
                                    durationMillis = 400,
                                    easing = FastOutSlowInEasing,
                                ),
                            ) + fadeIn(animationSpec = tween(400), initialAlpha = 0.2f)
                        },
                    ) {
                        MainScreen(navController)
                    }
                    composable(
                        route = "Note" + "/{noteId}",
                        arguments = listOf(
                            navArgument("noteId") {
                                type = NavType.StringType
                                defaultValue = null
                                nullable = true
                            },
                        ),
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentScope.SlideDirection.Left,
                                initialOffset = { it / 2 },
                                animationSpec = tween(
                                    durationMillis = 450,
                                    easing = FastOutSlowInEasing,
                                ),
                            ) + fadeIn(animationSpec = tween(450))
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentScope.SlideDirection.Right,
                                targetOffset = { (it * 0.8f).toInt() },
                                animationSpec = tween(
                                    durationMillis = 400,
                                    easing = LinearEasing,
                                ),
                            ) + fadeOut(animationSpec = tween(300))
                        },
                    ) {
                        NoteViewScreen(navController)
                    }
                }
            }
        }
    }
}
