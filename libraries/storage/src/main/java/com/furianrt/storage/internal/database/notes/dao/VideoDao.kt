package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVideo
import com.furianrt.storage.internal.database.notes.entities.PartVideoUri

@Dao
internal interface VideoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(videos: List<EntryNoteVideo>)

    @Update(entity = EntryNoteVideo::class)
    suspend fun update(data: PartVideoUri)

    @Query("DELETE FROM ${EntryNoteVideo.TABLE_NAME} WHERE ${EntryNoteVideo.FIELD_ID} = :videoId")
    suspend fun delete(videoId: String)

    @Query("SELECT EXISTS(SELECT * FROM ${EntryNoteVideo.TABLE_NAME} WHERE ${EntryNoteVideo.FIELD_ID} = :imageId AND ${EntryNoteVideo.FIELD_IS_SAVED} = 1)")
    suspend fun isSaved(imageId: String): Boolean
}