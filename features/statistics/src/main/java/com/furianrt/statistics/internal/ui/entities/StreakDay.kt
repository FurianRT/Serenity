package com.furianrt.statistics.internal.ui.entities

import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
internal data class StreakDay(
    val hasNotes: Boolean,
    val date: LocalDate,
)