package com.furianrt.domain.repositories

import com.furianrt.domain.entities.BackupProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getBackupProfile(): Flow<BackupProfile?>
    fun isSignedIn(): Flow<Boolean>
    suspend fun saveBackupProfile(profile: BackupProfile)
    suspend fun deleteBackupProfile(email: String)
}