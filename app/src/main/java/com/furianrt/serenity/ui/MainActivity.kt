package com.furianrt.serenity.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.window.DialogProperties
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.furianrt.mediaselector.api.MediaSelectorBottomSheet
import com.furianrt.noteview.api.NoteViewScreen
import com.furianrt.settings.api.SettingsScreen
import com.furianrt.storage.api.repositories.MediaRepository
import com.furianrt.storage.api.repositories.mediaAccessDenied
import com.furianrt.uikit.theme.SerenityTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val SPLASH_SCREEN_EXIT_ANIM_DURATION = 250L
private val SYSTEM_BARS_COLOR = Color.argb(0x4D, 0x1b, 0x1b, 0x1b)

@AndroidEntryPoint
internal class MainActivity : ComponentActivity() {

    @Inject
    lateinit var mediaRepository: MediaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
            .setOnExitAnimationListener { splashScreenViewProvider ->
                splashScreenViewProvider.view
                    .animate()
                    .alpha(0f)
                    .duration = SPLASH_SCREEN_EXIT_ANIM_DURATION

                splashScreenViewProvider.view
                    .animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .duration = SPLASH_SCREEN_EXIT_ANIM_DURATION

                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.dark(SYSTEM_BARS_COLOR),
                    navigationBarStyle = SystemBarStyle.dark(SYSTEM_BARS_COLOR),
                )
            }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(SYSTEM_BARS_COLOR),
            navigationBarStyle = SystemBarStyle.dark(SYSTEM_BARS_COLOR),
        )

        setContent {
            SerenityTheme {
                val navController = rememberNavController()

                LifecycleStartEffect(lifecycleOwner = this, key1 = Unit) {
                    val currentRoute = navController.currentDestination?.route
                    if (currentRoute == "Sheet" && mediaRepository.mediaAccessDenied()) {
                        navController.popBackStack()
                    }
                    onStopOrDispose {}
                }

                NavHost(
                    navController = navController,
                    startDestination = "Main",
                ) {
                    composable(
                        route = "Main",
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
                        content = { MainScreen(navController) },
                    )

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
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                initialOffset = { it / 2 },
                                animationSpec = tween(
                                    durationMillis = 450,
                                    easing = FastOutSlowInEasing,
                                ),
                            ) + fadeIn(animationSpec = tween(450))
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
                        content = { NoteViewScreen(navController) },
                    )

                    composable(
                        route = "Settings",
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
                        content = { SettingsScreen(navController) },
                    )

                    dialog(
                        route = "Sheet" + "/{noteId}" + "/{blockId}",
                        arguments = listOf(
                            navArgument("noteId") {
                                type = NavType.StringType
                                nullable = false
                            },
                            navArgument("blockId") {
                                type = NavType.StringType
                                nullable = false
                            },
                        ),
                        dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
                        content = {
                            MediaSelectorBottomSheet(
                                navHostController = navController,
                            )
                        },
                    )
                }
            }
        }
    }
}
