package com.furianrt.serenity

import android.app.Application
import android.os.StrictMode
import android.util.Log
import androidx.compose.runtime.Composer
import androidx.compose.runtime.tooling.ComposeStackTraceMode
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.CachePolicy
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.managers.SyncManager
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.serenity.domain.IncrementLaunchCountUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
internal class SerenityApp : Application(), Configuration.Provider, SingletonImageLoader.Factory {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var mediaRepository: MediaRepository

    @Inject
    lateinit var notesRepository: NotesRepository

    @Inject
    lateinit var incrementLaunchCountUseCase: IncrementLaunchCountUseCase

    @Inject
    lateinit var syncManager: SyncManager

    @Inject
    lateinit var dispatchers: DispatchersProvider

    private val scope by lazy(LazyThreadSafetyMode.NONE) {
        CoroutineScope(dispatchers.main + SupervisorJob())
    }

    override fun onCreate() {
        super.onCreate()
        Composer.setDiagnosticStackTraceMode(ComposeStackTraceMode.Auto)
        if (BuildConfig.DEBUG) {
            initStrictMode()
        }
        SingletonImageLoader.setSafe(this)
        startPeriodicWorks()
        updateLaunchCount()
    }

    override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .setExecutor(Dispatchers.Default.asExecutor())
        .setTaskExecutor(Dispatchers.Default.asExecutor())
        .setMinimumLoggingLevel(Log.INFO)
        .build()

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(this)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startPeriodicWorks() {
        notesRepository.enqueuePeriodicCleanup()
        mediaRepository.enqueuePeriodicMediaSave()
        scope.launch { syncManager.tryStartAutoBackup() }
    }

    private fun initStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectNetwork()
                .detectResourceMismatches()
                //.detectUnbufferedIo()
                .penaltyLog()
                .penaltyDeath()
                .build()
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .detectActivityLeaks()
                .detectContentUriWithoutPermission()
                .detectFileUriExposure()
                .penaltyLog()
                .build()
        )
    }

    private fun updateLaunchCount() {
        scope.launch { incrementLaunchCountUseCase() }
    }
}
