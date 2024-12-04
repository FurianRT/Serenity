package com.furianrt.search.internal.ui.entities

import androidx.compose.runtime.Immutable
import java.time.LocalDate

internal sealed class SelectedFilter(
    val id: String,
) {
    internal data class Tag(
        val title: String,
    ) : SelectedFilter(title)

    @Immutable
    internal data class DateRange(
        val start: LocalDate,
        val end: LocalDate?,
    ) : SelectedFilter(ID) {

        companion object {
            const val ID = "date_range"
        }
    }
}