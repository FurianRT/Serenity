package com.furianrt.serenity.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
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
                    ) {
                        NoteViewScreen(navController)
                    }
                }
            }
        }
    }
}
