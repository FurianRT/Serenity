package com.furianrt.uikit.entities

import androidx.compose.ui.graphics.Color
import com.furianrt.uikit.theme.Colors

enum class UiThemeColor(
    val id: String,
    val primary: Color,
    val accent: Color,
    val surfaceTint: Color = primary.copy(alpha = 0.3f),
    val surfaceDim: Color = Color.Black.copy(alpha = 0.2f),
) {
    SCANDI_GRANDPA_GRAY_DARK(
        id = "1",
        primary = Colors.Primary.ScandiGrandpaGrayDark,
        accent = Colors.Accent.Turquoise,
    ),

    VAMPIRE_RED_DARK(
        id = "2",
        primary = Colors.Primary.VampireRedDark,
        accent = Colors.Accent.PlumWine,
    ),

    EUPHORIA_BLUE_DARK(
        id = "3",
        primary = Colors.Primary.EuphoriaBlueDark,
        accent = Colors.Accent.Blue,
    ),
    EUPHORIA_VIOLET(
        id = "4",
        primary = Colors.Primary.EuphoriaViolet,
        accent = Colors.Accent.Purple2,
    ),
    EUPHORIA_BLUE(
        id = "5",
        primary = Colors.Primary.EuphoriaBlue,
        accent = Colors.Accent.Blue2,
    ),
    EUPHORIA_PINK(
        id = "6",
        primary = Colors.Primary.EuphoriaPink,
        accent = Colors.Accent.RichMulberry,
        surfaceDim = Color.Black.copy(alpha = 0.1f),
    ),

    IRIS_RED(
        id = "7",
        primary = Colors.Primary.IrisRed,
        accent = Colors.Accent.Red,
        surfaceDim = Color.Black.copy(alpha = 0.1f),
    ),
    IRIS_RED_DARK(
        id = "8",
        primary = Colors.Primary.IrisRedDark,
        accent = Colors.Accent.Pink3,
    ),

    STORM_IN_THE_NIGHT_BLUE(
        id = "9",
        primary = Colors.Primary.StormInTheNightBlue,
        accent = Colors.Accent.PurpleDark,
    ),
    STORM_IN_THE_NIGHT_BLUE_LIGHT(
        id = "10",
        primary = Colors.Primary.StormInTheNightBlueLight,
        accent = Colors.Accent.Purple,
    ),

    UNICORN_2012_BLUE(
        id = "11",
        primary = Colors.Primary.Unicorn2012Blue,
        accent = Colors.Accent.Blue3,
        surfaceDim = Color.DarkGray.copy(alpha = 0.15f),
    ),
    UNICORN_2012_PINK_DARK(
        id = "12",
        primary = Colors.Primary.Unicorn2012PinkDark,
        accent = Colors.Accent.Pink,
        surfaceDim = Color.DarkGray.copy(alpha = 0.1f),
    ),

    DISTANT_CASTLE_GREEN(
        id = "13",
        primary = Colors.Primary.DistantCastleGreen,
        accent = Colors.Accent.GreenLight,
    ),
    DISTANT_CASTLE_BLUE(
        id = "14",
        primary = Colors.Primary.DistantCastleBlue,
        accent = Colors.Accent.Purple,
    );

    companion object {
        fun fromId(id: String?) = entries.find { it.id == id } ?: DISTANT_CASTLE_GREEN
    }
}
