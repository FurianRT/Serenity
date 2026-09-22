package com.furianrt.billing.internal.domain.repository

import com.furianrt.billing.internal.domain.entities.SerenityPlusPlan
import com.furianrt.domain.managers.SerenityPlusProvider
import kotlinx.coroutines.flow.Flow

internal interface BillingRepository : SerenityPlusProvider {
    suspend fun sync()
    fun getSerenityPlusPlans(): Flow<List<SerenityPlusPlan>>
    suspend fun launchBillingFlow(productId: String): Result<Unit>
    suspend fun acknowledgePurchases()
}