package com.furianrt.storage.internal.database.auth.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
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