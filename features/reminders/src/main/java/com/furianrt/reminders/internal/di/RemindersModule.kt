package com.furianrt.reminders.internal.di

import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.furianrt.reminders.api.RemindersApi
import com.furianrt.reminders.internal.data.NotificationsRepositoryImpl
import com.furianrt.reminders.internal.domain.repositories.NotificationsRepository
import com.furianrt.reminders.internal.receivers.ReminderReceiver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RemindersModule {
    @Binds
    @Singleton
    fun NotificationsRepository(imp: NotificationsRepositoryImpl): NotificationsRepository

    companion object {
        @Provides
        @Singleton
        fun notificationManager(
            @ApplicationContext context: Context,
        ): NotificationManagerCompat = NotificationManagerCompat.from(context)

        @Provides
        @Singleton
        fun remindersApi(
            notificationsRepository: NotificationsRepository,
        ): RemindersApi = object : RemindersApi {
            override fun cancelNotification(intent: Intent) {
                val notificationId = intent.getIntExtra(ReminderReceiver.EXTRA_NOTIFICATION_ID, -1)
                notificationsRepository.cancelNNotification(notificationId)
            }
        }
    }
}