package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ImageDao {
    @Upsert
    suspend fun upsert(images: List<EntryNoteImage>)

    @Delete
    suspend fun delete(image: EntryNoteImage)

    @Query("SELECT * FROM ${EntryNoteImage.TABLE_NAME} WHERE ${EntryNoteImage.FIELD_NOTE_ID} = :noteId")
    fun getImages(noteId: String): Flow<List<EntryNoteImage>>
}
