package com.furianrt.notecreate.internal.ui.entites

import androidx.compose.runtime.Immutable
import com.furianrt.notelistui.entities.UiNoteTheme
import java.time.ZonedDateTime

@Immutable
internal data class NoteItem(
    val id: String,
    val theme: UiNoteTheme?,
    val date: ZonedDateTime,
    val isPinned: Boolean,
)