package com.furianrt.search.internal.ui.entities

import androidx.compose.runtime.Immutable
import com.furianrt.notelistui.entities.LocationState
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import kotlinx.collections.immutable.ImmutableList

internal sealed class SearchListItem(
    open val id: String,
) {
    data class NotesCountTitle(
        val count: Int,
    ) : SearchListItem(ID) {

        companion object {
            private const val ID = "notes_count"
        }
    }

    @Immutable
    data class TagsList(
        val tags: ImmutableList<Tag>,
    ) : SearchListItem(ID) {

        companion object {
            private const val ID = "tags_list"
        }

        data class Tag(
            val title: String,
            val count: Int,
        )
    }

    @Immutable
    data class Note(
        override val id: String,
        val date: Date,
        val tags: ImmutableList<UiNoteTag>,
        val isSelected: Boolean,
        val fontColor: UiNoteFontColor?,
        val fontFamily: UiNoteFontFamily?,
        val fontSize: Int,
        val moodId: String?,
        val locationState: LocationState,
        val content: ImmutableList<UiNoteContent>,
    ) : SearchListItem(id) {
        sealed interface Date {
            data object Today : Date
            data object Yesterday : Date
            data class Other(val text: String) : Date
        }
    }
}
