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
        id = "10",
        primary = Colors.Primary.VampireRedDark,
        accent = Colors.Accent.Purple,
    ),

    EUPHORIA_BLUE_DARK(
        id = "13",
        primary = Colors.Primary.EuphoriaBlueDark,
        accent = Colors.Accent.Blue,
    ),
    EUPHORIA_VIOLET(
        id = "14",
        primary = Colors.Primary.EuphoriaViolet,
        accent = Colors.Accent.Purple,
    ),
    EUPHORIA_BLUE(
        id = "15",
        primary = Colors.Primary.EuphoriaBlue,
        accent = Colors.Accent.Blue2,
    ),
    EUPHORIA_PINK(
        id = "16",
        primary = Colors.Primary.EuphoriaPink,
        accent = Colors.Accent.Purple,
        surfaceDim = Color.Black.copy(alpha = 0.1f),
    ),

    IRIS_RED(
        id = "18",
        primary = Colors.Primary.IrisRed,
        accent = Colors.Accent.Purple,
        surfaceDim = Color.Black.copy(alpha = 0.1f),
    ),
    IRIS_RED_DARK(
        id = "19",
        primary = Colors.Primary.IrisRedDark,
        accent = Colors.Accent.Purple,
    ),

    STORM_IN_THE_NIGHT_BLUE(
        id = "21",
        primary = Colors.Primary.StormInTheNightBlue,
        accent = Colors.Accent.PurpleDark,
    ),
    STORM_IN_THE_NIGHT_BLUE_LIGHT(
        id = "24",
        primary = Colors.Primary.StormInTheNightBlueLight,
        accent = Colors.Accent.Purple,
    ),

    UNICORN_2012_BLUE(
        id = "27",
        primary = Colors.Primary.Unicorn2012Blue,
        accent = Colors.Accent.Purple,
        surfaceDim = Color.DarkGray.copy(alpha = 0.15f),
    ),
    UNICORN_2012_PINK_DARK(
        id = "29",
        primary = Colors.Primary.Unicorn2012PinkDark,
        accent = Colors.Accent.Purple,
        surfaceDim = Color.DarkGray.copy(alpha = 0.1f),
    ),

    DISTANT_CASTLE_GREEN(
        id = "37",
        primary = Colors.Primary.DistantCastleGreen,
        accent = Colors.Accent.GreenLight,
    ),
    DISTANT_CASTLE_BLUE(
        id = "38",
        primary = Colors.Primary.DistantCastleBlue,
        accent = Colors.Accent.Purple,
    );

    companion object {
        fun fromId(id: String?) = entries.find { it.id == id } ?: DISTANT_CASTLE_GREEN
    }
}
