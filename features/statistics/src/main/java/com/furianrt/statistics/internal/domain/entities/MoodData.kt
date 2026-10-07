package com.furianrt.statistics.internal.domain.entities

import java.time.DayOfWeek
import java.time.ZonedDateTime

internal data class MoodData(
    val averageMood: Float,
    val change: Float,
    val noteWithMood: Int,
    val bestDays: Set<DayOfWeek>,
    val moods: List<Mood>,
    val moodDays: List<MoodDay>,
) {
    enum class MoodLevel {
        TERRIBLE,
        BAD,
        SAD,
        NORMAL,
        GOOD,
        PERFECT,
    }

    data class Mood(
        val level: MoodLevel,
        val percent: Float,
    )

    data class MoodDay(
        val date: ZonedDateTime,
        val averageMood: Float,
    )
}
