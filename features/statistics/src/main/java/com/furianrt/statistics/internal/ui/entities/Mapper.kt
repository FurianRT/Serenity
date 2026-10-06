package com.furianrt.statistics.internal.ui.entities

import com.furianrt.statistics.internal.domain.entities.StreakData

internal fun StreakData.StreakDay.toUI() = StreakDay(
    hasNotes = hasNotes,
    date = date,
)