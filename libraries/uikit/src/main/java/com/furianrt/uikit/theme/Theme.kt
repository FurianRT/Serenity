package com.furianrt.uikit.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.MaterialTheme
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
import com.furianrt.uikit.extensions.div

private val darkColorScheme = darkColorScheme(
    primary = Colors.Blue,
    onPrimary = Colors.White,
    secondary = Colors.Blue,
    onSecondary = Colors.White,
    surface = Colors.Blue,
    onSurface = Colors.White,
    primaryContainer = Colors.Blue,
    onPrimaryContainer = Colors.White,
    tertiary = Colors.WhiteAlpha5,
    onTertiary = Colors.White,
    errorContainer = Colors.Red,
    onErrorContainer = Colors.White,
)

private val lightColorScheme = lightColorScheme(
    primary = Colors.Blue,
    onPrimary = Colors.White,
    secondary = Colors.Blue,
    onSecondary = Colors.White,
    surface = Colors.Blue,
    onSurface = Colors.White,
    primaryContainer = Colors.Blue,
    onPrimaryContainer = Colors.White,
    tertiary = Colors.WhiteAlpha5,
    onTertiary = Colors.White,
    errorContainer = Colors.Red,
    onErrorContainer = Colors.White,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun SerenityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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
        window.statusBarColor = Colors.BlackAlpha25.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
    ) {
        CompositionLocalProvider(
            LocalRippleTheme provides SerenityRippleTheme(),
            content = content,
        )
    }
}

open class SerenityRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor(): Color = MaterialTheme.colorScheme.onPrimary

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleTheme.defaultRippleAlpha(
        MaterialTheme.colorScheme.primary,
        lightTheme = !isSystemInDarkTheme(),
    )
}

object OnTertiaryRippleTheme : SerenityRippleTheme() {
    @Composable
    override fun rippleAlpha() = super.rippleAlpha() / 2f
}
