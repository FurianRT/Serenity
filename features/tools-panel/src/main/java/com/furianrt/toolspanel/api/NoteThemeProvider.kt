package com.furianrt.toolspanel.api

import com.furianrt.notelistui.entities.UiNoteBackgroundImage
import com.furianrt.notelistui.entities.UiNoteTheme

interface NoteThemeProvider {
    suspend fun findTheme(
        colorId: String?,
        imageId: String?,
    ): UiNoteTheme?

    fun getSolidThemes(): List<UiNoteTheme.Solid>
    fun getPictureThemes(): List<UiNoteTheme.Image.Picture>
    fun getPatternImages(): List<UiNoteBackgroundImage>
}