package com.furianrt.storage.internal.database.notes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.furianrt.storage.internal.database.notes.entities.EntryNoteSticker.Anchor
import kotlinx.serialization.Serializable

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

    @ColumnInfo(name = FIELD_TYPE_ID)
    val typeId: String,

    @ColumnInfo(name = FIELD_SCALE)
    val scale: Float,

    @ColumnInfo(name = FIELD_ROTATION)
    val rotation: Float,

    @ColumnInfo(name = FIELD_IS_FLIPPED)
    val isFlipped: Boolean,

    @ColumnInfo(name = FIELD_ANCHORS)
    val anchors: List<Anchor>,

    @ColumnInfo(name = FIELD_EDIT_TIME)
    val editTime: Long,
) {
    @Serializable
    class Anchor(
        val id: String?,
        val biasX: Float,
        val biasY: Float,
    )

    companion object {
        const val TABLE_NAME = "Stickers"
        const val FIELD_ID = "id"
        const val FIELD_NOTE_ID = "note_id"
        const val FIELD_TYPE_ID = "type_id"
        const val FIELD_SCALE = "scale"
        const val FIELD_ROTATION = "rotation"
        const val FIELD_IS_FLIPPED = "is_flipped"
        const val FIELD_ANCHORS = "anchors"
        const val FIELD_EDIT_TIME = "edit_time"
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

    @ColumnInfo(name = EntryNoteSticker.FIELD_IS_FLIPPED)
    val isFlipped: Boolean,

    @ColumnInfo(name = EntryNoteSticker.FIELD_ANCHORS)
    val anchors: List<Anchor>,

    @ColumnInfo(name = EntryNoteSticker.FIELD_EDIT_TIME)
    val editTime: Long,
)

@Entity
internal class PartStickerId(
    @ColumnInfo(name = EntryNoteSticker.FIELD_ID)
    val id: String,
)
