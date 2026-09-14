package com.furianrt.reminders.internal.domain.repositories

internal interface NotificationsRepository {
    fun cancelNNotification(id: Int)
}