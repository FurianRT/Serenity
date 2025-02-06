package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furianrt.storage.internal.database.notes.entities.EntryNoteSticker
import com.furianrt.storage.internal.database.notes.entities.PartStickerId
import com.furianrt.storage.internal.database.notes.entities.PartStickerTransformations
import kotlinx.coroutines.flow.Flow

@Dao
internal interface StickerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(stickers: List<EntryNoteSticker>)

    @Update(entity = EntryNoteSticker::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(data: List<PartStickerTransformations>)

    @Delete(entity = EntryNoteSticker::class)
    suspend fun delete(ids: List<PartStickerId>)

    @Query("SELECT * FROM ${EntryNoteSticker.TABLE_NAME} WHERE ${EntryNoteSticker.FIELD_NOTE_ID} = :noteId")
    fun getStickers(noteId: String): Flow<List<EntryNoteSticker>>
}