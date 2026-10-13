package com.furianrt.billing.internal.domain.entities

internal sealed interface SyncResult {
    data object Ok : SyncResult
    data class BillingUnavailable(val error: Exception) : SyncResult
    data class Other(val error: Exception) : SyncResult
}

internal val SyncResult.isFailure: Boolean
    get() = this !is SyncResult.Ok
