package com.furianrt.uikit.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.furianrt.uikit.R

data object NoteFont {
    data object QuickSand {
        val light: FontFamily = FontFamily(Font(R.font.quicksand_light))
        val regular: FontFamily = FontFamily(Font(R.font.quicksand_regular))
        val bold: FontFamily = FontFamily(Font(R.font.quicksand_medium))
    }

    data object ToThePoint {
        val regular: FontFamily = FontFamily(Font(R.font.to_the_point_regular))
    }
}
