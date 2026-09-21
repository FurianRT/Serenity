package com.furianrt.billing.internal.domain.entities

internal sealed class SerenityPlusPlan(
    open val productId: String,
    open val price: String,
    open val hasTrial: Boolean,
) {
    data class Monthly(
        override val productId: String,
        override val price: String,
        override val hasTrial: Boolean,
    ) : SerenityPlusPlan(
        productId = productId,
        price = price,
        hasTrial = hasTrial,
    )

    data class Yearly(
        override val productId: String,
        override val price: String,
        override val hasTrial: Boolean,
        val yearlyPerMonthPrice: String?,
        val perMonthPrice: String?,
        val discount: Int?,
    ) : SerenityPlusPlan(
        productId = productId,
        price = price,
        hasTrial = hasTrial,
    )

    data class Permanent(
        override val productId: String,
        override val price: String,
        override val hasTrial: Boolean,
    ) : SerenityPlusPlan(
        productId = productId,
        price = price,
        hasTrial = hasTrial,
    )
}