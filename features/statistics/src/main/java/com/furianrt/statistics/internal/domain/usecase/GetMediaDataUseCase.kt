package com.furianrt.statistics.internal.domain.usecase

import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.entities.NoteMedia
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.statistics.internal.domain.entities.MediaData
import com.furianrt.statistics.internal.domain.entities.TimePeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

internal class GetMediaDataUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val dispatchers: DispatchersProvider,
) {
    operator fun invoke(
        period: TimePeriod,
    ): Flow<MediaData> = mediaRepository.getNotesMedia()
        .map { notesMedia ->
            val currentDate = LocalDate.now()
            val prevPeriodMedia = if (period != TimePeriod.ALL_TIME) {
                notesMedia.filterByDate(
                    start = currentDate.minusDays(period.days * 2L),
                    end = currentDate.minusDays(period.days)
                )
            } else {
                emptyList()
            }

            val currentPeriodMedia = if (period != TimePeriod.ALL_TIME) {
                notesMedia.filterByDate(
                    start = currentDate.minusDays(period.days),
                    end = null,
                )
            } else {
                notesMedia
            }

            MediaData(
                totalMedia = currentPeriodMedia.size,
                change = when {
                    period == TimePeriod.ALL_TIME -> 0f
                    currentPeriodMedia.isNotEmpty() && prevPeriodMedia.isNotEmpty() -> {
                        ((1f - prevPeriodMedia.size.toFloat() / currentPeriodMedia.size) * 100f)
                            .coerceAtMost(999f)
                    }

                    else -> 0f
                },
            )
        }
        .distinctUntilChanged()
        .flowOn(dispatchers.default)
}

private fun List<NoteMedia>.filterByDate(
    start: LocalDate?,
    end: LocalDate?,
): List<NoteMedia> = when {
    start != null && end != null -> filter { it.noteDate.toLocalDate() in start..end }
    start != null -> filter { it.noteDate.toLocalDate() >= start }
    end != null -> filter { it.noteDate.toLocalDate() <= end }
    else -> this
}
