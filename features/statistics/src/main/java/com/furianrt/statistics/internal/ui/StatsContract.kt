package com.furianrt.statistics.internal.ui

import androidx.compose.runtime.Immutable
import com.furianrt.statistics.internal.domain.entities.TimePeriod
import com.furianrt.uikit.entities.UiThemeColor
import java.time.LocalDate

internal data class StatsState(
    val theme: UiThemeColor,
    val content: Content,
) {
    sealed interface Content {
        data object Loading : Content

        @Immutable
        data class Success(
            val periods: List<TimePeriod>,
            val selectedPeriod: TimePeriod,
            val generalStats: GeneralStats,
            val streakStats: StreakStats,
        ) : Content
    }

    data class GeneralStats(
        val notesCount: Int,
        val notesChange: Float,
        val mediaCount: Int,
        val mediaChange: Float,
        val averageMood: Float,
        val moodChange: Float,
    )

    data class StreakStats(
        val currentStreak: Int,
        val longestStreak: Int,
        val streakDays: List<StreakDay>,
    ) {
        data class StreakDay(
            val hasNotes: Boolean,
            val date: LocalDate,
        )
    }
}

internal sealed interface StatsEvent {
    data object OnButtonBackClick : StatsEvent
    data class OnPeriodSelected(val period: TimePeriod) : StatsEvent
}

internal sealed interface StatsEffect {
    data object CloseScreen : StatsEffect
}
