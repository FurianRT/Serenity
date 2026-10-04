package com.furianrt.statistics.internal.ui

import androidx.compose.runtime.Immutable
import com.furianrt.statistics.internal.domain.entities.TimePeriod
import com.furianrt.uikit.entities.UiThemeColor

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
}

internal sealed interface StatsEvent {
    data object OnButtonBackClick : StatsEvent
    data class OnPeriodSelected(val period: TimePeriod) : StatsEvent
}

internal sealed interface StatsEffect {
    data object CloseScreen : StatsEffect
}
