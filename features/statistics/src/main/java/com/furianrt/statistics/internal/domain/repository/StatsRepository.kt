package com.furianrt.statistics.internal.domain.repository

import com.furianrt.statistics.internal.domain.entities.TimePeriod
import kotlinx.coroutines.flow.Flow

internal interface StatsRepository {
    fun getSelectedTimePeriod(): Flow<TimePeriod>
    suspend fun setSelectedTimePeriod(period: TimePeriod)
}