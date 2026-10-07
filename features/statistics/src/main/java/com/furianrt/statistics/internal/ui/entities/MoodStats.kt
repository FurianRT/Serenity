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
        val percent: Float,
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
            get() = when (this) {
                TERRIBLE -> Color(0xFFC76B83)
                BAD -> Color(0xFFD58A72)
                SAD -> Color(0xFF7D9FC1)
                NORMAL -> Color(0xFFB39AC7)
                GOOD -> Color(0xFF5FAF7A)
                PERFECT -> Color(0xFFD6A85C)
            }
    }
}