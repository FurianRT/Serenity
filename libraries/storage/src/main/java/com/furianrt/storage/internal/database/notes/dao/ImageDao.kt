package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.PartImageId
import com.furianrt.storage.internal.database.notes.entities.PartImageUri
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(images: List<EntryNoteImage>)

    @Update(entity = EntryNoteImage::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(data: PartImageUri)

    @Delete(entity = EntryNoteImage::class)
    suspend fun delete(data: List<PartImageId>)

    @Query("SELECT * FROM ${EntryNoteImage.TABLE_NAME} WHERE ${EntryNoteImage.FIELD_NOTE_ID} = :noteId")
    fun getImages(noteId: String): Flow<List<EntryNoteImage>>

    @Query("SELECT * FROM ${EntryNoteImage.TABLE_NAME} WHERE ${EntryNoteImage.FIELD_IS_SAVED} = 0")
    fun getUnsavedImages(): Flow<List<EntryNoteImage>>

    @Query("SELECT * FROM ${EntryNoteImage.TABLE_NAME}")
    fun getAllImages(): Flow<List<EntryNoteImage>>

    @Query("SELECT EXISTS(SELECT * FROM ${EntryNoteImage.TABLE_NAME} WHERE ${EntryNoteImage.FIELD_ID} = :imageId AND ${EntryNoteImage.FIELD_IS_SAVED} = 1)")
    suspend fun isSaved(imageId: String): Boolean
}
