package com.furianrt.reminders.internal.ui.entities

import java.time.DayOfWeek

internal data class DayItem(
    val day: DayOfWeek,
    val isSelected: Boolean,
)