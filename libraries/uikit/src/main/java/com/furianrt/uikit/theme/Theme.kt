package com.furianrt.uikit.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.furianrt.uikit.entities.UiThemeColor

private const val COLOR_ANIM_DURATION = 250

private val defaultColorScheme = darkColorScheme()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SerenityTheme(
    color: UiThemeColor = UiThemeColor.DISTANT_CASTLE_GREEN,
    font: NoteFont = NoteFont.QuickSand,
    content: @Composable () -> Unit,
) {
    val colorScheme = defaultColorScheme.copy(
        primary = color.primary,
        onPrimary = color.onPrimary,
        secondary = color.secondary,
        onSecondary = color.onSecondary,
        onBackground = color.onBackground,
        onSurface = color.onSurface,
        onSurfaceVariant = color.onSurfaceVariant,
        surfaceContainer = color.surfaceContainer,
        onPrimaryContainer = color.onPrimaryContainer,
        outlineVariant = color.outlineVariant,
        secondaryContainer = color.secondaryContainer,
        tertiary = color.tertiary,
        onTertiary = color.onTertiary,
        tertiaryContainer = color.tertiaryContainer,
        onTertiaryContainer = color.onTertiaryContainer,
        errorContainer = color.errorContainer,
        onErrorContainer = color.onErrorContainer,
        scrim = color.scrim,
        background = color.background,
        surface = color.surface,
        primaryContainer = color.primaryContainer,
        surfaceTint = color.surfaceTint,
        surfaceDim = color.surfaceDim,
    )

    val animatedPrimaryContainer by animateColorAsState(
        animationSpec = tween(COLOR_ANIM_DURATION),
        targetValue = colorScheme.primaryContainer,
    )
    val animatedSurface by animateColorAsState(
        animationSpec = tween(COLOR_ANIM_DURATION),
        targetValue = colorScheme.surface,
    )
    val animatedOnSurface by animateColorAsState(
        animationSpec = tween(COLOR_ANIM_DURATION),
        targetValue = colorScheme.onSurface,
    )
    val animatedSurfaceContainer by animateColorAsState(
        animationSpec = tween(COLOR_ANIM_DURATION),
        targetValue = colorScheme.surfaceContainer,
    )

    val typography = remember(font) { getTypography(font) }

    val resultColorTheme = colorScheme.copy(
        primaryContainer = animatedPrimaryContainer,
        surface = animatedSurface,
        onSurface = animatedOnSurface,
        surfaceContainer = animatedSurfaceContainer,
    )

    MaterialTheme(
        colorScheme = resultColorTheme,
        typography = typography,
    ) {
        val rippleConfig = RippleConfiguration(
            color = MaterialTheme.colorScheme.surfaceContainer,
            rippleAlpha = RippleAlpha(
                draggedAlpha = 0.1f,
                focusedAlpha = 0.1f,
                hoveredAlpha = 0.1f,
                pressedAlpha = 0.1f,
            ),
        )
        val textSelectionColors = TextSelectionColors(
            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
            handleColor = MaterialTheme.colorScheme.surfaceContainer,
        )
        CompositionLocalProvider(
            LocalRippleConfiguration provides rippleConfig,
            LocalTextSelectionColors provides textSelectionColors,
            LocalContentColor provides resultColorTheme.onSurface,
            content = content,
        )
    }
}
