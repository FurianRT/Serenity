package com.furianrt.reminders.internal.ui.list.extensions

import com.furianrt.domain.entities.Reminder
import com.furianrt.reminders.internal.ui.entities.DayItem
import com.furianrt.reminders.internal.ui.list.entities.ReminderItem
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter

internal fun Reminder.toReminderItem(
    allDaysOfWeek: List<DayOfWeek>,
    timeFormatter: DateTimeFormatter,
) = ReminderItem(
    id = id,
    title = title,
    time = time.format(timeFormatter),
    daysOfWeek = allDaysOfWeek.map { day ->
        DayItem(
            day = day,
            isSelected = daysOfWeek.contains(day),
        )
    },
)