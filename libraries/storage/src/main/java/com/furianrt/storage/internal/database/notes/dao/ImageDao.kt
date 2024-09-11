package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.PartImageUri

@Dao
internal interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(images: List<EntryNoteImage>)

    @Update(entity = EntryNoteImage::class)
    suspend fun update(data: PartImageUri)

    @Query("DELETE FROM ${EntryNoteImage.TABLE_NAME} WHERE ${EntryNoteImage.FIELD_ID} = :imageId")
    suspend fun delete(imageId: String)

    @Query("SELECT EXISTS(SELECT * FROM ${EntryNoteImage.TABLE_NAME} WHERE ${EntryNoteImage.FIELD_ID} = :imageId AND ${EntryNoteImage.FIELD_IS_SAVED} = 1)")
    suspend fun isSaved(imageId: String): Boolean
}
