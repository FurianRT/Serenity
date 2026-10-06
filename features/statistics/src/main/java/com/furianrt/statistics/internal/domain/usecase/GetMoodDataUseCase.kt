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

private data class MoodWithDate(
    val date: LocalDate,
    val level: MoodData.MoodLevel,
)

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

            val currentMoodDays = currentPeriodNotes.mapNotNull { note ->
                MoodWithDate(
                    date = note.date.toLocalDate(),
                    level = when (MoodHolder.findMood(note.moodId)?.level) {
                        Mood.Level.TERRIBLE -> MoodData.MoodLevel.TERRIBLE
                        Mood.Level.BAD -> MoodData.MoodLevel.BAD
                        Mood.Level.SAD -> MoodData.MoodLevel.SAD
                        Mood.Level.NORMAL -> MoodData.MoodLevel.NORMAL
                        Mood.Level.GOOD -> MoodData.MoodLevel.GOOD
                        Mood.Level.PERFECT -> MoodData.MoodLevel.PERFECT
                        null -> return@mapNotNull null
                    }
                )
            }

            val currentAverageMood = currentMoodDays
                .map { it.level.toValue() }
                .average()
                .toFloat()
                .normalizeMood()

            val averageMoods = currentMoodDays
                .groupBy { it.date }
                .mapValues { it.value.map { mood -> mood.level.toValue() }.average() }

            val bestMood = averageMoods.maxOfOrNull { it.value }

            val moodPercents = currentMoodDays
                .groupingBy { it.level }
                .eachCount()
                .map { (level, count) ->
                    MoodData.Mood(
                        level = level,
                        percent = count.toFloat() / currentMoodDays.size,
                    )
                }

            MoodData(
                averageMood = currentAverageMood,
                change = when {
                    period == TimePeriod.ALL_TIME -> 0f
                    currentPeriodNotes.isNotEmpty() && prevPeriodNotes.isNotEmpty() -> {
                        currentAverageMood - prevAverageMood
                    }

                    else -> 0f
                },
                noteWithMood = currentMoodDays.size,
                moods = buildList {
                    addAll(moodPercents)
                    MoodData.MoodLevel.entries.forEach { entry ->
                        if (moodPercents.none { it.level == entry }) {
                            add(
                                MoodData.Mood(
                                    level = entry,
                                    percent = 0f,
                                )
                            )
                        }
                    }
                }.sortedByDescending { it.level.toValue() },
                bestDays = averageMoods
                    .filter { it.value == bestMood }
                    .mapKeys { it.key.dayOfWeek }
                    .keys,
                moodDays = averageMoods.map { mood ->
                    MoodData.MoodDay(
                        date = mood.key,
                        averageMood = mood.value.toFloat(),
                    )
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

private fun MoodData.MoodLevel.toValue(): Int = when (this) {
    MoodData.MoodLevel.TERRIBLE -> 1
    MoodData.MoodLevel.BAD -> 2
    MoodData.MoodLevel.SAD -> 3
    MoodData.MoodLevel.NORMAL -> 4
    MoodData.MoodLevel.GOOD -> 5
    MoodData.MoodLevel.PERFECT -> 6
}
