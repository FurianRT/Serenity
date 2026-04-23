package com.furianrt.storage.internal.database.notes.entities

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(tableName = EntryCustomSticker.TABLE_NAME)
internal class EntryCustomSticker(
    @PrimaryKey
    @ColumnInfo(name = FIELD_ID)
    val id: String,

    @ColumnInfo(name = FIELD_NAME)
    val name: String,

    @ColumnInfo(name = FIELD_URI)
    val uri: Uri,

    @ColumnInfo(name = FIELD_RATIO)
    val ratio: Float,

    @ColumnInfo(name = FIELD_ADDED_DATE)
    val addedDate: ZonedDateTime,

    @ColumnInfo(name = FIELD_IS_SAVED)
    val isSaved: Boolean,

    @ColumnInfo(name = FIELD_IS_HIDDEN)
    val isHidden: Boolean,
) {
    companion object {
        const val TABLE_NAME = "CustomStickers"
        const val FIELD_ID = "id"
        const val FIELD_NAME = "name"
        const val FIELD_URI = "uri"
        const val FIELD_RATIO = "ratio"
        const val FIELD_ADDED_DATE = "added_date"
        const val FIELD_IS_SAVED = "is_saved"
        const val FIELD_IS_HIDDEN = "is_hidden"
    }
}

@Entity
internal class PartCustomStickerUri(
    @ColumnInfo(name = EntryCustomSticker.FIELD_ID)
    val id: String,

    @ColumnInfo(name = EntryCustomSticker.FIELD_NAME)
    val name: String,

    @ColumnInfo(name = EntryCustomSticker.FIELD_URI)
    val uri: Uri,

    @ColumnInfo(name = EntryCustomSticker.FIELD_IS_SAVED)
    val isSaved: Boolean,
)

@Entity
internal class PartCustomStickerIsHidden(
    @ColumnInfo(name = EntryCustomSticker.FIELD_ID)
    val id: String,

    @ColumnInfo(name = EntryCustomSticker.FIELD_IS_HIDDEN)
    val isHidden: Boolean,
)

@Entity
internal class PartCustomStickerId(
    @ColumnInfo(name = EntryCustomSticker.FIELD_ID)
    val id: String,
)
