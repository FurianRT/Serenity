package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.furianrt.storage.internal.database.notes.entities.EntryContentBlock
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ContentBlockDao {
    @Upsert
    suspend fun upsert(block: EntryContentBlock)

    @Query("DELETE FROM ${EntryContentBlock.TABLE_NAME} WHERE ${EntryContentBlock.FIELD_ID} = :blockId")
    suspend fun delete(blockId: String)

    @Query("SELECT * FROM ${EntryContentBlock.TABLE_NAME} WHERE ${EntryContentBlock.FIELD_NOTE_ID} = :noteId")
    fun getBlocks(noteId: String): Flow<List<EntryContentBlock>>
}
