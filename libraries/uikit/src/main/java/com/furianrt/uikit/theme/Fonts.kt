package com.furianrt.uikit.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.furianrt.uikit.R

sealed class NoteFont(
    val light: FontFamily,
    val regular: FontFamily,
    val bold: FontFamily,
) {
    data object QuickSand : NoteFont(
        light = FontFamily(Font(R.font.noto_sans_light)),
        regular = FontFamily(Font(R.font.noto_sans_regular)),
        bold = FontFamily(Font(R.font.noto_sans_bold)),
    )

    data object ShantellSans : NoteFont(
        light = FontFamily(Font(R.font.shantell_sans_light)),
        regular = FontFamily(Font(R.font.shantell_sans_regular)),
        bold = FontFamily(Font(R.font.shantell_sans_bold)),
    )

    data object PixelifySans : NoteFont(
        light = FontFamily(Font(R.font.pixelify_sans_regular)),
        regular = FontFamily(Font(R.font.pixelify_sans_regular)),
        bold = FontFamily(Font(R.font.pixelify_sans_bold)),
    )

    data object AdventPro : NoteFont(
        light = FontFamily(Font(R.font.advent_pro_light)),
        regular = FontFamily(Font(R.font.advent_pro_regular)),
        bold = FontFamily(Font(R.font.advent_pro_bold)),
    )

    data object CormorantUnicase : NoteFont(
        light = FontFamily(Font(R.font.cormorant_unicase_light)),
        regular = FontFamily(Font(R.font.cormorant_unicase_regular)),
        bold = FontFamily(Font(R.font.cormorant_unicase_bold)),
    )

    data object MontserratAlternates : NoteFont(
        light = FontFamily(Font(R.font.montserrat_alternates_light)),
        regular = FontFamily(Font(R.font.montserrat_alternates_regular)),
        bold = FontFamily(Font(R.font.montserrat_alternates_bold)),
    )

    data object Tektur : NoteFont(
        light = FontFamily(Font(R.font.tektur_regular)),
        regular = FontFamily(Font(R.font.tektur_regular)),
        bold = FontFamily(Font(R.font.tektur_bold)),
    )

    data object Doto : NoteFont(
        light = FontFamily(Font(R.font.doto_light)),
        regular = FontFamily(Font(R.font.doto_regular)),
        bold = FontFamily(Font(R.font.doto_bold)),
    )

    data object PlayWriteModern : NoteFont(
        light = FontFamily(Font(R.font.playwrite_modern_light)),
        regular = FontFamily(Font(R.font.pixelify_sans_regular)),
        bold = FontFamily(Font(R.font.pixelify_sans_bold)),
    )

    data object Tillana : NoteFont(
        light = FontFamily(Font(R.font.tillana_light)),
        regular = FontFamily(Font(R.font.tillana_regular)),
        bold = FontFamily(Font(R.font.tillana_bold)),
    )
}
