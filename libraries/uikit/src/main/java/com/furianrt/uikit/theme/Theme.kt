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
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.constants.SystemBarsConstants
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.entities.colorScheme
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.glass.ChromaticAberrationMode
import dev.chrisbanes.haze.glass.GlassOptics
import dev.chrisbanes.haze.glass.GlassStyle
import dev.chrisbanes.haze.glass.LocalGlassStyle
import dev.chrisbanes.haze.glass.OpticalSizeValue
import dev.chrisbanes.haze.glass.RefractionProfile
import dev.chrisbanes.haze.glass.SurfaceProfile

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
    onSurfaceVariant = primaryContainer.copy(alpha = 0.3f),
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
    onSurfaceVariant = primaryContainer.copy(alpha = 0.2f),
    outlineVariant = primaryContainer.copy(alpha = 0.15f),
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

val LocalColorScheme = compositionLocalOf { UiThemeColor.defaultTheme.colorScheme }
val LocalIsLightTheme = compositionLocalOf { false }
val LocalFont = compositionLocalOf<NoteFont> { NoteFont.NotoSans }
val LocalHasMediaRoute = compositionLocalOf { false }
val LocalHasMediaSortingRoute = compositionLocalOf { false }
val LocalSerenityPlus = compositionLocalOf { false }

data object GlassDefaults {

    @OptIn(ExperimentalHazeApi::class)
    val optics: GlassOptics = GlassOptics(
        refractionStrength = 0.6f,
        refractionHeightFraction = 0.25f,
        refractionDisplacement = 32.dp,
        refractionProfile = RefractionProfile.Surface,
        depth = OpticalSizeValue.Fixed(0.8f),
        blurRadius = OpticalSizeValue.Fixed(16.dp),
        refractionDetailIntensity = 0.76f,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeApi::class)
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
        val statusBarColor = SystemBarsConstants.StatusBarColor.toArgb()
        val navigationBarColor = SystemBarsConstants.NavigationBarColor.toArgb()
        if (isLightTheme) {
            activity?.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.light(
                    scrim = statusBarColor,
                    darkScrim = statusBarColor
                ),
                navigationBarStyle = SystemBarStyle.light(
                    scrim = navigationBarColor,
                    darkScrim = navigationBarColor
                ),
            )
        } else {
            activity?.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.dark(statusBarColor),
                navigationBarStyle = SystemBarStyle.dark(navigationBarColor),
            )
        }
    }

    MaterialTheme(
        colorScheme = resultColorTheme,
        typography = typography,
    ) {
        val rippleConfig = RippleConfiguration(
            color = MaterialTheme.colorScheme.primaryContainer,
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

        val glassStyle = remember(isLightTheme) {
            GlassStyle {
                optics(GlassDefaults.optics)

                edgeShadow(Color.Transparent)
                edgeSoftness(0.dp)

                ambientResponse(1f)

                surfaceProfile(SurfaceProfile.Circle)

                contrast(if (isLightTheme) -0.05f else 0f)

                whitePoint(0.03f)

                chromaMultiplier(1f)
                chromaticAberrationStrength(1f)
                chromaticAberrationMode(ChromaticAberrationMode.Simple)

                contentNormalBlend(1f)

                specularExponent(0f)
                specularIntensity(0f)

                fresnelExponent(2.5f)
            }
        }

        CompositionLocalProvider(
            LocalRippleConfiguration provides rippleConfig,
            LocalTextSelectionColors provides textSelectionColors,
            LocalContentColor provides resultColorTheme.onSurface,
            LocalIsLightTheme provides isLightTheme,
            LocalColorScheme provides resultColorTheme,
            LocalFont provides font,
            LocalGlassStyle provides glassStyle,
            content = content,
        )
    }
}
