package com.furianrt.statistics.internal.domain.usecase

import com.furianrt.statistics.internal.domain.entities.TimePeriod
import com.furianrt.statistics.internal.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetSelectedTimePeriodUseCase @Inject constructor(
    private val statsRepository: StatsRepository,
) {
    operator fun invoke(): Flow<TimePeriod> = statsRepository.getSelectedTimePeriod()
}