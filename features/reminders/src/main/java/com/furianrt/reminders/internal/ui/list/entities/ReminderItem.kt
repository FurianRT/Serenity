package com.furianrt.reminders.internal.ui.list.entities

import com.furianrt.reminders.internal.ui.entities.DayItem

internal data class ReminderItem(
    val id: String,
    val title: String?,
    val time: String,
    val daysOfWeek: List<DayItem>,
)