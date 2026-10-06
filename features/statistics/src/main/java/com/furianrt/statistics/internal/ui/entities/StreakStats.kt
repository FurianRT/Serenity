package com.furianrt.statistics.internal.ui.entities

import androidx.compose.runtime.Immutable

@Immutable
internal data class StreakStats(
    val currentStreak: Int,
    val longestStreak: Int,
    val streakDays: List<StreakDay>,
)
