package com.furianrt.uikit.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val darkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.White,
    secondary = Colors.GreenLight,
    onSecondary = Color.White,
    surface = Colors.Green,
    onSurface = Color.White,
    primaryContainer = Colors.GreenLight,
    onPrimaryContainer = Color.White,
    tertiary = Color.White.copy(alpha = 0.1f),
    onTertiary = Color.White,
    tertiaryContainer = Color.White.copy(alpha = 0.2f),
    onTertiaryContainer = Color.White,
    errorContainer = Colors.Red,
    onErrorContainer = Color.White,
)

private val lightColorScheme = lightColorScheme(
    primary = Color.White,
    onPrimary = Color.White,
    secondary = Colors.GreenLight,
    onSecondary = Color.White,
    surface = Colors.Green,
    onSurface = Color.White,
    primaryContainer = Colors.GreenLight,
    onPrimaryContainer = Color.White,
    tertiary = Color.White.copy(alpha = 0.1f),
    onTertiary = Color.White,
    tertiaryContainer = Color.White.copy(alpha = 0.2f),
    onTertiaryContainer = Color.White,
    errorContainer = Colors.Red,
    onErrorContainer = Color.White,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SerenityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }
    val view = LocalView.current
    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        window.setDecorFitsSystemWindows(false)
        window.statusBarColor = Color.Black.copy(alpha = 0.25f).toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
    ) {
        CompositionLocalProvider(
            LocalRippleConfiguration provides RippleConfiguration(color = MaterialTheme.colorScheme.onPrimary, rippleAlpha = null),
            content = content,
        )
    }
}
