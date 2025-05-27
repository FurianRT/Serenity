package com.furianrt.storage.internal.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.furianrt.common.ErrorTracker
import com.furianrt.domain.usecase.DeleteTemplateNotesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

private const val WORK_NAME_PERIODIC = "DatabaseCleanupPeriodic"
private const val WORK_NAME_ONE_TIME = "DatabaseCleanupOneTime"

@HiltWorker
internal class DatabaseCleanupWorker @AssistedInject constructor(
    private val deleteTemplateNotesUseCase: DeleteTemplateNotesUseCase,
    private val errorTracker: ErrorTracker,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    companion object {
        fun enqueueOneTime(context: Context) {
            val workRequest = OneTimeWorkRequest.Builder(DatabaseCleanupWorker::class.java)
                .setInitialDelay(duration = 5, timeUnit = TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME_ONE_TIME,
                ExistingWorkPolicy.KEEP,
                workRequest,
            )
        }

        fun enqueuePeriodic(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresDeviceIdle(true)
                .build()

            val workRequest = PeriodicWorkRequest.Builder(
                workerClass = DatabaseCleanupWorker::class.java,
                repeatInterval = 2,
                repeatIntervalTimeUnit = TimeUnit.DAYS,
            )
                .setConstraints(constraints)
                .setInitialDelay(duration = 2, timeUnit = TimeUnit.DAYS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                uniqueWorkName = WORK_NAME_PERIODIC,
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
                request = workRequest,
            )
        }
    }

    override suspend fun doWork(): Result = try {
        deleteTemplateNotesUseCase()
        Result.success()
    } catch (e: Exception) {
        errorTracker.trackNonFatalError(e)
        Result.retry()
    }
}