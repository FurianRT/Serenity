package com.furianrt.statistics.internal.domain.usecase

import com.furianrt.statistics.internal.domain.entities.TimePeriod
import com.furianrt.statistics.internal.domain.repository.StatsRepository
import javax.inject.Inject

internal class UpdateSelectedTimePeriodUseCase @Inject constructor(
    private val statsRepository: StatsRepository,
) {
    suspend operator fun invoke(period: TimePeriod) {
        statsRepository.setSelectedTimePeriod(period)
    }
}