package com.furianrt.reminders.internal.data

import androidx.core.app.NotificationManagerCompat
import com.furianrt.reminders.internal.domain.repositories.NotificationsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationsRepositoryImpl @Inject constructor(
    private val notificationManager: NotificationManagerCompat,
) : NotificationsRepository {

    override fun cancelNNotification(id: Int) {
        if (id != -1) {
            notificationManager.cancel(id)
        }
    }
}