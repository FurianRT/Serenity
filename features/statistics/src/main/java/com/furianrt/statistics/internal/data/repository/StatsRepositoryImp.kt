package com.furianrt.statistics.internal.data.repository

import com.furianrt.statistics.internal.data.sources.StatsDataStore
import com.furianrt.statistics.internal.domain.entities.TimePeriod
import com.furianrt.statistics.internal.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class StatsRepositoryImp @Inject constructor(
    private val dataStore: StatsDataStore,
) : StatsRepository {
    override fun getSelectedTimePeriod(): Flow<TimePeriod> = dataStore.getSelectedTimePeriod()
        .map(TimePeriod::fromDays)

    override suspend fun setSelectedTimePeriod(period: TimePeriod) {
        dataStore.setSelectedTimePeriod(period.days)
    }
}