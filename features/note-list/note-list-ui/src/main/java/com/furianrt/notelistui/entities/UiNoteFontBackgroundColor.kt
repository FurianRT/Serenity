package com.furianrt.notelistui.entities

import androidx.compose.ui.graphics.Color
import com.furianrt.uikit.theme.Colors

enum class UiNoteFontBackgroundColor(val value: Color) {
    PRUSSIAN_BLUE(Colors.FontBackground.Blue),
    INDIGO(Colors.FontBackground.Indigo),
    PURPLE(Colors.FontBackground.Purple),
    PINKISH_RED(Colors.FontBackground.PinkishRed),
    DEEP_ORANGE(Colors.FontBackground.DeepOrange),
    TEAL(Colors.FontBackground.Teal),
    FOREST_GREEN(Colors.FontBackground.ForestGreen),
    AMBER(Colors.FontBackground.Amber),
    ORANGE(Colors.FontBackground.Orange),
    LIGHT_BLUE(Colors.FontBackground.LightBlue),
    CRIMSON(Colors.FontBackground.Crimson),
    DEEP_PURPLE(Colors.FontBackground.DeepPurple),
    BROWN(Colors.FontBackground.Brown);

    companion object {
        fun fromColor(color: Color?): UiNoteFontBackgroundColor? {
            return UiNoteFontBackgroundColor.entries.find { it.value == color }
        }
    }
}