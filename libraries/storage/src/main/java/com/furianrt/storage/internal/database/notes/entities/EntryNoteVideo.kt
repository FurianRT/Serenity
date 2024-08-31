package com.furianrt.storage.internal.database.notes.entities

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = EntryNoteVideo.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = EntryNoteVideo::class,
            parentColumns = [EntryContentBlock.FIELD_ID],
            childColumns = [EntryNoteVideo.FIELD_BLOCK_ID],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
internal class EntryNoteVideo(
    @PrimaryKey
    @ColumnInfo(name = FIELD_ID)
    val id: String,

    @ColumnInfo(name = FIELD_BLOCK_ID, index = true)
    val blockId: String,

    @ColumnInfo(name = FIELD_URI)
    val uri: Uri,

    @ColumnInfo(name = FIELD_RATIO)
    val ratio: Float,

    @ColumnInfo(name = FIELD_DURATION)
    val duration: Int,

    @ColumnInfo(name = FIELD_POSITION)
    val position: Int,
) {
    companion object {
        const val TABLE_NAME = "Videos"
        const val FIELD_ID = "id"
        const val FIELD_BLOCK_ID = "block_id"
        const val FIELD_URI = "uri"
        const val FIELD_RATIO = "ratio"
        const val FIELD_DURATION = "duration"
        const val FIELD_POSITION = "position"
    }
}