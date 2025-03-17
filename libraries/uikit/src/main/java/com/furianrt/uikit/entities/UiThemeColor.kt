package com.furianrt.uikit.entities

import androidx.compose.ui.graphics.Color
import com.furianrt.uikit.theme.Colors

enum class UiThemeColor(
    val id: String,
    val primary: Color,
    val accent: Color,
    val surfaceTint: Color = primary.copy(alpha = 0.3f),
) {
    SCANDI_GRANDPA_GRAY_DARK(
        id = "1",
        primary = Colors.Primary.ScandiGrandpaGrayDark,
        accent = Colors.Accent.Purple,
    ),
    SCANDI_GRANDPA_BROWN(
        id = "2",
        primary = Colors.Primary.ScandiGrandpaBrown,
        accent = Colors.Accent.Purple,
    ),
    SCANDI_GRANDPA_YELLOW(
        id = "3",
        primary = Colors.Primary.ScandiGrandpaYellow,
        accent = Colors.Accent.Purple,
    ),
    SCANDI_GRANDPA_GRAY(
        id = "4",
        primary = Colors.Primary.ScandiGrandpaGray,
        accent = Colors.Accent.Purple,
    ),

    CLEOPATRA_BLUE(
        id = "5",
        primary = Colors.Primary.CleopatraBLue,
        accent = Colors.Accent.Purple,
    ),
    CLEOPATRA_YELLOW(
        id = "6",
        primary = Colors.Primary.CleopatraYellow,
        accent = Colors.Accent.Purple,
    ),
    CLEOPATRA_BROWN(
        id = "7",
        primary = Colors.Primary.CleopatraBrown,
        accent = Colors.Accent.Purple,
    ),
    CLEOPATRA_ORANGE(
        id = "8",
        primary = Colors.Primary.CleopatraOrange,
        accent = Colors.Accent.Purple,
    ),

    VAMPIRE_RED_LIGHT(
        id = "9",
        primary = Colors.Primary.VampireRedLight,
        accent = Colors.Accent.Purple,
    ),
    VAMPIRE_RED_DARK(
        id = "10",
        primary = Colors.Primary.VampireRedDark,
        accent = Colors.Accent.Purple,
    ),
    VAMPIRE_RED(
        id = "11",
        primary = Colors.Primary.VampireRed,
        accent = Colors.Accent.Purple,
    ),
    VAMPIRE_BLACK(
        id = "12",
        primary = Colors.Primary.VampireBlack,
        accent = Colors.Accent.Purple,
        surfaceTint = Color.White.copy(alpha = 0.1f),
    ),

    EUPHORIA_BLUE_DARK(
        id = "13",
        primary = Colors.Primary.EuphoriaBlueDark,
        accent = Colors.Accent.Purple,
    ),
    EUPHORIA_VIOLET(
        id = "14",
        primary = Colors.Primary.EuphoriaViolet,
        accent = Colors.Accent.Purple,
    ),
    EUPHORIA_BLUE(
        id = "15",
        primary = Colors.Primary.EuphoriaBlue,
        accent = Colors.Accent.Purple,
    ),
    EUPHORIA_PINK(
        id = "16",
        primary = Colors.Primary.EuphoriaPink,
        accent = Colors.Accent.Purple,
    ),

    IRIS_PINK(
        id = "17",
        primary = Colors.Primary.IrisPink,
        accent = Colors.Accent.Purple,
    ),
    IRIS_RED(
        id = "18",
        primary = Colors.Primary.IrisRed,
        accent = Colors.Accent.Purple,
    ),
    IRIS_RED_DARK(
        id = "19",
        primary = Colors.Primary.IrisRedDark,
        accent = Colors.Accent.Purple,
    ),
    IRIS_GREEN(
        id = "20",
        primary = Colors.Primary.IrisGreen,
        accent = Colors.Accent.Purple,
    ),

    STORM_IN_THE_NIGHT_BLUE(
        id = "21",
        primary = Colors.Primary.StormInTheNightBlue,
        accent = Colors.Accent.PurpleDark,
    ),
    STORM_IN_THE_NIGHT_BLUE_DARK(
        id = "22",
        primary = Colors.Primary.StormInTheNightBlueDark,
        accent = Colors.Accent.Purple,
    ),
    STORM_IN_THE_NIGHT_BLACK(
        id = "23",
        primary = Colors.Primary.StormInTheNightBlack,
        accent = Colors.Accent.Purple,
        surfaceTint = Color.White.copy(alpha = 0.1f),
    ),
    STORM_IN_THE_NIGHT_BLUE_LIGHT(
        id = "24",
        primary = Colors.Primary.StormInTheNightBlueLight,
        accent = Colors.Accent.Purple,
    ),
    STORM_IN_THE_NIGHT_GRAY(
        id = "25",
        primary = Colors.Primary.StormInTheNightGray,
        accent = Colors.Accent.Purple,
    ),

    UNICORN_2012_PINK(
        id = "26",
        primary = Colors.Primary.Unicorn2012Pink,
        accent = Colors.Accent.Purple,
    ),
    UNICORN_2012_BLUE(
        id = "27",
        primary = Colors.Primary.Unicorn2012Blue,
        accent = Colors.Accent.Purple,
    ),
    UNICORN_2012_YELLOW(
        id = "28",
        primary = Colors.Primary.Unicorn2012Yellow,
        accent = Colors.Accent.Purple,
    ),
    UNICORN_2012_PINK_DARK(
        id = "29",
        primary = Colors.Primary.Unicorn2012PinkDark,
        accent = Colors.Accent.Purple,
    ),
    UNICORN_2012_RED(
        id = "30",
        primary = Colors.Primary.Unicorn2012Red,
        accent = Colors.Accent.Purple,
    ),

    SPACE_COWBOY_BROWN_LIGHT(
        id = "31",
        primary = Colors.Primary.SpaceCowboyBrownLight,
        accent = Colors.Accent.Purple,
    ),
    SPACE_COWBOY_BROWN(
        id = "32",
        primary = Colors.Primary.SpaceCowboyBrown,
        accent = Colors.Accent.Purple,
    ),
    SPACE_COWBOY_BROWN_DARK(
        id = "33",
        primary = Colors.Primary.SpaceCowboyBrownDark,
        accent = Colors.Accent.Purple,
        surfaceTint = Color.White.copy(alpha = 0.1f),
    ),
    SPACE_COWBOY_BLUE(
        id = "34",
        primary = Colors.Primary.SpaceCowboyBlue,
        accent = Colors.Accent.Purple,
        surfaceTint = Color.White.copy(alpha = 0.1f),
    ),

    DISTANT_CASTLE_BROWN(
        id = "35",
        primary = Colors.Primary.DistantCastleBrown,
        accent = Colors.Accent.Purple,
    ),
    DISTANT_CASTLE_PINK(
        id = "36",
        primary = Colors.Primary.DistantCastlePink,
        accent = Colors.Accent.Purple,
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
    ),

    OLIVE_TREE_BROWN(
        id = "39",
        primary = Colors.Primary.OliveTreeBrown,
        accent = Colors.Accent.Purple,
    ),
    OLIVE_TREE_GREEN(
        id = "40",
        primary = Colors.Primary.OliveTreeGreen,
        accent = Colors.Accent.Purple,
    ),
    OLIVE_TREE_BROWN_DARK(
        id = "41",
        primary = Colors.Primary.OliveTreeBrownDark,
        accent = Colors.Accent.Purple,
    ),
    OLIVE_TREE_GREEN_DARK(
        id = "42",
        primary = Colors.Primary.OliveTreeGreenDark,
        accent = Colors.Accent.Purple,
    ),

    BEIGE_CENSORSHIP_BROWN_LIGHT(
        id = "43",
        primary = Colors.Primary.BeigeCensorshipBrownLight,
        accent = Colors.Accent.Purple,
    ),
    BEIGE_CENSORSHIP_BROWN_DARK(
        id = "44",
        primary = Colors.Primary.BeigeCensorshipBrownDark,
        accent = Colors.Accent.Purple,
    ),
    BEIGE_CENSORSHIP_BLACK(
        id = "45",
        primary = Colors.Primary.BeigeCensorshipBlack,
        accent = Colors.Accent.Purple,
    ),
    BEIGE_CENSORSHIP_BROWN(
        id = "46",
        primary = Colors.Primary.BeigeCensorshipBrown,
        accent = Colors.Accent.Purple,
    );

    companion object {
        fun fromId(id: String?) = entries.find { it.id == id } ?: DISTANT_CASTLE_GREEN
    }
}
