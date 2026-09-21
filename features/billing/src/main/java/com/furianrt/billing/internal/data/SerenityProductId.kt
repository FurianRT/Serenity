package com.furianrt.billing.internal.data

internal object SerenityProductId {
    const val SUBSCRIPTIONS_ID = "serenity_plus_subscription"
    const val MONTHLY_SUBSCRIPTIONS_PRODUCT_ID = "month-subscription-v2"
    const val YEARLY_SUBSCRIPTIONS_PRODUCT_ID = "year-subscription"

    const val PERMANENT_ID = "permament_plan"

    val availableIds = setOf(
        PERMANENT_ID,
        SUBSCRIPTIONS_ID,
        MONTHLY_SUBSCRIPTIONS_PRODUCT_ID,
        YEARLY_SUBSCRIPTIONS_PRODUCT_ID,
    )
}