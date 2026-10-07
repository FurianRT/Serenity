package com.furianrt.statistics.internal.ui.entities

import com.furianrt.statistics.internal.domain.entities.MoodData
import com.furianrt.statistics.internal.domain.entities.StreakData

internal fun StreakData.StreakDay.toUI() = StreakDay(
    hasNotes = hasNotes,
    date = date,
)

internal fun MoodData.Mood.toUI() = MoodStats.Mood(
    level = when (level) {
        MoodData.MoodLevel.TERRIBLE -> MoodStats.Level.TERRIBLE
        MoodData.MoodLevel.BAD -> MoodStats.Level.BAD
        MoodData.MoodLevel.SAD -> MoodStats.Level.SAD
        MoodData.MoodLevel.NORMAL -> MoodStats.Level.NORMAL
        MoodData.MoodLevel.GOOD -> MoodStats.Level.GOOD
        MoodData.MoodLevel.PERFECT -> MoodStats.Level.PERFECT
    },
    percent = (percent * 100f),
)
