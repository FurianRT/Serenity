package com.furianrt.notelistui.entities

import androidx.annotation.ColorInt
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.furianrt.uikit.theme.Colors

@Immutable
enum class UiNoteFontColor(val value: Color) {
    WHITE(Colors.Font.White),

    GREY_LIGHT(Colors.Font.GreyLight),
    GREY(Colors.Font.Grey),
    GREY_DARK(Colors.Font.GreyDark),

    PINK_LIGHT(Colors.Font.PinkLight),
    PINK(Colors.Font.Pink),
    PINK_DARK(Colors.Font.PinkDark),

    YELLOW_LIGHT(Colors.Font.YellowLight),
    YELLOW(Colors.Font.Yellow),
    YELLOW_DARK(Colors.Font.YellowDark),

    GREEN_LIGHT(Colors.Font.GreenLight),
    GREEN(Colors.Font.Green),
    GREEN_DARK(Colors.Font.GreenDark),

    BLUE_LIGHT(Colors.Font.BlueLight),
    BLUE(Colors.Font.Blue),
    BLUE_DARK(Colors.Font.BlueDark),

    PURPLE_LIGHT(Colors.Font.PurpleLight),
    PURPLE(Colors.Font.Purple),
    PURPLE_DARK(Colors.Font.PurpleDark),

    RED_LIGHT(Colors.Font.RedLight),
    RED(Colors.Font.Red),
    RED_DARK(Colors.Font.RedDark);

    companion object {
        fun fromColor(@ColorInt color: Int): UiNoteFontColor {
            return entries.find { it.value.toArgb() == color } ?: WHITE
        }
    }
}
