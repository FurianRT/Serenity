package com.furianrt.notecreate.internal.ui.entites

import androidx.compose.runtime.Immutable
import com.furianrt.notelistui.entities.UiNoteBackground
import java.time.ZonedDateTime

@Immutable
internal data class NoteItem(
    val id: String,
    val background: UiNoteBackground?,
    val date: ZonedDateTime,
    val isPinned: Boolean,
)