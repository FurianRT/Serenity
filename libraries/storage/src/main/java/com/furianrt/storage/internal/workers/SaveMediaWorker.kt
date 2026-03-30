package com.furianrt.storage.internal.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.furianrt.common.ErrorTracker
import com.furianrt.storage.internal.managers.MediaSaver
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

private const val WORK_NAME_ONE_TIME = "SaveMediaOneTime"

@HiltWorker
internal class SaveMediaWorker @AssistedInject constructor(
    private val mediaSaver: MediaSaver,
    private val errorTracker: ErrorTracker,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    companion object {
        fun enqueueOneTime(context: Context) {
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME_ONE_TIME,
                ExistingWorkPolicy.APPEND,
                OneTimeWorkRequest.Builder(SaveMediaWorker::class.java).build(),
            )
        }
    }

    override suspend fun doWork(): Result = try {
        mediaSaver.saveAll()
        Result.success()
    } catch (e: Exception) {
        errorTracker.trackNonFatalError(e)
        Result.retry()
    }
}