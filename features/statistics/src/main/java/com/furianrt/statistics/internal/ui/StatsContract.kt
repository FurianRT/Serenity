package com.furianrt.statistics.internal.ui

import androidx.compose.runtime.Immutable
import com.furianrt.statistics.internal.domain.entities.TimePeriod
import com.furianrt.statistics.internal.ui.entities.GeneralStats
import com.furianrt.statistics.internal.ui.entities.StreakDay
import com.furianrt.statistics.internal.ui.entities.StreakStats
import com.furianrt.uikit.entities.UiThemeColor
import java.time.LocalDate
import java.time.ZonedDateTime

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
}

internal sealed interface StatsEvent {
    data object OnButtonBackClick : StatsEvent
    data object OnNotesStatClick : StatsEvent
    data object OnGalleryStatClick : StatsEvent
    data object OnMoodStatClick : StatsEvent
    data class OnPeriodSelected(val period: TimePeriod) : StatsEvent
    data class OnStreakDayClick(val day: StreakDay) : StatsEvent
}

internal sealed interface StatsEffect {
    data object CloseScreen : StatsEffect
    data class OpenNoteCreateScreen(val date: ZonedDateTime) : StatsEffect
    data class OpenGalleryRequest(val startDate: LocalDate?) : StatsEffect
    data class OpenNoteSearchRequest(
        val startDate: LocalDate?,
        val endDate: LocalDate?,
    ) : StatsEffect
}
