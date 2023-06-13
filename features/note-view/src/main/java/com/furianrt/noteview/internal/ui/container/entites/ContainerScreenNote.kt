package com.furianrt.noteview.internal.ui.container.entites

import androidx.compose.runtime.Immutable
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import kotlinx.collections.immutable.ImmutableSet

@Immutable
internal class ContainerScreenNote(
    val id: String,
    val timestamp: Long,
    val tags: ImmutableSet<UiNoteTag>,
    val content: ImmutableSet<UiNoteContent>,
)
