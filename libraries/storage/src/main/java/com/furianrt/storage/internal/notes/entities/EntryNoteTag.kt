package com.furianrt.storage.internal.notes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = EntryNoteTag.TABLE_NAME)
internal class EntryNoteTag(
    @ColumnInfo(name = FIELD_ID)
    @PrimaryKey(autoGenerate = false)
    val id: String,

    @ColumnInfo(name = FIELD_TITLE)
    val title: String,

    @ColumnInfo(name = FIELD_BLOCK_POSITION)
    val blockPosition: Int,
) {
    companion object {
        const val TABLE_NAME = "Tags"
        const val FIELD_ID = "id"
        const val FIELD_TITLE = "title"
        const val FIELD_BLOCK_POSITION = "block_position"
    }
}
