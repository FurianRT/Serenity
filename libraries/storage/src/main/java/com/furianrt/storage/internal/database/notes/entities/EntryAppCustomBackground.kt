package com.furianrt.storage.internal.database.notes.entities

import android.net.Uri
import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import java.time.ZonedDateTime

@Entity(tableName = EntryAppCustomBackground.TABLE_NAME)
internal class EntryAppCustomBackground(
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
) {
    companion object {
        const val TABLE_NAME = "AppCustomBackgrounds"
        const val FIELD_ID = "id"
        const val FIELD_NAME = "name"
        const val FIELD_URI = "uri"
        const val FIELD_PRIMARY_COLOR = "primary_color"
        const val FIELD_ACCENT_COLOR = "accent_color"
        const val FIELD_IS_LIGHT = "is_light"
        const val FIELD_ADDED_DATE = "added_date"
        const val FIELD_IS_SAVED = "is_saved"
    }
}

@Entity
internal class PartAppCustomBackgroundUri(
    @ColumnInfo(name = EntryAppCustomBackground.FIELD_ID)
    val id: String,

    @ColumnInfo(name = EntryAppCustomBackground.FIELD_NAME)
    val name: String,

    @ColumnInfo(name = EntryAppCustomBackground.FIELD_URI)
    val uri: Uri,

    @ColumnInfo(name = EntryAppCustomBackground.FIELD_IS_SAVED)
    val isSaved: Boolean,
)

@Entity
internal class PartAppCustomBackgroundId(
    @ColumnInfo(name = EntryAppCustomBackground.FIELD_ID)
    val id: String,
)
