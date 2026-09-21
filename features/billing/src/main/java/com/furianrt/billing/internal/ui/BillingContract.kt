package com.furianrt.billing.internal.ui

import androidx.compose.runtime.Immutable
import com.furianrt.billing.internal.ui.entities.BenefitItem
import com.furianrt.billing.internal.ui.entities.SubscriptionPlan
import com.furianrt.uikit.entities.UiThemeColor

@Immutable
internal data class BillingState(
    val benefits: List<BenefitItem>,
    val showButtonProgress: Boolean,
    val theme: UiThemeColor,
    val content: Content,
) {
    sealed interface Content {
        data object Loading : Content

        @Immutable
        data class Success(
            val plans: List<SubscriptionPlan>,
            val selectedPlanId: String,
            val buttonTitle: String,
        ) : Content
    }
}

internal sealed interface BillingEvent {
    data object OnButtonCloseClick : BillingEvent
    data object OnSubscribeClick : BillingEvent
    data object OnPrivacyPolicyClick : BillingEvent
    data object OnTermsClick : BillingEvent
    data class OnPlanSelected(val id: String) : BillingEvent
}

internal sealed interface BillingEffect {
    data object CloseScreen : BillingEffect
    data class OpenLink(val url: String) : BillingEffect
}