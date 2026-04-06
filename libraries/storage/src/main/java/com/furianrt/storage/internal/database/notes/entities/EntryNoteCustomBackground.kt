package com.furianrt.storage.internal.database.notes.entities

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(tableName = EntryNoteCustomBackground.TABLE_NAME)
internal class EntryNoteCustomBackground(
    @ColumnInfo(name = FIELD_ID)
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = FIELD_NAME)
    val name: String,

    @ColumnInfo(name = FIELD_URI)
    val uri: Uri,

    @ColumnInfo(name = FIELD_PRIMARY_COLOR)
    val primaryColor: Int,

    @ColumnInfo(name = FIELD_ACCENT_COLOR)
    val accentColor: Int,

    @ColumnInfo(name = FIELD_IS_LIGHT)
    val isLight: Boolean,

    @ColumnInfo(name = FIELD_ADDED_DATE)
    val addedDate: ZonedDateTime,

    @ColumnInfo(name = FIELD_IS_SAVED)
    val isSaved: Boolean,

    @ColumnInfo(name = FIELD_IS_HIDDEN)
    val isHidden: Boolean,
) {
    companion object {
        const val TABLE_NAME = "NoteCustomBackgrounds"
        const val FIELD_ID = "id"
        const val FIELD_NAME = "name"
        const val FIELD_URI = "uri"
        const val FIELD_PRIMARY_COLOR = "primary_color"
        const val FIELD_ACCENT_COLOR = "accent_color"
        const val FIELD_IS_LIGHT = "is_light"
        const val FIELD_ADDED_DATE = "added_date"
        const val FIELD_IS_SAVED = "is_saved"
        const val FIELD_IS_HIDDEN = "is_hidden"
    }
}

@Entity
internal class PartNoteCustomBackgroundUri(
    @ColumnInfo(name = EntryNoteCustomBackground.FIELD_ID)
    val id: String,

    @ColumnInfo(name = EntryNoteCustomBackground.FIELD_NAME)
    val name: String,

    @ColumnInfo(name = EntryNoteCustomBackground.FIELD_URI)
    val uri: Uri,

    @ColumnInfo(name = EntryNoteCustomBackground.FIELD_IS_SAVED)
    val isSaved: Boolean,
)

@Entity
internal class PartNoteCustomBackgroundIsHidden(
    @ColumnInfo(name = EntryNoteCustomBackground.FIELD_ID)
    val id: String,

    @ColumnInfo(name = EntryNoteCustomBackground.FIELD_IS_HIDDEN)
    val isHidden: Boolean,
)

@Entity
internal class PartNoteCustomBackgroundId(
    @ColumnInfo(name = EntryNoteCustomBackground.FIELD_ID)
    val id: String,
)
