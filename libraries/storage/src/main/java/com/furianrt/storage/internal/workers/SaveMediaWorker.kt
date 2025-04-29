package com.furianrt.storage.internal.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.furianrt.storage.internal.managers.MediaSaver
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

private const val WORK_NAME_PERIODIC = "SaveMediaPeriodic"

@HiltWorker
internal class SaveMediaWorker @AssistedInject constructor(
    private val mediaSaver: MediaSaver,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    companion object {
        fun enqueuePeriodic(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequest.Builder(
                workerClass = SaveMediaWorker::class.java,
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.DAYS,
            )
                .setConstraints(constraints)
                .setInitialDelay(duration = 1, timeUnit = TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                uniqueWorkName = WORK_NAME_PERIODIC,
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
                request = workRequest,
            )
        }
    }

    override suspend fun doWork(): Result = try {
        mediaSaver.saveAll()
        Result.success()
    } catch (exception: Exception) {
        Result.retry()
        // TODO отправлять не фатальную ошибку в firebase
    }
}