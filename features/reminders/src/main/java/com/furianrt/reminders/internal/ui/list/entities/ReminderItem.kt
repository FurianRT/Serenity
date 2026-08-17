package com.furianrt.reminders.internal.ui.list.entities

import androidx.compose.runtime.Immutable
import com.furianrt.reminders.internal.ui.entities.DayItem

@Immutable
internal data class ReminderItem(
    val id: String,
    val title: String?,
    val time: String,
    val daysOfWeek: List<DayItem>,
)