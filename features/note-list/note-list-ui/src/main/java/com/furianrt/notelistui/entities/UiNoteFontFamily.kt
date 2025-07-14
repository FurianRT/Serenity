package com.furianrt.notelistui.entities

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontFamily
import com.furianrt.uikit.theme.NoteFont

@Immutable
sealed class UiNoteFontFamily(
    val name: String,
    val light: FontFamily,
    val regular: FontFamily,
    val bold: FontFamily,
) {
    @Immutable
    data object QuickSand : UiNoteFontFamily(
        name = "Quick Sand",
        light = NoteFont.QuickSand.light,
        regular = NoteFont.QuickSand.regular,
        bold = NoteFont.QuickSand.bold,
    )

    @Immutable
    data object ShantellSans : UiNoteFontFamily(
        name = "Shantell Sans",
        light = NoteFont.ShantellSans.light,
        regular = NoteFont.ShantellSans.regular,
        bold = NoteFont.ShantellSans.bold,
    )

    @Immutable
    data object PixelifySans : UiNoteFontFamily(
        name = "Pixelify Sans",
        light = NoteFont.PixelifySans.light,
        regular = NoteFont.PixelifySans.regular,
        bold = NoteFont.PixelifySans.bold,
    )

    @Immutable
    data object AdventPro : UiNoteFontFamily(
        name = "Advent Pro",
        light = NoteFont.AdventPro.light,
        regular = NoteFont.AdventPro.regular,
        bold = NoteFont.AdventPro.bold,
    )

    @Immutable
    data object CormorantUnicase : UiNoteFontFamily(
        name = "Cormorant Unicase",
        light = NoteFont.CormorantUnicase.light,
        regular = NoteFont.CormorantUnicase.regular,
        bold = NoteFont.CormorantUnicase.bold,
    )

    @Immutable
    data object MontserratAlternates : UiNoteFontFamily(
        name = "Montserrat Alternates",
        light = NoteFont.MontserratAlternates.light,
        regular = NoteFont.MontserratAlternates.regular,
        bold = NoteFont.MontserratAlternates.bold,
    )

    @Immutable
    data object Tektur : UiNoteFontFamily(
        name = "Tektur Font",
        light = NoteFont.Tektur.light,
        regular = NoteFont.Tektur.regular,
        bold = NoteFont.Tektur.bold,
    )

    @Immutable
    data object Doto : UiNoteFontFamily(
        name = "Doto Font",
        light = NoteFont.Doto.light,
        regular = NoteFont.Doto.regular,
        bold = NoteFont.Doto.bold,
    )

    @Immutable
    data object PlayWriteModern : UiNoteFontFamily(
        name = "Play Write Modern",
        light = NoteFont.PlayWriteModern.light,
        regular = NoteFont.PlayWriteModern.regular,
        bold = NoteFont.PlayWriteModern.bold,
    )

    @Immutable
    data object Tillana : UiNoteFontFamily(
        name = "Tillana",
        light = NoteFont.Tillana.light,
        regular = NoteFont.Tillana.regular,
        bold = NoteFont.Tillana.bold,
    )

    @Immutable
    data object LifeSavers : UiNoteFontFamily(
        name = "Life Savers",
        light = NoteFont.LifeSavers.light,
        regular = NoteFont.LifeSavers.regular,
        bold = NoteFont.LifeSavers.bold,
    )

    @Immutable
    data object Texturina : UiNoteFontFamily(
        name = "Texturina",
        light = NoteFont.Texturina.light,
        regular = NoteFont.Texturina.regular,
        bold = NoteFont.Texturina.bold,
    )
}
