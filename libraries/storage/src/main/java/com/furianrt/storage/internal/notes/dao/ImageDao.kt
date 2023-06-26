package com.furianrt.storage.internal.notes.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.furianrt.storage.internal.notes.entities.EntryNoteImage

@Dao
internal interface ImageDao {
    @Upsert
    suspend fun upsert(image: EntryNoteImage)
}
