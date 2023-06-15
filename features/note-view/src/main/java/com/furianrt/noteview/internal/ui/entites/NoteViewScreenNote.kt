package com.furianrt.noteview.internal.ui.entites

import androidx.compose.runtime.Immutable
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import kotlinx.collections.immutable.ImmutableList

@Immutable
internal class NoteViewScreenNote(
    val id: String,
    val timestamp: Long,
    val tags: ImmutableList<UiNoteTag>,
    val content: ImmutableList<UiNoteContent>,
)
