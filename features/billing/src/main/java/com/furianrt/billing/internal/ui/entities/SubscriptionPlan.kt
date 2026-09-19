package com.furianrt.billing.internal.ui.entities

sealed class SubscriptionPlan(
    open val finalPrice: Float,
    open val oldPrice: Float,
    open val discount: Int,
    open val iSelected: Boolean,
) {
    data class Yearly(
        override val finalPrice: Float,
        override val oldPrice: Float,
        override val discount: Int,
        override val iSelected: Boolean,
        val pricePerMonth: String,
    ) : SubscriptionPlan(
        finalPrice = finalPrice,
        oldPrice = oldPrice,
        discount = discount,
        iSelected = iSelected,
    )

    data class Monthly(
        override val finalPrice: Float,
        override val oldPrice: Float,
        override val discount: Int,
        override val iSelected: Boolean,
    ) : SubscriptionPlan(
        finalPrice = finalPrice,
        oldPrice = oldPrice,
        discount = discount,
        iSelected = iSelected,
    )

    data class Permanent(
        override val finalPrice: Float,
        override val oldPrice: Float,
        override val discount: Int,
        override val iSelected: Boolean,
    ) : SubscriptionPlan(
        finalPrice = finalPrice,
        oldPrice = oldPrice,
        discount = discount,
        iSelected = iSelected,
    )
}