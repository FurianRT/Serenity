package com.furianrt.search.internal.ui.entities

import androidx.compose.runtime.Immutable
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import kotlinx.collections.immutable.ImmutableList
import java.time.ZonedDateTime

internal sealed class SearchListItem(
    open val id: String,
)

@Immutable
internal data class TagsList(
    val items: ImmutableList<SelectableItem>,
    val rowsLimit: Int = DEFAULT_ROWS_LIMIT,
) : SearchListItem(ID) {

    internal sealed class SelectableItem(
        open val id: String,
    )

    internal data class Tag(
        val title: String,
        val count: Int,
    ) : SelectableItem(title)

    @Immutable
    internal data class DateRange(
        val start: ZonedDateTime,
        val end: ZonedDateTime?,
    ) : SelectableItem(ID) {
        companion object {
            const val ID = "date_range"
        }
    }

    companion object {
        const val ID = "tags_list"
        const val DEFAULT_ROWS_LIMIT = 3
    }
}

@Immutable
internal data class Note(
    override val id: String,
    val date: String,
    val tags: ImmutableList<UiNoteTag>,
    val content: ImmutableList<UiNoteContent>,
) : SearchListItem(id)
