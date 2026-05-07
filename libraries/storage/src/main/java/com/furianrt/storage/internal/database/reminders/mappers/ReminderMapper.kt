package com.furianrt.storage.internal.database.reminders.mappers

import com.furianrt.domain.entities.Reminder
import com.furianrt.storage.internal.database.reminders.entities.EntryReminder

internal fun EntryReminder.toDomain() = Reminder(
    id = id,
    title = title,
    time = time,
    daysOfWeek = daysOfWeek,
)

internal fun Reminder.toEntry() = EntryReminder(
    id = id,
    title = title,
    time = time,
    daysOfWeek = daysOfWeek,
)
