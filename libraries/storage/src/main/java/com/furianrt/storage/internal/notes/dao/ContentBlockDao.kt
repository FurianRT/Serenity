package com.furianrt.storage.internal.notes.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.furianrt.storage.internal.notes.entities.EntryContentBlock

@Dao
internal interface ContentBlockDao {
    @Upsert
    suspend fun upsert(block: EntryContentBlock)
}
