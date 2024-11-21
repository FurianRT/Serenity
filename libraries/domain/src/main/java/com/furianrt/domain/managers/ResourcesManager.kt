package com.furianrt.domain.managers

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourcesManager @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) {

    private val activityCallbacks = CurrentActivityCallbacks()

    init {
        (applicationContext as Application).registerActivityLifecycleCallbacks(activityCallbacks)
    }

    private val resources: Resources
        get() = (activityCallbacks.currentActivityContext ?: applicationContext).resources


    fun getString(@StringRes id: Int): String = resources.getString(id)

    fun getString(
        @StringRes resourceId: Int,
        vararg objects: Any,
    ): String = resources.getString(resourceId, *objects)

    private class CurrentActivityCallbacks : Application.ActivityLifecycleCallbacks {

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
        }
    }
}