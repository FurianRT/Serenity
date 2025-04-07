package com.furianrt.security.internal.domain

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.furianrt.core.DispatchersProvider
import com.furianrt.security.api.LockAuthorizer
import com.furianrt.security.internal.domain.repositories.SecurityRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LockManager @Inject constructor(
    @ApplicationContext context: Context,
    dispatchers: DispatchersProvider,
    private val securityRepository: SecurityRepository,
) : LockAuthorizer {
    private val scope = CoroutineScope(dispatchers.main)
    private val isAuthorizedFlow = MutableStateFlow(false)
    private var lockJob: Job? = null

    private val lifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
        override fun onActivityResumed(activity: Activity) = Unit
        override fun onActivityPaused(activity: Activity) = Unit
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
        override fun onActivityDestroyed(activity: Activity) = Unit
        override fun onActivityStarted(activity: Activity) {
            lockJob?.cancel()
        }

        override fun onActivityStopped(activity: Activity) {
            lockJob = scope.launch {
                val pinRequestDelay = securityRepository.getPinRequestDelay().first().toLong()
                delay(pinRequestDelay)
                isAuthorizedFlow.update { false }
            }
        }
    }

    init {
        (context as Application).registerActivityLifecycleCallbacks(lifecycleCallbacks)
    }

    override fun isAuthorized(): Flow<Boolean> = combine(
        isAuthorizedFlow,
        securityRepository.getPin(),
    ) { isAuthorized, pin ->
        pin == null || isAuthorized
    }

    override fun authorize() {
        isAuthorizedFlow.update { true }
    }
}