package com.furianrt.notepage.internal.ui.entities

import androidx.compose.runtime.Immutable
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import kotlinx.collections.immutable.ImmutableList

@Immutable
internal class NoteItem(
    val id: String,
    val tags: ImmutableList<UiNoteTag>,
    val content: ImmutableList<UiNoteContent>,
)