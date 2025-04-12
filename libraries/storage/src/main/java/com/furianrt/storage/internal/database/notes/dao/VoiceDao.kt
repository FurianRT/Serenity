package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVoice
import com.furianrt.storage.internal.database.notes.entities.PartVoiceId
import kotlinx.coroutines.flow.Flow

@Dao
internal interface VoiceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(voices: List<EntryNoteVoice>)

    @Delete(entity = EntryNoteVoice::class)
    suspend fun delete(data: List<PartVoiceId>)

    @Query("SELECT * FROM ${EntryNoteVoice.TABLE_NAME} WHERE ${EntryNoteVoice.FIELD_NOTE_ID} = :noteId")
    fun getVoices(noteId: String): Flow<List<EntryNoteVoice>>

    @Query("SELECT * FROM ${EntryNoteVoice.TABLE_NAME}")
    fun getAllVoices(): Flow<List<EntryNoteVoice>>
}