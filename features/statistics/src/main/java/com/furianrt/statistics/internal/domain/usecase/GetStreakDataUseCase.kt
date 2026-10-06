package com.furianrt.statistics.internal.domain.usecase

import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.statistics.internal.domain.entities.StreakData
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GetStreakDataUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
    private val dispatchers: DispatchersProvider,
) {
    operator fun invoke(): Flow<StreakData> = notesRepository.getAllSimpleNotes()
        .map { notes ->
            if (notes.isEmpty()) {
                return@map StreakData(
                    currentStreak = 0,
                    longestStreak = 0,
                    streakDays = getLast7Days(emptySet())
                )
            }

            val noteDatesSet = notes.map { it.date.toLocalDate() }.toSet()
            val sortedUniqueDates = noteDatesSet.sorted()

            var maxStreak = 1
            var runningStreak = 1

            for (i in 1 until sortedUniqueDates.size) {
                val previousDate = sortedUniqueDates[i - 1]
                val currentDate = sortedUniqueDates[i]

                if (ChronoUnit.DAYS.between(previousDate, currentDate) == 1L) {
                    runningStreak++
                } else {
                    maxStreak = maxOf(maxStreak, runningStreak)
                    runningStreak = 1
                }
            }
            val finalLongestStreak = maxOf(maxStreak, runningStreak)

            val today = LocalDate.now()
            var calculatedCurrentStreak = 0
            var checkDate = today

            if (!noteDatesSet.contains(checkDate)) {
                checkDate = today.minusDays(1)
            }

            while (noteDatesSet.contains(checkDate)) {
                calculatedCurrentStreak++
                checkDate = checkDate.minusDays(1)
            }

            val streakDaysList = getLast7Days(noteDatesSet)

            StreakData(
                currentStreak = calculatedCurrentStreak,
                longestStreak = maxOf(finalLongestStreak, calculatedCurrentStreak),
                streakDays = streakDaysList,
            )
        }
        .distinctUntilChanged()
        .flowOn(dispatchers.default)

    private fun getLast7Days(noteDatesSet: Set<LocalDate>): List<StreakData.StreakDay> {
        val today = LocalDate.now()
        val firstDay = today.minusDays(6)

        return (0..6).map {
            val targetDate = firstDay.plusDays(it.toLong())
            StreakData.StreakDay(
                date = targetDate,
                hasNotes = noteDatesSet.contains(targetDate)
            )
        }
    }
}
