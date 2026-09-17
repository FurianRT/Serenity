package com.furianrt.billing.internal.ui

internal data object BillingState

internal sealed interface BillingEvent {
    data object OnButtonCloseClick : BillingEvent
}

internal sealed interface BillingEffect {
    data object CloseScreen : BillingEffect
}