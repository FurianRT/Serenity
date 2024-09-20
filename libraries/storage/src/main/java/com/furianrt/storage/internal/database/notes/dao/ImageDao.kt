package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.PartImageName
import com.furianrt.storage.internal.database.notes.entities.PartImageUri
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(images: List<EntryNoteImage>)

    @Update(entity = EntryNoteImage::class)
    suspend fun update(data: PartImageUri)

    @Delete(entity = EntryNoteImage::class)
    suspend fun delete(data: List<PartImageName>)

    @Query("SELECT * FROM ${EntryNoteImage.TABLE_NAME} WHERE ${EntryNoteImage.FIELD_NOTE_ID} = :noteId")
    fun getImages(noteId: String): Flow<List<EntryNoteImage>>

    @Query("SELECT EXISTS(SELECT * FROM ${EntryNoteImage.TABLE_NAME} WHERE ${EntryNoteImage.FIELD_NAME} = :imageName AND ${EntryNoteImage.FIELD_IS_SAVED} = 1)")
    suspend fun isSaved(imageName: String): Boolean
}
