package com.furianrt.storage.internal.notes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.furianrt.storage.internal.notes.entities.EntryNote.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
internal class EntryNote(
    @ColumnInfo(name = FIELD_ID)
    @PrimaryKey(autoGenerate = false)
    val id: String,

    @ColumnInfo(name = FIELD_TIMESTAMP)
    val timestamp: Long,
) {
    companion object {
        const val TABLE_NAME = "Notes"
        const val FIELD_ID = "id"
        const val FIELD_TIMESTAMP = "timestamp"
    }
}
