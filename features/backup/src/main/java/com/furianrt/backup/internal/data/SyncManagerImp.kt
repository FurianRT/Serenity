package com.furianrt.backup.internal.data

import android.content.Context
import com.furianrt.backup.internal.domain.BackupDataManager
import com.furianrt.backup.internal.domain.RestoreDataManager
import com.furianrt.backup.internal.domain.entities.SyncState
import com.furianrt.backup.internal.domain.repositories.BackupRepository
import com.furianrt.backup.internal.workers.AutoBackupWorker
import com.furianrt.domain.managers.SyncManager
import com.furianrt.domain.repositories.ProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class SyncManagerImp @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val profileRepository: ProfileRepository,
    private val backupRepository: BackupRepository,
    private val backupDataManager: BackupDataManager,
    private val restoreDataManager: RestoreDataManager,
) : SyncManager {

    override suspend fun tryStartAutoBackup() {
        val isSignedIn = profileRepository.isSignedIn().first()
        val isBackupEnabled = backupRepository.isAutoBackupEnabled().first()
        if (isSignedIn && isBackupEnabled) {
            val backupPeriod = backupRepository.getAutoBackupPeriod().first()
            val (repeatInterval, repeatIntervalTimeUnit) = backupPeriod.getTimeUnit()
            AutoBackupWorker.enqueuePeriodic(
                context = context,
                repeatInterval = repeatInterval,
                repeatIntervalTimeUnit = repeatIntervalTimeUnit,
            )
        }
    }

    override fun isBackupInProgress(): Boolean =
        backupDataManager.state.value is SyncState.Starting ||
                backupDataManager.state.value is SyncState.Progress

    override fun isRestoreInProgress(): Boolean =
        restoreDataManager.state.value is SyncState.Starting ||
                restoreDataManager.state.value is SyncState.Progress
}