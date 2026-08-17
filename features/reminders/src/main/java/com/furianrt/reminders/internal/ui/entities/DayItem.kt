package com.furianrt.reminders.internal.ui.entities

import androidx.compose.runtime.Immutable
import java.time.DayOfWeek

@Immutable
internal data class DayItem(
    val day: DayOfWeek,
    val isSelected: Boolean,
)