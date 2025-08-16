package com.furianrt.security.internal.domain

import android.app.Activity
import android.app.Application
import android.content.Context
import com.furianrt.common.ActivityLifecycleCallbacks
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.managers.LockAuthorizer
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
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LockManager @Inject constructor(
    @ApplicationContext context: Context,
    dispatchers: DispatchersProvider,
    private val securityRepository: SecurityRepository,
) : LockAuthorizer {
    private val scope = CoroutineScope(dispatchers.mainImmediate)
    private val isAuthorizedFlow = MutableStateFlow(false)
    private var lockJob: Job? = null
    private val skipLock = AtomicBoolean(false)

    private val lifecycleCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityStarted(activity: Activity) {
            lockJob?.cancel()
        }

        override fun onActivityStopped(activity: Activity) {
            lockJob = scope.launch {
                val pinRequestDelay = securityRepository.getPinRequestDelay().first().toLong()
                delay(pinRequestDelay)
                if (skipLock.get()) {
                    skipLock.set(false)
                } else {
                    isAuthorizedFlow.update { false }
                }
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

    override fun skipNextLock() {
        skipLock.set(true)
    }

    override fun cancelSkipNextLock() {
        skipLock.set(false)
    }
}