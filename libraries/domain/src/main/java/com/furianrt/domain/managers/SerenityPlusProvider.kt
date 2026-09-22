package com.furianrt.domain.managers

import kotlinx.coroutines.flow.StateFlow

interface SerenityPlusProvider {
    fun enqueueBillingUpdateWork()
    fun hasSerenityPlus(): StateFlow<Boolean>
}