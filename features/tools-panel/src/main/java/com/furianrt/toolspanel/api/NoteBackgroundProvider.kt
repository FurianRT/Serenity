package com.furianrt.toolspanel.api

import com.furianrt.notelistui.entities.UiNoteBackground

interface NoteBackgroundProvider {
    fun getBackground(id: String?): UiNoteBackground?
    fun getDarkBackgrounds(): List<UiNoteBackground>
    fun getLightBackgrounds(): List<UiNoteBackground>
}