package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVideo
import kotlinx.coroutines.flow.Flow

@Dao
internal interface VideoDao {
    @Upsert
    suspend fun upsert(videos: List<EntryNoteVideo>)

    @Delete
    suspend fun delete(video: EntryNoteVideo)

    @Query("SELECT * FROM ${EntryNoteVideo.TABLE_NAME} WHERE ${EntryNoteVideo.FIELD_NOTE_ID} = :noteId")
    fun getVideos(noteId: String): Flow<List<EntryNoteVideo>>
}