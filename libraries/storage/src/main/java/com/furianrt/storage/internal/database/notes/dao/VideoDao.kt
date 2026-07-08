package com.furianrt.storage.internal.database.notes.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVideo
import com.furianrt.storage.internal.database.notes.entities.PartVideoId
import com.furianrt.storage.internal.database.notes.entities.PartVideoUri
import kotlinx.coroutines.flow.Flow

@Dao
internal interface VideoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(videos: List<EntryNoteVideo>)

    @Update(entity = EntryNoteVideo::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(data: PartVideoUri)

    @Delete(entity = EntryNoteVideo::class)
    suspend fun delete(data: List<PartVideoId>)

    @Query("SELECT * FROM ${EntryNoteVideo.TABLE_NAME} WHERE ${EntryNoteVideo.FIELD_NOTE_ID} = :noteId")
    fun getVideos(noteId: String): Flow<List<EntryNoteVideo>>

    @Query("SELECT * FROM ${EntryNoteVideo.TABLE_NAME} WHERE ${EntryNoteVideo.FIELD_IS_SAVED} = 0")
    fun getUnsavedVideos(): Flow<List<EntryNoteVideo>>

    @Query("SELECT * FROM ${EntryNoteVideo.TABLE_NAME}")
    fun getAllVideos(): Flow<List<EntryNoteVideo>>

    @Query("SELECT EXISTS(SELECT * FROM ${EntryNoteVideo.TABLE_NAME} WHERE ${EntryNoteVideo.FIELD_ID} = :videoId AND ${EntryNoteVideo.FIELD_IS_SAVED} = 1)")
    suspend fun isSaved(videoId: String): Boolean
}