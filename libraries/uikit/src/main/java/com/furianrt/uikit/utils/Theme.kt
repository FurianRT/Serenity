package com.furianrt.uikit.utils

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import com.materialkolor.dynamicColorScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetectThemeResult(
    val isDark: Boolean,
    val primary: Color,
    val secondary: Color,
)

suspend fun Bitmap.detectTheme(): DetectThemeResult = withContext(Dispatchers.Default) {
    val safeBitmap = if (config == Bitmap.Config.HARDWARE) {
        copy(Bitmap.Config.ARGB_8888, false)
    } else {
        this@detectTheme
    }
    val palette = Palette.from(safeBitmap)
        .clearFilters()
        .maximumColorCount(24)
        .generate()

    val isDark = palette.isDarkTheme()

    val dominantColor = extractDominantColor(
        palette = palette,
        fallback = Color.Gray,
    )

    val scheme = dynamicColorScheme(
        seedColor = dominantColor,
        isDark = isDark,
    )

    DetectThemeResult(
        isDark = isDark,
        primary = dominantColor,
        secondary = scheme.inversePrimary,
    )
}

private fun extractDominantColor(
    palette: Palette,
    fallback: Color,
): Color {
    val dominantColor = palette.swatches.maxByOrNull { it.population }?.rgb ?: fallback.toArgb()
    return Color(dominantColor)
}

private fun Palette.isDarkTheme(): Boolean {
    val swatches = listOfNotNull(dominantSwatch, vibrantSwatch, mutedSwatch)

    if (swatches.isEmpty()) return false

    val totalPopulation = swatches.sumOf(Palette.Swatch::getPopulation)

    val luminance = swatches.sumOf { swatch ->
        val lum = ColorUtils.calculateLuminance(swatch.rgb)
        lum * swatch.population
    } / totalPopulation

    return luminance < 0.45
}
