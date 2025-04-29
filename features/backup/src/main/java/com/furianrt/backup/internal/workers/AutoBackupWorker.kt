package com.furianrt.backup.internal.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.furianrt.backup.internal.domain.BackupDataManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

private const val WORK_NAME = "AutoBackup"

@HiltWorker
internal class AutoBackupWorker @AssistedInject constructor(
    private val backupDataManager: BackupDataManager,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    companion object {
        fun enqueuePeriodic(
            context: Context,
            repeatInterval: Long,
            repeatIntervalTimeUnit: TimeUnit,
        ) {
            createWork(
                context = context,
                repeatInterval = repeatInterval,
                repeatIntervalTimeUnit = repeatIntervalTimeUnit,
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
            )
        }

        fun update(
            context: Context,
            repeatInterval: Long,
            repeatIntervalTimeUnit: TimeUnit,
        ) {
            createWork(
                context = context,
                repeatInterval = repeatInterval,
                repeatIntervalTimeUnit = repeatIntervalTimeUnit,
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            )
        }

        fun cancelWork(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }

        private fun createWork(
            context: Context,
            repeatInterval: Long,
            repeatIntervalTimeUnit: TimeUnit,
            existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy,
        ) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(requiresDeviceIdle = true)
                .build()

            val workRequest = PeriodicWorkRequest.Builder(
                workerClass = AutoBackupWorker::class.java,
                repeatInterval = repeatInterval,
                repeatIntervalTimeUnit = repeatIntervalTimeUnit,
            )
                .setConstraints(constraints)
                .setInitialDelay(duration = repeatInterval, timeUnit = repeatIntervalTimeUnit)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                uniqueWorkName = WORK_NAME,
                existingPeriodicWorkPolicy = existingPeriodicWorkPolicy,
                request = workRequest,
            )
        }
    }

    override suspend fun doWork(): Result = try {
        backupDataManager.startBackup()
        Result.success()
    } catch (exception: Exception) {
        Result.retry()
        // TODO отправлять не фатальную ошибку в firebase
    }
}