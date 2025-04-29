package com.furianrt.backup.internal.domain

import android.content.Context
import com.furianrt.backup.api.BackupApi
import com.furianrt.backup.internal.domain.repositories.BackupRepository
import com.furianrt.backup.internal.workers.AutoBackupWorker
import com.furianrt.domain.repositories.ProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BackupMediator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profileRepository: ProfileRepository,
    private val backupRepository: BackupRepository,
) : BackupApi {

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
}