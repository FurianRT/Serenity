package com.furianrt.search.internal.ui.entities

import androidx.compose.runtime.Immutable
import java.time.LocalDate

internal sealed class SelectedFilter(
    val id: String,
    open val isSelected: Boolean,
) {
    internal data class Tag(
        val title: String,
        override val isSelected: Boolean = true,
    ) : SelectedFilter(title, isSelected)

    @Immutable
    internal data class DateRange(
        val start: LocalDate,
        val end: LocalDate?,
    ) : SelectedFilter(ID, isSelected = true) {

        companion object {
            const val ID = "date_range"
        }
    }
}