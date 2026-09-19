package com.furianrt.billing.internal.ui

import com.furianrt.billing.internal.ui.entities.SubscriptionPlan
import com.furianrt.uikit.entities.UiThemeColor

internal data class BillingState(
    val theme: UiThemeColor,
    val plans: List<SubscriptionPlan>,
)

internal sealed interface BillingEvent {
    data object OnButtonCloseClick : BillingEvent
}

internal sealed interface BillingEffect {
    data object CloseScreen : BillingEffect
}