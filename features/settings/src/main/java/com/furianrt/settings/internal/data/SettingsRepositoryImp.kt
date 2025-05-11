package com.furianrt.settings.internal.data

import com.furianrt.settings.internal.domain.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class SettingsRepositoryImp @Inject constructor(
    private val dataStore: SettingsDataStore,
) : SettingsRepository {

    override fun getAppRating(): Flow<Int> = dataStore.getAppRating()

    override suspend fun setAppRating(rating: Int) {
        dataStore.setAppRating(rating)
    }
}