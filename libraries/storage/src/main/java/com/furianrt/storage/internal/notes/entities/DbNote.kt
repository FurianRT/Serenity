package com.furianrt.storage.internal.notes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.furianrt.storage.api.entities.Note
import com.furianrt.storage.internal.notes.entities.DbNote.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
internal class DbNote(
    @ColumnInfo(name = FIELD_ID)
    @PrimaryKey(autoGenerate = false)
    val id: String,

    @ColumnInfo(name = FIELD_TIME)
    val time: Long,

    @ColumnInfo(name = FIELD_TITLE)
    val title: String,
) {
    companion object {
        const val TABLE_NAME = "Notes"
        const val FIELD_ID = "id"
        const val FIELD_TIME = "time"
        const val FIELD_TITLE = "title"
    }
}

internal fun DbNote.toNote() = Note(
    id = id,
    time = time,
    title = title,
)
