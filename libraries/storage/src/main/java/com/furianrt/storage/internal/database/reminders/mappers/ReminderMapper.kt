package com.furianrt.storage.internal.database.reminders.mappers

import com.furianrt.domain.entities.Reminder
import com.furianrt.storage.internal.database.reminders.entities.EntryReminder
import java.time.DayOfWeek

internal fun EntryReminder.toDomain() = Reminder(
    id = id,
    title = title,
    time = time,
    daysOfWeek = daysOfWeek.ifEmpty {
        setOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY,
        )
    },
)

internal fun Reminder.toEntry() = EntryReminder(
    id = id,
    title = title,
    time = time,
    daysOfWeek = daysOfWeek,
)
