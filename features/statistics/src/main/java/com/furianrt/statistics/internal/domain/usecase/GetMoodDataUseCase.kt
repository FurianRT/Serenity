package com.furianrt.statistics.internal.domain.usecase

import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.mood.api.MoodHolder
import com.furianrt.mood.api.entities.Mood
import com.furianrt.statistics.internal.domain.entities.MoodData
import com.furianrt.statistics.internal.domain.entities.TimePeriod
import com.furianrt.statistics.internal.domain.utils.filterByDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

internal class GetMoodDataUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
    private val dispatchers: DispatchersProvider,
) {
    operator fun invoke(
        period: TimePeriod,
    ): Flow<MoodData> = notesRepository.getSimpleNotesWithMood()
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

            val prevAverageMood = prevPeriodNotes
                .mapNotNull { MoodHolder.findMood(it.moodId)?.level?.toValue() }
                .average()
                .toFloat()
                .normalizeMood()

            val currentAverageMood = currentPeriodNotes
                .mapNotNull { MoodHolder.findMood(it.moodId)?.level?.toValue() }
                .average()
                .toFloat()
                .normalizeMood()

            MoodData(
                averageMood = currentAverageMood,
                change = when {
                    period == TimePeriod.ALL_TIME -> 0f
                    currentPeriodNotes.isNotEmpty() && prevPeriodNotes.isNotEmpty() -> {
                        currentAverageMood - prevAverageMood
                    }

                    else -> 0f
                },
            )
        }
        .distinctUntilChanged()
        .flowOn(dispatchers.default)
}

private fun Float.normalizeMood(): Float = 1 + (this - 1) * 4 / 5f

private fun Mood.Level.toValue(): Int = when (this) {
    Mood.Level.TERRIBLE -> 1
    Mood.Level.BAD -> 2
    Mood.Level.SAD -> 3
    Mood.Level.NORMAL -> 4
    Mood.Level.GOOD -> 5
    Mood.Level.PERFECT -> 6
}
