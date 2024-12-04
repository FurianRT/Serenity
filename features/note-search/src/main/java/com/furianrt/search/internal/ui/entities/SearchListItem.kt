package com.furianrt.search.internal.ui.entities

import androidx.compose.runtime.Immutable
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteTag
import kotlinx.collections.immutable.ImmutableList
import java.time.LocalDate

internal sealed class SearchListItem(
    open val id: String,
) {
    @Immutable
    internal data class FiltersList(
        val items: ImmutableList<Filter.Tag>,
    ) : SearchListItem(ID) {

        companion object {
            const val ID = "tags_list"
        }

        internal sealed class Filter(
            open val id: String,
        ) {
            internal data class Tag(
                val title: String,
                val count: Int,
            ) : Filter(title)

            @Immutable
            internal data class DateRange(
                val start: LocalDate,
                val end: LocalDate?,
            ) : Filter(ID) {

                companion object {
                    const val ID = "date_range"
                }
            }
        }
    }

    @Immutable
    internal data class Note(
        override val id: String,
        val date: String,
        val tags: ImmutableList<UiNoteTag>,
        val content: ImmutableList<UiNoteContent>,
    ) : SearchListItem(id)
}
