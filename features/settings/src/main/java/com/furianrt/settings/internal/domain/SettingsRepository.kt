package com.furianrt.settings.internal.domain

import kotlinx.coroutines.flow.Flow

internal interface SettingsRepository {
    fun getAppRating(): Flow<Int>
    suspend fun setAppRating(rating: Int)
}