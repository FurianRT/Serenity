package com.furianrt.statistics.internal.domain.usecase

import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.statistics.internal.domain.entities.NotesData
import com.furianrt.statistics.internal.domain.entities.TimePeriod
import com.furianrt.statistics.internal.domain.utils.filterByDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

internal class GetNotesDataUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
    private val dispatchers: DispatchersProvider,
) {
    operator fun invoke(
        period: TimePeriod,
    ): Flow<NotesData> = notesRepository.getAllSimpleNotes()
        .map { notes ->
            val currentDate = LocalDate.now()
            val prevPeriodNotes = if (period != TimePeriod.ALL_TIME) {
                notes.filterByDate(
                    start = currentDate.minusDays(period.days * 2L),
                    end = currentDate.minusDays(period.days)
                )
            } else {
                emptyList()
            }

            val currentPeriodNotes = if (period != TimePeriod.ALL_TIME) {
                notes.filterByDate(
                    start = currentDate.minusDays(period.days),
                    end = null,
                )
            } else {
                notes
            }

            NotesData(
                totalNotes = currentPeriodNotes.size,
                change = when {
                    period == TimePeriod.ALL_TIME -> 0f
                    currentPeriodNotes.isNotEmpty() && prevPeriodNotes.isNotEmpty() -> {
                        ((1f - prevPeriodNotes.size.toFloat() / currentPeriodNotes.size) * 100f)
                            .coerceAtMost(999f)
                    }

                    else -> 0f
                },
            )
        }
        .distinctUntilChanged()
        .flowOn(dispatchers.default)
}
