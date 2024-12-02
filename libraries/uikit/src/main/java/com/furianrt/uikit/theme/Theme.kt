package com.furianrt.uikit.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val darkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.White,
    secondary = Color.White,
    onSecondary = Color.White,
    background = Colors.Green,
    onBackground = Color.White,
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
    scrim = Color.Black.copy(alpha = 0.4f),
    surfaceDim = Color.Black.copy(alpha = 0.5f),
)

private val lightColorScheme = lightColorScheme(
    primary = Color.White,
    onPrimary = Color.White,
    secondary = Color.White,
    onSecondary = Color.White,
    background = Colors.Green,
    onBackground = Color.White,
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
    scrim = Color.Black.copy(alpha = 0.4f),
    surfaceDim = Color.Black.copy(alpha = 0.5f),
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
    ) {
        val rippleAlpha = RippleAlpha(
            draggedAlpha = 0.1f,
            focusedAlpha = 0.1f,
            hoveredAlpha = 0.1f,
            pressedAlpha = 0.1f,
        )
        val rippleConfig = RippleConfiguration(MaterialTheme.colorScheme.onPrimary, rippleAlpha)
        CompositionLocalProvider(LocalRippleConfiguration provides rippleConfig, content)
    }
}
