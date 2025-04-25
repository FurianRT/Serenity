package com.furianrt.noteview.internal.ui.entites

import androidx.compose.runtime.Immutable
import java.time.ZonedDateTime

@Immutable
internal data class NoteItem(
    val id: String,
    val date: ZonedDateTime,
    val isPinned: Boolean,
)