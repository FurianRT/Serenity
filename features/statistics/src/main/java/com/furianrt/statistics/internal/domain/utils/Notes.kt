package com.furianrt.statistics.internal.domain.utils

import com.furianrt.domain.entities.SimpleNote
import java.time.LocalDate

internal fun List<SimpleNote>.filterByDate(
    start: LocalDate?,
    end: LocalDate?,
): List<SimpleNote> = when {
    start != null && end != null -> filter { it.date.toLocalDate() in start..end }
    start != null -> filter { it.date.toLocalDate() >= start }
    end != null -> filter { it.date.toLocalDate() <= end }
    else -> this
}
