package com.furianrt.storage.internal.repositories

import android.app.Activity
import android.app.Application
import android.app.LocaleManager
import android.content.Context
import android.os.Bundle
import android.os.LocaleList
import com.furianrt.common.ActivityLifecycleCallbacks
import com.furianrt.domain.entities.AppLocale
import com.furianrt.domain.repositories.LocaleRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import java.lang.ref.WeakReference
import java.util.Locale
import javax.inject.Inject

internal class LocaleRepositoryImp @Inject constructor(
    @param:ApplicationContext private val applicationContext: Context,
) : LocaleRepository {
    private val activityCallbacks = CurrentActivityCallbacks()

    private val localeFlow by lazy { MutableStateFlow(getDefaultLocale()) }

    init {
        (applicationContext as Application).registerActivityLifecycleCallbacks(activityCallbacks)
    }

    private val activityContext: Context
        get() = (activityCallbacks.currentActivityContext ?: applicationContext)

    override fun setSelectedLocale(locale: AppLocale) {
        activityContext.getSystemService(LocaleManager::class.java)
            ?.applicationLocales = LocaleList.forLanguageTags(locale.tag)
    }

    override fun getLocaleList(): Flow<List<AppLocale>> = flowOf(
        listOf(
            AppLocale.ENGLISH,
            AppLocale.RUSSIAN,
            AppLocale.HINDI,
            AppLocale.BENGALI,
            AppLocale.INDONESIAN,
            AppLocale.VIETNAMESE,
            AppLocale.THAI,
            AppLocale.SPANISH,
            AppLocale.PORTUGUESE,
            AppLocale.GERMAN,
            AppLocale.FRENCH,
            AppLocale.ITALIAN,
            AppLocale.TURKISH,
            AppLocale.UKRAINIAN,
            AppLocale.POLISH,
            AppLocale.FILIPINO,
            AppLocale.JAPANESE,
            AppLocale.KOREAN,
            AppLocale.NEDERLANDS,
            AppLocale.ROMANIAN,
        )
    )

    override fun getSelectedLocale(): Flow<AppLocale> = localeFlow

    private fun getDefaultLocale(): AppLocale {
        return AppLocale.fromTag(Locale.getDefault().language)
    }

    private inner class CurrentActivityCallbacks : ActivityLifecycleCallbacks {

        private var currentActivity: WeakReference<Activity>? = null

        val currentActivityContext: Context?
            get() = currentActivity?.get()?.takeUnless { it.isDestroyed }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            currentActivity = WeakReference(activity)
            localeFlow.update { getDefaultLocale() }
        }
    }
}
