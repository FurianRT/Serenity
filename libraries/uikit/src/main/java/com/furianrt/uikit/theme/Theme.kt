package com.furianrt.uikit.theme

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.furianrt.uikit.constants.SystemBarsConstants
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.entities.colorScheme

private const val THEME_COLOR_ANIM_DURATION = 250

fun getDefaultDarkColorScheme(
    surface: Color,
    primaryContainer: Color,
) = darkColorScheme(
    surface = surface,
    primaryContainer = primaryContainer,
    primary = Color.White,
    onPrimary = Color.White,
    secondary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    secondaryContainer = Color.White.copy(alpha = 0.05f),
    inverseSurface = Color.White.copy(alpha = 0.05f),
    onSurface = Color.White,
    onPrimaryContainer = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerLowest = surface,
    onSurfaceVariant = primaryContainer.copy(alpha = 0.2f),
    outlineVariant = Color.White.copy(alpha = 0.05f),
    background = Color.White.copy(alpha = 0.1f),
    tertiary = Color.White.copy(alpha = 0.1f),
    onTertiary = Color.White,
    tertiaryContainer = Color.White.copy(alpha = 0.2f),
    onTertiaryContainer = Color.White.copy(alpha = 0.3f),
    errorContainer = Color(0xFFF2402F),
    onErrorContainer = Color.White,
    scrim = Color.Black.copy(alpha = 0.5f),
    surfaceTint = Color.Transparent,
    surfaceDim = Color.Black.copy(alpha = 0.1f),
    surfaceContainerLow = Color.White.copy(alpha = 0.5f),
)

fun getDefaultLightColorScheme(
    surface: Color,
    primaryContainer: Color,
) = lightColorScheme(
    surface = surface,
    primaryContainer = primaryContainer,
    onPrimaryContainer = Color.White,
    primary = Color.Black,
    onPrimary = Color.Black,
    secondary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    secondaryContainer = Color.White.copy(alpha = 0.35f),
    inverseSurface = primaryContainer.copy(alpha = 0.9f),
    onSurface = Color.Black,
    surfaceContainer = primaryContainer,
    surfaceContainerLowest = surface,
    onSurfaceVariant = primaryContainer.copy(alpha = 0.1f),
    outlineVariant =primaryContainer.copy(alpha = 0.15f),
    background = Color.White.copy(alpha = 0.35f),
    tertiary = primaryContainer.copy(alpha = 0.15f),
    onTertiary = Color.Black,
    tertiaryContainer = primaryContainer.copy(alpha = 0.2f),
    onTertiaryContainer = primaryContainer.copy(alpha = 0.25f),
    errorContainer = Color(0xFFF2402F),
    onErrorContainer = Color.White,
    scrim = Color.Black.copy(alpha = 0.35f),
    surfaceTint = Color.Transparent,
    surfaceDim = Color.DarkGray.copy(alpha = 0.1f),
    surfaceContainerLow = Color.Black.copy(alpha = 0.3f),
)

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

    val colorTransition = updateTransition(targetState = colorScheme)

    val animatedPrimaryContainer by colorTransition.animateColor(
        transitionSpec = {
            tween(
                durationMillis = THEME_COLOR_ANIM_DURATION,
                easing = LinearEasing,
            )
        },
        targetValueByState = { it.primaryContainer },
    )
    val animatedSurface by colorTransition.animateColor(
        transitionSpec = {
            tween(
                durationMillis = THEME_COLOR_ANIM_DURATION,
                easing = LinearEasing,
            )
        },
        targetValueByState = { it.surface },
    )
    val animatedOnSurface by colorTransition.animateColor(
        transitionSpec = {
            tween(
                durationMillis = THEME_COLOR_ANIM_DURATION,
                easing = LinearEasing,
            )
        },
        targetValueByState = { it.onSurface },
    )
    val animatedSurfaceContainer by colorTransition.animateColor(
        transitionSpec = {
            tween(
                durationMillis = THEME_COLOR_ANIM_DURATION,
                easing = LinearEasing,
            )
        },
        targetValueByState = { it.surfaceContainer },
    )
    val animatedBackground by colorTransition.animateColor(
        transitionSpec = {
            tween(
                durationMillis = THEME_COLOR_ANIM_DURATION,
                easing = LinearEasing,
            )
        },
        targetValueByState = { it.background },
    )
    val animatedSecondaryContainer by colorTransition.animateColor(
        transitionSpec = {
            tween(
                durationMillis = THEME_COLOR_ANIM_DURATION,
                easing = LinearEasing,
            )
        },
        targetValueByState = { it.secondaryContainer },
    )
    val animatedTertiary by colorTransition.animateColor(
        transitionSpec = {
            tween(
                durationMillis = THEME_COLOR_ANIM_DURATION,
                easing = LinearEasing,
            )
        },
        targetValueByState = { it.tertiary },
    )
    val animatedTertiaryContainer by colorTransition.animateColor(
        transitionSpec = {
            tween(
                durationMillis = THEME_COLOR_ANIM_DURATION,
                easing = LinearEasing,
            )
        },
        targetValueByState = { it.tertiaryContainer },
    )
    val animatedOnTertiaryContainer by colorTransition.animateColor(
        transitionSpec = {
            tween(
                durationMillis = THEME_COLOR_ANIM_DURATION,
                easing = LinearEasing,
            )
        },
        targetValueByState = { it.onTertiaryContainer },
    )

    val typography = remember(font) { getTypography(font) }

    val resultColorTheme = colorScheme.copy(
        primaryContainer = animatedPrimaryContainer,
        surface = animatedSurface,
        onSurface = animatedOnSurface,
        surfaceContainer = animatedSurfaceContainer,
        background = animatedBackground,
        secondaryContainer = animatedSecondaryContainer,
        tertiary = animatedTertiary,
        tertiaryContainer = animatedTertiaryContainer,
        onTertiaryContainer = animatedOnTertiaryContainer,
    )

    LaunchedEffect(isLightTheme) {
        val color = SystemBarsConstants.InsetsColor.toArgb()
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
