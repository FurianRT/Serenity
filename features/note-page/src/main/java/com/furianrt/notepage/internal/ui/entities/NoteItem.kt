package com.furianrt.notepage.internal.ui.entities

import androidx.compose.runtime.Immutable
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID

@Immutable
internal class NoteItem(
    val id: String = UUID.randomUUID().toString(),
    val tags: ImmutableList<UiNoteTag> = persistentListOf(),
    val content: ImmutableList<UiNoteContent> = persistentListOf(),
)