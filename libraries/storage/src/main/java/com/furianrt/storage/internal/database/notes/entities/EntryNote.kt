package com.furianrt.storage.internal.database.notes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.entities.NoteFontColor
import com.furianrt.domain.entities.NoteTextSpan
import java.time.ZonedDateTime

@Entity(tableName = EntryNote.TABLE_NAME)
internal class EntryNote(
    @ColumnInfo(name = FIELD_ID)
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = FIELD_TEXT)
    val text: String,

    @ColumnInfo(name = FIELD_TEXT_SPANS)
    val textSpans: List<NoteTextSpan>,

    @ColumnInfo(name = FIELD_FONT)
    val font: NoteFontFamily,

    @ColumnInfo(name = FIELD_FONT_COLOR)
    val fontColor: NoteFontColor,

    @ColumnInfo(name = FIELD_FONT_SIZE)
    val fontSize: Int,

    @ColumnInfo(name = FIELD_DATE)
    val date: ZonedDateTime,

    @ColumnInfo(name = FIELD_IS_PINNED)
    val isPinned: Boolean,
) {
    companion object {
        const val TABLE_NAME = "Notes"
        const val FIELD_ID = "id"
        const val FIELD_TEXT = "text"
        const val FIELD_TEXT_SPANS = "text_spans"
        const val FIELD_FONT = "font"
        const val FIELD_FONT_COLOR = "font_color"
        const val FIELD_FONT_SIZE = "font_size"
        const val FIELD_DATE = "date"
        const val FIELD_IS_PINNED = "is_pinned"
    }
}

@Entity
internal class PartNoteId(
    @ColumnInfo(name = EntryNote.FIELD_ID)
    val id: String,
)

@Entity
internal class PartNoteText(
    @ColumnInfo(name = EntryNote.FIELD_ID)
    val id: String,

    @ColumnInfo(name = EntryNote.FIELD_TEXT)
    val text: String,

    @ColumnInfo(name = EntryNote.FIELD_TEXT_SPANS)
    val textSpans: List<NoteTextSpan>,
)

@Entity
internal class PartNoteDate(
    @ColumnInfo(name = EntryNote.FIELD_ID)
    val id: String,

    @ColumnInfo(name = EntryNote.FIELD_DATE)
    val date: ZonedDateTime,
)

@Entity
internal class PartNoteFont(
    @ColumnInfo(name = EntryNote.FIELD_ID)
    val id: String,

    @ColumnInfo(name = EntryNote.FIELD_FONT)
    val font: NoteFontFamily,

    @ColumnInfo(name = EntryNote.FIELD_FONT_COLOR)
    val fontColor: NoteFontColor,

    @ColumnInfo(name = EntryNote.FIELD_FONT_SIZE)
    val fontSize: Int,
)

@Entity
internal class PartNoteIsPinned(
    @ColumnInfo(name = EntryNote.FIELD_ID)
    val id: String,

    @ColumnInfo(name = EntryNote.FIELD_IS_PINNED)
    val isPinned: Boolean,
)
