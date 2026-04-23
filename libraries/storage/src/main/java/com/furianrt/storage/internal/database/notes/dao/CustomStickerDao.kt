package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.furianrt.storage.internal.database.notes.entities.EntryCustomSticker
import com.furianrt.storage.internal.database.notes.entities.PartCustomStickerId
import com.furianrt.storage.internal.database.notes.entities.PartCustomStickerIsHidden
import com.furianrt.storage.internal.database.notes.entities.PartCustomStickerUri
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CustomStickerDao {
    @Upsert
    suspend fun upsert(stickers: List<EntryCustomSticker>)

    @Update(entity = EntryCustomSticker::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(data: PartCustomStickerUri)

    @Update(entity = EntryCustomSticker::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(data: PartCustomStickerIsHidden)

    @Delete(entity = EntryCustomSticker::class)
    suspend fun delete(sticker: PartCustomStickerId)

    @Query(
        """
    SELECT *
    FROM ${EntryCustomSticker.TABLE_NAME}
    WHERE ${EntryCustomSticker.FIELD_IS_SAVED} = 0
    """
    )
    fun getUnsavedStickers(): Flow<List<EntryCustomSticker>>

    @Query(
        """
    SELECT *
    FROM ${EntryCustomSticker.TABLE_NAME} 
    WHERE ${EntryCustomSticker.FIELD_IS_HIDDEN} = 0
    """
    )
    fun getNotHiddenStickers(): Flow<List<EntryCustomSticker>>

    @Query(
        """
    SELECT *
    FROM ${EntryCustomSticker.TABLE_NAME} 
    WHERE ${EntryCustomSticker.FIELD_IS_HIDDEN} = 1
    """
    )
    fun getHiddenStickers(): Flow<List<EntryCustomSticker>>

    @Query(
        """
    SELECT *
    FROM ${EntryCustomSticker.TABLE_NAME}
    """
    )
    fun getAllStickers(): Flow<List<EntryCustomSticker>>

    @Query(
        """
    SELECT EXISTS(
        SELECT *
        FROM ${EntryCustomSticker.TABLE_NAME}
        WHERE ${EntryCustomSticker.FIELD_ID} = :id
        AND ${EntryCustomSticker.FIELD_IS_SAVED} = 1
    )
    """
    )
    suspend fun isSaved(id: String): Boolean
}