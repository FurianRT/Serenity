package com.furianrt.notelist.internal.ui.entities

import androidx.compose.runtime.Immutable
import com.furianrt.notelistui.entities.LocationState
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import kotlinx.collections.immutable.ImmutableList

@Immutable
internal data class NoteListScreenNote(
    val id: String,
    val date: Date,
    val tags: ImmutableList<UiNoteTag>,
    val fontFamily: UiNoteFontFamily?,
    val fontSize: Int,
    val moodId: String?,
    val locationState: LocationState,
    val isPinned: Boolean,
    val isSelected: Boolean,
    val content: ImmutableList<UiNoteContent>,
) {
    sealed interface Date {
        data object Today : Date
        data object Yesterday : Date
        data class Other(val text: String) : Date
    }
}
