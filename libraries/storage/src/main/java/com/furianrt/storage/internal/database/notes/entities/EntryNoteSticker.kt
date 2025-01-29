package com.furianrt.storage.internal.database.notes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = EntryNoteSticker.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = EntryNote::class,
            parentColumns = [EntryNote.FIELD_ID],
            childColumns = [EntryNoteSticker.FIELD_NOTE_ID],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
internal class EntryNoteSticker(
    @PrimaryKey
    @ColumnInfo(name = FIELD_ID)
    val id: String,

    @ColumnInfo(name = FIELD_NOTE_ID, index = true)
    val noteId: String,

    @ColumnInfo(name = FIELD_TYPE)
    val type: Int,

    @ColumnInfo(name = FIELD_SCALE)
    val scale: Float,

    @ColumnInfo(name = FIELD_ROTATION)
    val rotation: Float,

    @ColumnInfo(name = FIELD_ANCHOR_ID)
    val anchorId: String?,

    @ColumnInfo(name = FIELD_BIAS_X)
    val biasX: Float,

    @ColumnInfo(name = FIELD_BIAS_Y)
    val biasY: Float,
) {
    companion object {
        const val TABLE_NAME = "Stickers"
        const val FIELD_ID = "id"
        const val FIELD_NOTE_ID = "note_id"
        const val FIELD_TYPE = "type"
        const val FIELD_SCALE = "scale"
        const val FIELD_ROTATION = "rotation"
        const val FIELD_ANCHOR_ID = "anchor_id"
        const val FIELD_BIAS_X = "bias_x"
        const val FIELD_BIAS_Y = "bias_y"
    }
}

@Entity
internal class PartStickerTransformations(
    @ColumnInfo(name = EntryNoteSticker.FIELD_ID)
    val id: String,

    @ColumnInfo(name = EntryNoteSticker.FIELD_SCALE)
    val scale: Float,

    @ColumnInfo(name = EntryNoteSticker.FIELD_ROTATION)
    val rotation: Float,

    @ColumnInfo(name = EntryNoteSticker.FIELD_ANCHOR_ID)
    val anchorId: String?,

    @ColumnInfo(name = EntryNoteSticker.FIELD_BIAS_X)
    val biasX: Float,

    @ColumnInfo(name = EntryNoteSticker.FIELD_BIAS_Y)
    val biasY: Float,
)

@Entity
internal class PartStickerId(
    @ColumnInfo(name = EntryNoteSticker.FIELD_ID)
    val id: String,
)
