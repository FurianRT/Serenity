package com.furianrt.statistics.internal.ui.entities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.furianrt.statistics.R

@Immutable
internal data class MoodStats(
    val notesCount: Int,
    val bestDaysOfWeek: List<String>,
    val pieChartData: List<Mood>,
) {
    data class Mood(
        val level: Level,
        val percent: Int,
    )

    enum class Level {
        TERRIBLE,
        BAD,
        SAD,
        NORMAL,
        GOOD,
        PERFECT;

        val icon: Painter
            @Composable get() = when (this) {
                TERRIBLE -> painterResource(R.drawable.ic_stat_mood_terrible)
                BAD -> painterResource(R.drawable.ic_stat_mood_bad)
                SAD -> painterResource(R.drawable.ic_stat_mood_sad)
                NORMAL -> painterResource(R.drawable.ic_stat_mood_normal)
                GOOD -> painterResource(R.drawable.ic_stat_mood_good)
                PERFECT -> painterResource(R.drawable.ic_stat_mood_perfect)
            }

        val color: Color
            @Composable get() = when (this) {
                TERRIBLE -> Color(0xFFE48F84)
                BAD -> Color(0xFFE4B284)
                SAD -> Color(0xFFAB93EC)
                NORMAL -> Color(0xFF9FD3EF)
                GOOD -> Color(0xFF83B3F5)
                PERFECT -> Color(0xFF81C2AB)
            }
    }
}