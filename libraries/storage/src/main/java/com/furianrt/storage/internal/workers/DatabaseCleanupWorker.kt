package com.furianrt.storage.internal.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.furianrt.common.ErrorTracker
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.domain.repositories.TagsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

private const val WORK_NAME_PERIODIC = "DatabaseCleanupPeriodic"

@HiltWorker
internal class DatabaseCleanupWorker @AssistedInject constructor(
    private val notesRepository: NotesRepository,
    private val tagsRepository: TagsRepository,
    private val errorTracker: ErrorTracker,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    companion object {
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
        notesRepository.deleteTemplates()
        tagsRepository.deleteUnusedTags()
        Result.success()
    } catch (e: Exception) {
        errorTracker.trackNonFatalError(e)
        Result.retry()
    }
}