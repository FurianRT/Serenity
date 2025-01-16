package com.furianrt.notelist.internal.ui.entities

import androidx.compose.runtime.Immutable
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import kotlinx.collections.immutable.ImmutableList

@Immutable
internal data class NoteListScreenNote(
    val id: String,
    val date: String,
    val tags: ImmutableList<UiNoteTag>,
    val fontColor: UiNoteFontColor,
    val fontFamily: UiNoteFontFamily,
    val fontSize: Int,
    val content: ImmutableList<UiNoteContent>,
)
