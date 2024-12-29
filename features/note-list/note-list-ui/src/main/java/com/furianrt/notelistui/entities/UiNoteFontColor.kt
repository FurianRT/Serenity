package com.furianrt.notelistui.entities

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.furianrt.uikit.theme.Colors

@Immutable
enum class UiNoteFontColor(val value: Color) {
    WHITE(Colors.Font.White),
    ORANGE(Colors.Font.Orange),
    GREEN(Colors.Font.Green),
    BLUE_LIGHT(Colors.Font.BlueLight),
    BLUE(Colors.Font.Blue),
    BLUE_DARK(Colors.Font.BlueDark),
    PURPLE(Colors.Font.Purple),
    PURPLE_DARK(Colors.Font.PurpleDark);
}