package com.furianrt.domain.managers

import kotlinx.coroutines.flow.Flow

interface SerenityPlusProvider {
    fun enqueueBillingUpdateWork()
    fun hasSerenityPlus(): Flow<Boolean>
}