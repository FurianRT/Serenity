package com.furianrt.billing.internal.data.repository

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.furianrt.billing.internal.data.SerenityProductId
import com.furianrt.billing.internal.data.mappers.toDomain
import com.furianrt.billing.internal.data.mappers.toEntryBillingPlans
import com.furianrt.billing.internal.data.sources.BillingDataStore
import com.furianrt.billing.internal.domain.entities.SerenityPlusPlan
import com.furianrt.billing.internal.domain.repository.BillingRepository
import com.furianrt.billing.internal.workers.AcknowledgePurchaseWorker
import com.furianrt.common.ActivityChecker
import com.furianrt.common.ActivityLifecycleCallbacks
import com.furianrt.common.ErrorTracker
import com.furianrt.core.deepMap
import com.furianrt.storage.internal.database.billing.dao.BillingPlanDao
import com.furianrt.storage.internal.database.billing.entities.EntryBillingPlan
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private class BillingException(
    code: Int,
    message: String,
) : Exception("Code: $code message: $message")

@Singleton
internal class BillingRepositoryImp @Inject constructor(
    @param:ApplicationContext private val applicationContext: Context,
    private val billingDataStore: BillingDataStore,
    private val billingPlanDao: BillingPlanDao,
    private val errorTracker: ErrorTracker,
    private val activityChecker: ActivityChecker,
) : BillingRepository,
    PurchasesUpdatedListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val mutex = Mutex()

    private val billingClient = BillingClient.newBuilder(applicationContext)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    private val activityCallbacks = CurrentActivityCallbacks()

    init {
        (applicationContext as Application).registerActivityLifecycleCallbacks(activityCallbacks)
    }

    override suspend fun sync() {
        val connectionResult = startConnection()
        if (connectionResult.isFailure) {
            return
        }

        acknowledgePurchases()

        if (billingDataStore.isSerenityPlusAvailable().first()) {
            return
        }

        val deferredSubs = scope.async {
            queryPlansForType(
                productId = SerenityProductId.SUBSCRIPTIONS_ID,
                productType = BillingClient.ProductType.SUBS,
            )
        }
        val deferredInApps = scope.async {
            queryPlansForType(
                productId = SerenityProductId.PERMANENT_ID,
                productType = BillingClient.ProductType.INAPP,
            )
        }
        try {
            val subsList = deferredSubs.await()
            val inAppsList = deferredInApps.await()
            val allPlans = subsList + inAppsList
            billingPlanDao.clearAndInsert(allPlans.toEntryBillingPlans())
        } catch (e: Exception) {
            errorTracker.trackNonFatalError(e)
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase?>?,
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> if (!purchases.isNullOrEmpty()) {
                scope.launch {
                    val connectionResult = startConnection()
                    if (connectionResult.isFailure) {
                        return@launch
                    }
                    for (purchase in purchases) {
                        if (purchase?.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            billingDataStore.setSerenityPlusAvailable(available = true)
                            if (!purchase.isAcknowledged) {
                                acknowledgePurchase(purchase)
                            }
                        }
                    }
                }
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> {
                // Do Nothing
            }

            else -> errorTracker.trackNonFatalError(
                BillingException(
                    code = billingResult.responseCode,
                    message = billingResult.debugMessage,
                )
            )
        }
    }

    override suspend fun acknowledgePurchases() {
        val connectionResult = startConnection()
        if (connectionResult.isFailure) {
            return
        }
        val deferredSubs = scope.async { queryPurchasesForType(BillingClient.ProductType.SUBS) }
        val deferredInApps = scope.async { queryPurchasesForType(BillingClient.ProductType.INAPP) }
        val subsList = deferredSubs.await()
        val inAppsList = deferredInApps.await()

        val allPurchases = subsList + inAppsList

        val hasActivePremium = allPurchases.any { purchase ->
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
        }
        billingDataStore.setSerenityPlusAvailable(hasActivePremium)

        allPurchases
            .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged }
            .map { purchase -> scope.async { acknowledgePurchase(purchase) } }
            .awaitAll()
    }

    override fun getSerenityPlusPlans(): Flow<List<SerenityPlusPlan>> =
        billingPlanDao.getBillingPlans()
            .deepMap(EntryBillingPlan::toDomain)
            .map { plans ->
                plans.sortedBy { plan ->
                    when (plan) {
                        is SerenityPlusPlan.Monthly -> 1
                        is SerenityPlusPlan.Yearly -> 2
                        is SerenityPlusPlan.Permanent -> 3
                    }
                }
            }

    override fun hasSerenityPlus(): StateFlow<Boolean> = billingDataStore.isSerenityPlusAvailable()
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    override fun enqueueBillingUpdateWork() {
        AcknowledgePurchaseWorker.enqueuePeriodic(applicationContext)
        AcknowledgePurchaseWorker.enqueueOneTime(applicationContext)
    }

    private suspend fun getSubscriptionBillingParams(
        productId: String,
    ): Result<BillingFlowParams> {
        val plan = try {
            queryPlansForType(
                productId = SerenityProductId.SUBSCRIPTIONS_ID,
                productType = BillingClient.ProductType.SUBS,
            ).firstOrNull() ?: throw IllegalStateException()
        } catch (e: Exception) {
            return Result.failure(e)
        }

        val offer = plan.subscriptionOfferDetails?.findTrialOffer(productId)
            ?: plan.subscriptionOfferDetails?.findOffer(productId)
            ?: return Result.failure(IllegalStateException())

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(plan)
                .setOfferToken(offer.offerToken)
                .build()
        )

        return Result.success(
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
        )
    }

    private suspend fun getPermanentBillingParams(
        productId: String,
    ): Result<BillingFlowParams> {
        val plan = try {
            queryPlansForType(
                productId = SerenityProductId.PERMANENT_ID,
                productType = BillingClient.ProductType.INAPP,
            ).find { it.productId == productId } ?: throw IllegalStateException()
        } catch (e: Exception) {
            return Result.failure(e)
        }
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(plan)
                .build()
        )
        return Result.success(
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
        )
    }

    override suspend fun launchBillingFlow(productId: String): Result<Unit> {
        val activity = activityCallbacks.currentActivity
            ?: return Result.failure(IllegalStateException())

        val connectionResult = startConnection()
        if (connectionResult.isFailure) {
            return connectionResult
        }

        val billingFlowParams = if (productId == SerenityProductId.PERMANENT_ID) {
            getPermanentBillingParams(productId)
        } else {
            getSubscriptionBillingParams(productId)
        }.onFailure { error ->
            return Result.failure(error)
        }.getOrNull() ?: return Result.failure(IllegalStateException())

        val result = billingClient.launchBillingFlow(activity, billingFlowParams)

        return if (result.responseCode == BillingClient.BillingResponseCode.OK) {
            Result.success(Unit)
        } else {
            Result.failure(
                BillingException(
                    code = result.responseCode,
                    message = result.debugMessage,
                ),
            )
        }
    }

    private fun List<ProductDetails.SubscriptionOfferDetails>.findOffer(
        productId: String,
    ): ProductDetails.SubscriptionOfferDetails? = find { details ->
        details.basePlanId == productId
    }

    private fun List<ProductDetails.SubscriptionOfferDetails>.findTrialOffer(
        productId: String,
    ): ProductDetails.SubscriptionOfferDetails? = find { details ->
        details.basePlanId == productId && details.offerTags.contains(SerenityProductId.TAG_TRIAL)
    }

    private suspend fun queryPlansForType(
        productId: String,
        productType: String,
    ): List<ProductDetails> {
        val products = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(productType)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(products)
            .build()

        return suspendCancellableCoroutine { continuation ->
            billingClient.queryProductDetailsAsync(params) { billingResult, result ->
                if (!continuation.isActive) return@queryProductDetailsAsync

                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    continuation.resume(result.productDetailsList)
                } else {
                    continuation.resumeWithException(
                        BillingException(
                            code = billingResult.responseCode,
                            message = billingResult.debugMessage,
                        )
                    )
                }
            }
        }
    }

    private suspend fun queryPurchasesForType(productType: String): List<Purchase> {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(productType)
            .build()

        return suspendCancellableCoroutine { continuation ->
            billingClient.queryPurchasesAsync(params) { billingResult, purchaseList ->
                if (!continuation.isActive) return@queryPurchasesAsync

                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    continuation.resume(purchaseList)
                } else {
                    continuation.resumeWithException(
                        BillingException(
                            code = billingResult.responseCode,
                            message = billingResult.debugMessage,
                        )
                    )
                }
            }
        }
    }

    private suspend fun acknowledgePurchase(
        purchase: Purchase,
    ): Unit = suspendCancellableCoroutine { continuation ->
        val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(acknowledgeParams) { billingResult ->
            if (!continuation.isActive) return@acknowledgePurchase

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                continuation.resume(Unit)
            } else {
                val exception = BillingException(
                    code = billingResult.responseCode,
                    message = billingResult.debugMessage,
                )
                errorTracker.trackNonFatalError(exception)

                continuation.resume(Unit)
            }
        }
    }

    private suspend fun startConnection(): Result<Unit> = mutex.withLock {
        if (billingClient.isReady) {
            return Result.success(Unit)
        }

        return suspendCancellableCoroutine { continuation ->
            var isListenerActive = true

            val listener = object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (!continuation.isActive || !isListenerActive) return

                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        continuation.resume(Result.success(Unit))
                    } else {
                        continuation.resume(
                            Result.failure(
                                BillingException(
                                    code = billingResult.responseCode,
                                    message = billingResult.debugMessage,
                                )
                            )
                        )
                    }
                }

                override fun onBillingServiceDisconnected() {
                    if (continuation.isActive && isListenerActive) {
                        continuation.resume(
                            Result.failure(
                                BillingException(
                                    code = BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
                                    message = "Billing service disconnected during setup",
                                )
                            )
                        )
                    }
                }
            }
            continuation.invokeOnCancellation {
                isListenerActive = false
                if (billingClient.connectionState == BillingClient.ConnectionState.CONNECTING) {
                    try {
                        billingClient.endConnection()
                    } catch (e: Exception) {
                        errorTracker.trackNonFatalError(e)
                    }
                }
            }

            try {
                billingClient.startConnection(listener)
            } catch (e: Exception) {
                if (continuation.isActive) {
                    continuation.resume(Result.failure(e))
                }
            }
        }
    }

    private inner class CurrentActivityCallbacks : ActivityLifecycleCallbacks {

        private var currentActivityRef: WeakReference<Activity>? = null

        val currentActivity: Activity?
            get() = currentActivityRef?.get()?.takeUnless { it.isDestroyed }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (activityChecker.isMainActivity(activity)) {
                currentActivityRef = WeakReference(activity)
            }
        }
    }
}