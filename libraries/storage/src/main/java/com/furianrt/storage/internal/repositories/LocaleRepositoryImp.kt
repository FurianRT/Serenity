package com.furianrt.storage.internal.repositories

import android.app.Activity
import android.app.Application
import android.app.LocaleManager
import android.content.Context
import android.os.Bundle
import android.os.LocaleList
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
    @ApplicationContext private val applicationContext: Context,
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
            AppLocale.INDONESIAN,
            AppLocale.SPANISH,
            AppLocale.PORTUGUESE,
            AppLocale.GERMAN,
            AppLocale.TURKISH,
        )
    )

    override fun getSelectedLocale(): Flow<AppLocale> = localeFlow

    private fun getDefaultLocale(): AppLocale {
        return AppLocale.fromTag(Locale.getDefault().language)
    }

    private inner class CurrentActivityCallbacks : Application.ActivityLifecycleCallbacks {

        private var currentActivity: WeakReference<Activity>? = null

        val currentActivityContext: Context?
            get() = currentActivity?.get()?.takeUnless { it.isDestroyed }


        override fun onActivityStarted(activity: Activity) = Unit
        override fun onActivityResumed(activity: Activity) = Unit
        override fun onActivityPaused(activity: Activity) = Unit
        override fun onActivityStopped(activity: Activity) = Unit
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
        override fun onActivityDestroyed(activity: Activity) = Unit
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            currentActivity = WeakReference(activity)
            localeFlow.update { getDefaultLocale() }
        }
    }
}
