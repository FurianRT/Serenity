package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.furianrt.storage.internal.database.notes.entities.EntryDeletedFile

@Dao
internal interface DeletedFilesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(media: List<EntryDeletedFile>)

    @Query("DELETE FROM ${EntryDeletedFile.TABLE_NAME}")
    suspend fun clear()
}