package com.furianrt.reminders.internal.schedulers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.furianrt.domain.entities.Reminder
import com.furianrt.reminders.internal.receivers.ReminderReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ReminderScheduler @Inject constructor(
    @param:ApplicationContext private val applicationContext: Context,
) {
    private val alarmManager by lazy {
        applicationContext.getSystemService(AlarmManager::class.java)
    }

    fun schedule(reminder: Reminder) {
        cancel(reminder)
        val triggerAt = calculateNextTrigger(reminder) ?: return

        val intent = Intent(applicationContext, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminder.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        if (canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt.toInstant().toEpochMilli(),
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt.toInstant().toEpochMilli(),
                pendingIntent
            )
        }
    }

    fun cancel(reminder: Reminder) {
        val intent = Intent(applicationContext, ReminderReceiver::class.java)
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                applicationContext,
                reminder.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            ),
        )
    }

    fun canScheduleExactAlarms(): Boolean = alarmManager.canScheduleExactAlarms()

    private fun calculateNextTrigger(reminder: Reminder): ZonedDateTime? {
        val now = ZonedDateTime.now()
        val today = now.toLocalDate()

        for (i in 0..7) {
            val date = today.plusDays(i.toLong())
            if (date.dayOfWeek in reminder.daysOfWeek) {
                val candidate = ZonedDateTime.of(date, reminder.time, now.zone)
                if (candidate.isAfter(now)) {
                    return candidate
                }
            }
        }
        return null
    }
}

