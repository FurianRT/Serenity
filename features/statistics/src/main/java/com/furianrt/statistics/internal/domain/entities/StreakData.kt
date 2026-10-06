package com.furianrt.statistics.internal.domain.entities

import java.time.LocalDate

internal data class StreakData(
    val currentStreak: Int,
    val longestStreak: Int,
    val streakDays: List<StreakDay>,
) {
    data class StreakDay(
        val hasNotes: Boolean,
        val date: LocalDate,
    )
}