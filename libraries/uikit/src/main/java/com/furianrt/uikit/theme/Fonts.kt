package com.furianrt.uikit.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.furianrt.uikit.R

@Immutable
sealed class NoteFont(
    val regular: FontFamily,
    val light: FontFamily? = null,
    val bold: FontFamily? = null,
    val sizeMultiplier: Float = 1f,
) {
    @Immutable
    data object NotoSans : NoteFont(
        light = FontFamily(Font(R.font.noto_sans_light)),
        regular = FontFamily(Font(R.font.noto_sans_regular)),
        bold = FontFamily(Font(R.font.noto_sans_bold)),
    )

    @Immutable
    data object NotoSerif : NoteFont(
        light = FontFamily(Font(R.font.noto_serif_light)),
        regular = FontFamily(Font(R.font.noto_serif_regular)),
        bold = FontFamily(Font(R.font.noto_serif_bold)),
    )

    @Immutable
    data object Roboto : NoteFont(
        light = FontFamily(Font(R.font.roboto_light)),
        regular = FontFamily(Font(R.font.roboto_regular)),
        bold = FontFamily(Font(R.font.roboto_bold)),
    )

    @Immutable
    data object ShantellSans : NoteFont(
        light = FontFamily(Font(R.font.shantell_sans_light)),
        regular = FontFamily(Font(R.font.shantell_sans_regular)),
        bold = FontFamily(Font(R.font.shantell_sans_bold)),
    )

    @Immutable
    data object PixelifySans : NoteFont(
        light = FontFamily(Font(R.font.pixelify_sans_regular)),
        regular = FontFamily(Font(R.font.pixelify_sans_regular)),
        bold = FontFamily(Font(R.font.pixelify_sans_bold)),
    )

    @Immutable
    data object AdventPro : NoteFont(
        light = FontFamily(Font(R.font.advent_pro_light)),
        regular = FontFamily(Font(R.font.advent_pro_regular)),
        bold = FontFamily(Font(R.font.advent_pro_bold)),
    )

    @Immutable
    data object CormorantUnicase : NoteFont(
        light = FontFamily(Font(R.font.cormorant_unicase_light)),
        regular = FontFamily(Font(R.font.cormorant_unicase_regular)),
        bold = FontFamily(Font(R.font.cormorant_unicase_bold)),
    )

    @Immutable
    data object MontserratAlternates : NoteFont(
        light = FontFamily(Font(R.font.montserrat_alternates_light)),
        regular = FontFamily(Font(R.font.montserrat_alternates_regular)),
        bold = FontFamily(Font(R.font.montserrat_alternates_bold)),
    )

    @Immutable
    data object Tektur : NoteFont(
        light = FontFamily(Font(R.font.tektur_regular)),
        regular = FontFamily(Font(R.font.tektur_regular)),
        bold = FontFamily(Font(R.font.tektur_bold)),
    )

    @Immutable
    data object Doto : NoteFont(
        light = FontFamily(Font(R.font.doto_light)),
        regular = FontFamily(Font(R.font.doto_regular)),
        bold = FontFamily(Font(R.font.doto_bold)),
    )

    @Immutable
    data object PlayWriteModern : NoteFont(
        light = FontFamily(Font(R.font.playwrite_modern_light)),
        regular = FontFamily(Font(R.font.playwrite_modern_regular)),
        bold = FontFamily(Font(R.font.playwrite_modern_bold)),
    )

    @Immutable
    data object Tillana : NoteFont(
        light = FontFamily(Font(R.font.tillana_light)),
        regular = FontFamily(Font(R.font.tillana_regular)),
        bold = FontFamily(Font(R.font.tillana_bold)),
    )

    @Immutable
    data object LifeSavers : NoteFont(
        light = FontFamily(Font(R.font.life_savers_regular)),
        regular = FontFamily(Font(R.font.life_savers_regular)),
        bold = FontFamily(Font(R.font.life_savers_bold)),
    )

    @Immutable
    data object Texturina : NoteFont(
        light = FontFamily(Font(R.font.texturina_light)),
        regular = FontFamily(Font(R.font.texturina_regular)),
        bold = FontFamily(Font(R.font.texturina_bold)),
    )

    @Immutable
    data object Parisienne : NoteFont(
        light = FontFamily(Font(R.font.parisienne_regular)),
        regular = FontFamily(Font(R.font.parisienne_regular)),
        bold = FontFamily(Font(R.font.parisienne_regular)),
        sizeMultiplier = 1.25f,
    )

    @Immutable
    data object SpaceMono : NoteFont(
        light = FontFamily(Font(R.font.space_mono_regular)),
        regular = FontFamily(Font(R.font.space_mono_regular)),
        bold = FontFamily(Font(R.font.space_mono_bold)),
    )

    @Immutable
    data object CormorantGaramond : NoteFont(
        light = FontFamily(Font(R.font.cormorant_garamond_light)),
        regular = FontFamily(Font(R.font.cormorant_garamond_regular)),
        bold = FontFamily(Font(R.font.cormorant_garamond_bold)),
        sizeMultiplier = 1.25f,
    )

    @Immutable
    data object RobotoCondensed : NoteFont(
        light = FontFamily(Font(R.font.roboto_condensed_light)),
        regular = FontFamily(Font(R.font.roboto_condensed_regular)),
        bold = FontFamily(Font(R.font.roboto_condensed_bold)),
    )

    @Immutable
    data object DeathNote : NoteFont(
        regular = FontFamily(Font(R.font.death_note_regular)),
    )
}
