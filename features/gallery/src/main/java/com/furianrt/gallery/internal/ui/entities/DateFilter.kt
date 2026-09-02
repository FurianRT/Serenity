package com.furianrt.gallery.internal.ui.entities

import java.time.LocalDate

internal data class DateFilter(
    val start: LocalDate,
    val end: LocalDate?,
)