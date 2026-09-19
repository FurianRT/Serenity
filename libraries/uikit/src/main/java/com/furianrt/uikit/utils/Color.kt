package com.furianrt.uikit.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

fun Color.brighterBy(percent: Float): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsl)
    hsl[2] = (hsl[2] + percent).coerceIn(0f, 1f)
    return Color(ColorUtils.HSLToColor(hsl))
}

fun Color.shiftToAccent(
    hueShift: Float = 26f,
    saturation: Float = 0.80f,
    lightness: Float = 0.60f,
): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(toArgb(), hsl)

    hsl[0] = (hsl[0] + hueShift) % 360f
    hsl[1] = saturation.coerceIn(0f, 1f)
    hsl[2] = lightness.coerceIn(0f, 1f)

    return Color(ColorUtils.HSLToColor(hsl))
}
