package com.furianrt.notelistui.entities

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.furianrt.uikit.theme.Colors

@Immutable
enum class UiNoteFontColor(val value: Color) {
    BLACK(Colors.Font.Black),

    WHITE(Colors.Font.White),

    GREY(Colors.Font.Grey),

    BLUE(Colors.Font.Blue),
    BLUE_DARK(Colors.Font.BlueDark),
    SKY(Colors.Font.Sky),
    INDIGO(Colors.Font.Indigo),

    PURPLE_LIGHT(Colors.Font.PurpleLight),
    PURPLE(Colors.Font.Purple),
    PURPLE_DARK(Colors.Font.PurpleDark),
    LAVENDER(Colors.Font.Lavender),

    PINK_LIGHT(Colors.Font.PinkLight),
    PINK_DARK(Colors.Font.PinkDark),

    STEEL(Colors.Font.Steel),
    NAVI(Colors.Font.Navy),

    GREEN_LIGHT(Colors.Font.GreenLight),
    GREEN(Colors.Font.Green),
    GREEN_DARK(Colors.Font.GreenDark),
    BLUE_LIGHT(Colors.Font.BlueLight),

    CORAL(Colors.Font.Coral),
    PEACH(Colors.Font.Peach),
    CORAL_DARK(Colors.Font.CoralDark),
    RED(Colors.Font.Red),
    RED_DARK(Colors.Font.RedDark),

    MINT(Colors.Font.Mint),
    MINT_DARK(Colors.Font.MintDark),
    TEAL(Colors.Font.Teal),

    YELLOW_LIGHT(Colors.Font.YellowLight),
    YELLOW_DARK(Colors.Font.YellowDark),
    ORANGE(Colors.Font.Orange),
    AMBER(Colors.Font.Amber),
    BROWN(Colors.Font.Brown),
    CHOCOLATE(Colors.Font.Chocolate),

    SAGE(Colors.Font.Sage),
    OLIVE(Colors.Font.Olive),
    KHAKI(Colors.Font.Khaki),
    SAND(Colors.Font.Sand);


    companion object {
        fun fromColor(color: Color): UiNoteFontColor {
            return entries.find { it.value == color } ?: WHITE
        }
    }
}
