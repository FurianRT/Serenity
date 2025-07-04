package com.furianrt.serenity

import android.app.Application
import android.os.StrictMode
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.managers.SyncManager
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.domain.repositories.NotesRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
internal class SerenityApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var mediaRepository: MediaRepository

    @Inject
    lateinit var notesRepository: NotesRepository

    @Inject
    lateinit var syncManager: SyncManager

    @Inject
    lateinit var dispatchers: DispatchersProvider

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            initStrictMode()
        }
        startPeriodicWorks()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setExecutor(Dispatchers.Default.asExecutor())
            .setTaskExecutor(Dispatchers.Default.asExecutor())
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    @OptIn(DelicateCoroutinesApi::class)
    private fun startPeriodicWorks() {
        notesRepository.enqueuePeriodicCleanup()
        mediaRepository.enqueuePeriodicMediaSave()
        GlobalScope.launch { syncManager.tryStartAutoBackup() }
    }

    private fun initStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectNetwork()
                .detectResourceMismatches()
                .detectUnbufferedIo()
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
}

/*
* TODO Главный экран
* 3.Открытие других экранов через SharedElement
* */

/*
* TODO Экран заметки
*
* 1.Восстанавливать предыдущий фокус
* 3.Исправить фокус при вставке медиа
* */

/*
* TODO Общее
*
* 1.Сменить иконку
* 4.Проверить RTL
* 7.Сделать шаблоны
* */
