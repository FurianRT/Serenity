package com.furianrt.uikit.theme

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.entities.colorScheme

private const val COLOR_ANIM_DURATION = 250

internal val defaultColorScheme = darkColorScheme()

val LocalColorScheme = compositionLocalOf { UiThemeColor.STORM_IN_THE_NIGHT_BLUE_LIGHT.colorScheme }
val LocalIsLightTheme = compositionLocalOf { false }
val LocalFont = compositionLocalOf<NoteFont> { NoteFont.NotoSans }
val LocalHasMediaRoute = compositionLocalOf { false }
val LocalHasMediaSortingRoute = compositionLocalOf { false }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SerenityTheme(
    colorScheme: ColorScheme = LocalColorScheme.current,
    font: NoteFont = LocalFont.current,
    isLightTheme: Boolean = LocalIsLightTheme.current,
    content: @Composable () -> Unit,
) {
    val activity = LocalActivity.current as? ComponentActivity

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

    val animatedBackground by animateColorAsState(
        animationSpec = tween(COLOR_ANIM_DURATION),
        targetValue = colorScheme.background,
    )

    val typography = remember(font) { getTypography(font) }

    val resultColorTheme = colorScheme.copy(
        primaryContainer = animatedPrimaryContainer,
        surface = animatedSurface,
        onSurface = animatedOnSurface,
        surfaceContainer = animatedSurfaceContainer,
        background = animatedBackground,
    )

    LaunchedEffect(isLightTheme) {
        val color = Color.Transparent.toArgb()
        if (isLightTheme) {
            activity?.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.light(scrim = color, darkScrim = color),
                navigationBarStyle = SystemBarStyle.light(
                    scrim = color,
                    darkScrim = color
                ),
            )
        } else {
            activity?.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.dark(color),
                navigationBarStyle = SystemBarStyle.dark(color),
            )
        }
    }

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
            LocalIsLightTheme provides isLightTheme,
            LocalColorScheme provides resultColorTheme,
            LocalFont provides font,
            content = content,
        )
    }
}
