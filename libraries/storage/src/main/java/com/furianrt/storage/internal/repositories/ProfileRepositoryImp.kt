package com.furianrt.storage.internal.repositories

import com.furianrt.domain.entities.BackupProfile
import com.furianrt.domain.repositories.ProfileRepository
import com.furianrt.storage.internal.database.auth.dao.BackupProfileDao
import com.furianrt.storage.internal.database.auth.entities.EntryBackupProfile
import com.furianrt.storage.internal.database.auth.mappers.toBackupProfile
import com.furianrt.storage.internal.database.auth.mappers.toEntryBackupProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ProfileRepositoryImp @Inject constructor(
    private val backupProfileDao: BackupProfileDao,
) : ProfileRepository {

    override fun getBackupProfile(): Flow<BackupProfile?> = backupProfileDao.getAllProfiles()
        .map { it.firstOrNull()?.toBackupProfile() }

    override fun isSignedIn(): Flow<Boolean> = backupProfileDao.getAllProfiles()
        .map { it.firstOrNull() != null }

    override suspend fun saveBackupProfile(profile: BackupProfile) {
        backupProfileDao.upsert(profile.toEntryBackupProfile())
    }

    override suspend fun deleteBackupProfile(email: String) {
        backupProfileDao.delete(EntryBackupProfile(email))
    }
}