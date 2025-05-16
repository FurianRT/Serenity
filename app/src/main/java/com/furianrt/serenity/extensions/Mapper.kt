package com.furianrt.serenity.extensions

import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.uikit.theme.NoteFont

internal fun NoteFontFamily.toNoteFont() = when (this) {
    NoteFontFamily.QUICK_SAND -> NoteFont.QuickSand
    NoteFontFamily.SHANTELL_SANS -> NoteFont.ShantellSans
    NoteFontFamily.PIXELIFY_SANS -> NoteFont.PixelifySans
    NoteFontFamily.ADVENT_PRO -> NoteFont.AdventPro
    NoteFontFamily.CORMORANT_UNICASE -> NoteFont.CormorantUnicase
    NoteFontFamily.MONSERRAT_ALTERNATES -> NoteFont.MontserratAlternates
    NoteFontFamily.TEKTUR -> NoteFont.Tektur
    NoteFontFamily.DOTO -> NoteFont.Doto
    NoteFontFamily.PLAY_WRITE_MODERN -> NoteFont.PlayWriteModern
    NoteFontFamily.TILLANA -> NoteFont.Tillana
}