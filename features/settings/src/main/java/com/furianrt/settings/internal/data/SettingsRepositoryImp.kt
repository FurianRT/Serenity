package com.furianrt.settings.internal.data

import android.appwidget.AppWidgetManager
import com.furianrt.common.ErrorTracker
import com.furianrt.settings.internal.domain.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class SettingsRepositoryImp @Inject constructor(
    private val dataStore: SettingsDataStore,
    private val appWidgetManager: AppWidgetManager,
    private val errorTracker: ErrorTracker,
) : SettingsRepository {

    override fun getAppRating(): Flow<Int> = dataStore.getAppRating()

    override suspend fun setAppRating(rating: Int) {
        dataStore.setAppRating(rating)
    }

    override fun isPinAppWidgetSupported(): Boolean {
        val isRequestPinAppWidgetSupported = appWidgetManager.isRequestPinAppWidgetSupported
        if (!isRequestPinAppWidgetSupported) {
            errorTracker.trackNonFatalError(Exception("Pin App Widget unsupported"))
        }
        return isRequestPinAppWidgetSupported
    }
}