package com.furianrt.uikit.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.furianrt.uikit.entities.UiThemeColor

private val defaultColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.White,
    secondary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
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

private val greenColorScheme = defaultColorScheme.copy(
    background = Color.Green,
    surface = Color.Green,
    primaryContainer = Colors.GreenLight,
)

private val blackColorScheme = greenColorScheme.copy(
    background = Color.Black,
    surface = Color.Black,
    primaryContainer = Colors.GreenLight,
)

private val blueColorScheme = greenColorScheme.copy(
    background = Colors.FutureDusk,
    surface = Colors.FutureDusk,
    primaryContainer = Colors.GreenLight,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SerenityTheme(
    color: UiThemeColor = UiThemeColor.GREEN,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (color) {
        UiThemeColor.GREEN -> greenColorScheme
        UiThemeColor.BLACK -> blackColorScheme
        UiThemeColor.BLUE -> blueColorScheme
    }

    val animatedBackground by animateColorAsState(
        animationSpec = tween(250),
        targetValue = colorScheme.background,
        label = "BackgroundColorAnim",
    )
    val animatedSurface by animateColorAsState(
        animationSpec = tween(250),
        targetValue = colorScheme.surface,
        label = "SurfaceColorAnim",
    )

    MaterialTheme(
        colorScheme = colorScheme.copy(
            background = animatedBackground,
            surface = animatedSurface,
        ),
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
