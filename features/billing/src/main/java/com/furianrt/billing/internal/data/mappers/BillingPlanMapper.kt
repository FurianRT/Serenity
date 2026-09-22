package com.furianrt.billing.internal.data.mappers

import com.android.billingclient.api.ProductDetails
import com.furianrt.billing.internal.data.SerenityProductId
import com.furianrt.billing.internal.domain.entities.SerenityPlusPlan
import com.furianrt.storage.internal.database.billing.entities.EntryBillingPlan
import java.util.Locale
import kotlin.collections.orEmpty

internal fun EntryBillingPlan.toDomain(): SerenityPlusPlan = when (type) {
    EntryBillingPlan.Type.MONTHLY -> SerenityPlusPlan.Monthly(
        productId = id,
        price = price,
        hasTrial = isTrialAvailable,
    )

    EntryBillingPlan.Type.YEARLY -> SerenityPlusPlan.Yearly(
        productId = id,
        price = price,
        hasTrial = isTrialAvailable,
        perMonthPrice = perMonthPrice,
        yearlyPerMonthPrice = yearlyPerMonthPrice,
        discount = discount,
    )

    EntryBillingPlan.Type.PERMANENT -> SerenityPlusPlan.Permanent(
        productId = id,
        price = price,
        hasTrial = isTrialAvailable,
    )
}

internal fun List<ProductDetails>.toEntryBillingPlans(): List<EntryBillingPlan> {
    val availablePlans = filter { SerenityProductId.availableIds.contains(it.productId) }
    return availablePlans.flatMap { product ->
        when (product.productId) {
            SerenityProductId.PERMANENT_ID -> listOf(product.toPermanentPlan())
            SerenityProductId.SUBSCRIPTIONS_ID -> product.toSubscriptionPlans()
            else -> emptyList()
        }
    }
}

private fun ProductDetails.toPermanentPlan() = EntryBillingPlan(
    id = productId,
    price = oneTimePurchaseOfferDetails!!.formattedPrice,
    isTrialAvailable = false,
    type = EntryBillingPlan.Type.PERMANENT,
    perMonthPrice = null,
    yearlyPerMonthPrice = null,
    discount = null,
)

private fun ProductDetails.SubscriptionOfferDetails.toMonthSubscription(
    hasTrial: Boolean,
) = EntryBillingPlan(
    id = basePlanId,
    price = pricingPhases.pricingPhaseList.first().formattedPrice,
    isTrialAvailable = hasTrial,
    type = EntryBillingPlan.Type.MONTHLY,
    perMonthPrice = null,
    yearlyPerMonthPrice = null,
    discount = null,
)

private fun ProductDetails.SubscriptionOfferDetails.toYearSubscription(
    hasTrial: Boolean,
    perMonthPrice: String?,
): EntryBillingPlan? {
    val yearlyPhase = pricingPhases.pricingPhaseList.firstOrNull() ?: return null
    return EntryBillingPlan(
        id = basePlanId,
        price = yearlyPhase.formattedPrice,
        isTrialAvailable = hasTrial,
        type = EntryBillingPlan.Type.YEARLY,
        perMonthPrice = perMonthPrice,
        yearlyPerMonthPrice = calculateMonthlyPriceFromYearly(
            yearlyPhase = yearlyPhase,
        ),
        discount = 50,
    )
}

private fun ProductDetails.toSubscriptionPlans(): List<EntryBillingPlan> {
    val availableSubscriptions = subscriptionOfferDetails.orEmpty().filter { details ->
        SerenityProductId.availableIds.contains(details.basePlanId)
    }
    return availableSubscriptions.mapNotNull { subscription ->
        when (subscription.basePlanId) {
            SerenityProductId.MONTHLY_SUBSCRIPTIONS_PRODUCT_ID -> subscription.toMonthSubscription(
                hasTrial = availableSubscriptions.any { details ->
                    details.basePlanId == SerenityProductId.MONTHLY_SUBSCRIPTIONS_PRODUCT_ID &&
                            details.offerTags.contains(SerenityProductId.TAG_TRIAL)
                },
            )

            SerenityProductId.YEARLY_SUBSCRIPTIONS_PRODUCT_ID -> {
                val monthSubscription = availableSubscriptions.find { details ->
                    details.basePlanId == SerenityProductId.MONTHLY_SUBSCRIPTIONS_PRODUCT_ID &&
                            !details.offerTags.contains(SerenityProductId.TAG_TRIAL)
                }
                val monthDetails = monthSubscription?.pricingPhases?.pricingPhaseList?.firstOrNull()
                subscription.toYearSubscription(
                    hasTrial = availableSubscriptions.any { details ->
                        details.basePlanId == SerenityProductId.YEARLY_SUBSCRIPTIONS_PRODUCT_ID &&
                                details.offerTags.contains(SerenityProductId.TAG_TRIAL)
                    },
                    perMonthPrice = monthDetails?.formattedPrice,
                )
            }

            else -> null
        }
    }
}

private fun calculateMonthlyPriceFromYearly(yearlyPhase: ProductDetails.PricingPhase): String {
    val originalPriceText = yearlyPhase.formattedPrice

    val yearlyMicros = yearlyPhase.priceAmountMicros
    val monthlyAmount = (yearlyMicros / 12.0) / 1_000_000.0

    val numberRegex = Regex("[0-9 \\s.,]+")
    val matchResult = numberRegex.find(originalPriceText)

    return if (matchResult != null) {
        val rawNumberString = matchResult.value

        val hasCommaSeparator = rawNumberString.contains(",")
        val hasDotSeparator = rawNumberString.contains(".")

        val newNumberString = when {
            hasCommaSeparator -> {
                String.format(Locale.FRANCE, "%,.2f", monthlyAmount)
                    .replace("\u00A0", " ")
            }

            hasDotSeparator -> {
                String.format(Locale.US, "%,.2f", monthlyAmount)
            }

            else -> {
                String.format(Locale.US, "%,.2f", monthlyAmount)
            }
        }

        originalPriceText.replace(rawNumberString.trim(), newNumberString.trim())
    } else {
        String.format(Locale.US, "%.2f %s", monthlyAmount, yearlyPhase.priceCurrencyCode)
    }
}
