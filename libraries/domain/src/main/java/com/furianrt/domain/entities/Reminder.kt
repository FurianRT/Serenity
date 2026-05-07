package com.furianrt.domain.entities

import java.time.DayOfWeek
import java.time.LocalTime

data class Reminder(
    val id: String,
    val title: String?,
    val time: LocalTime,
    val daysOfWeek: Set<DayOfWeek>,
)