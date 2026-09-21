package com.furianrt.domain.managers

import kotlinx.coroutines.flow.StateFlow

interface SerenityPlusProvider {
    fun enqueuePeriodicWork()
    fun hasSerenityPlus(): StateFlow<Boolean>
}