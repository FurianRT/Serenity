package com.furianrt.uikit.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
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

private const val BACKGROUND_COLOR_ANIM_DURATION = 250

private val defaultColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.White,
    secondary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onPrimaryContainer = Color.White,
    outlineVariant = Color.White.copy(alpha = 0.05f),
    tertiary = Color.White.copy(alpha = 0.1f),
    onTertiary = Color.White,
    tertiaryContainer = Color.White.copy(alpha = 0.2f),
    onTertiaryContainer = Color.White.copy(alpha = 0.3f),
    errorContainer = Color(0xFFF2402F),
    onErrorContainer = Color.White,
    scrim = Color.Black.copy(alpha = 0.5f),
    surfaceDim = Color.Black.copy(alpha = 0.2f),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SerenityTheme(
    color: UiThemeColor = UiThemeColor.DISTANT_CASTLE_GREEN,
    content: @Composable () -> Unit,
) {
    val colorScheme = defaultColorScheme.copy(
        background = color.primary,
        surface = color.primary,
        primaryContainer = color.accent,
        surfaceTint = color.surfaceTint,
        surfaceDim = color.surfaceDim,
    )

    val animatedBackground by animateColorAsState(
        animationSpec = tween(BACKGROUND_COLOR_ANIM_DURATION),
        targetValue = colorScheme.background,
        label = "BackgroundColorAnim",
    )
    val animatedSurface by animateColorAsState(
        animationSpec = tween(BACKGROUND_COLOR_ANIM_DURATION),
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
        val rippleConfig = RippleConfiguration(MaterialTheme.colorScheme.onSurface, rippleAlpha)
        val textSelectionColors = TextSelectionColors(
            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
            handleColor = MaterialTheme.colorScheme.onSurface,
        )
        CompositionLocalProvider(
            LocalRippleConfiguration provides rippleConfig,
            LocalTextSelectionColors provides textSelectionColors,
            content = content,
        )
    }
}
