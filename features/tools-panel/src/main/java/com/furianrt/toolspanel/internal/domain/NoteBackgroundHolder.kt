package com.furianrt.toolspanel.internal.domain

import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.toolspanel.api.NoteBackgroundProvider
import javax.inject.Inject

internal class NoteBackgroundHolder @Inject constructor() : NoteBackgroundProvider {

    override fun getBackground(id: String?): UiNoteBackground? {
        return null
    }
}