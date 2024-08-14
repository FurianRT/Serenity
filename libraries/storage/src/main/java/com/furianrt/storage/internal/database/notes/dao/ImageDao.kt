package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage

@Dao
internal interface ImageDao {
    @Upsert
    suspend fun upsert(image: EntryNoteImage)
}
