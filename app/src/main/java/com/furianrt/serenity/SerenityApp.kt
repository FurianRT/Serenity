package com.furianrt.serenity

import android.app.Application
import android.os.StrictMode
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.furianrt.domain.repositories.MediaRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import javax.inject.Inject

@HiltAndroidApp
internal class SerenityApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var mediaRepository: MediaRepository

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            initStrictMode()
        }

        mediaRepository.enqueuePeriodicMediaSave()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setExecutor(Dispatchers.Default.asExecutor())
            .setTaskExecutor(Dispatchers.Default.asExecutor())
            .setMinimumLoggingLevel(Log.INFO)
            .build()

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
* 2.Множественный выбор записей для проставления тэгов(и еще для чего-нибудь)
* 3.Открытие других экранов через SharedElement
* */

/*
* TODO Экран заметки
*
* 1.Восстанавливать предыдущий фокус
* 2.Смена порядка фотографий
* 3.Исправить фокус при вставке медиа
* */

/*
* TODO Общее
*
* 1.Сменить иконку
* 4.Проверить RTL
* 7.Сделать шаблоны
* 8.Сделать блюр на пермишен диалоге
* 12.Сделать быстрый скролл вверх на основе общего серолла а не количества заметок
* */

/*
* TODO Видео
*
* 2.Добавить полноэкранный режим
* */