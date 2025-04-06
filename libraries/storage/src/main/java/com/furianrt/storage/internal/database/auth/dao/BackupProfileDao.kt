package com.furianrt.storage.internal.database.auth.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.furianrt.storage.internal.database.auth.entities.EntryBackupProfile
import kotlinx.coroutines.flow.Flow

@Dao
internal interface BackupProfileDao {
    @Query("SELECT * FROM ${EntryBackupProfile.TABLE_NAME}")
    fun getAllProfiles(): Flow<List<EntryBackupProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: EntryBackupProfile)

    @Delete(entity = EntryBackupProfile::class)
    suspend fun delete(id: EntryBackupProfile)
}