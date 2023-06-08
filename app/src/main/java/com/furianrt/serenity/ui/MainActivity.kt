package com.furianrt.serenity.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.furianrt.uikit.theme.SerenityTheme
import dagger.hilt.android.AndroidEntryPoint

private const val SPLASH_SCREEN_EXIT_ANIM_DURATION = 450L

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
                MainScreen()
            }
        }
    }
}
