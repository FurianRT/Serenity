package com.furianrt.billing.internal.ui.mappers

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.furianrt.billing.R
import com.furianrt.billing.internal.domain.entities.SerenityPlusPlan
import com.furianrt.billing.internal.ui.entities.SubscriptionPlan
import com.furianrt.domain.managers.ResourcesManager
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
internal class SubscriptionPlanMapper @Inject constructor(
    private val resourcesManager: ResourcesManager,
) {
    fun map(plan: SerenityPlusPlan) = SubscriptionPlan(
        id = plan.productId,
        title = when (plan) {
            is SerenityPlusPlan.Monthly -> resourcesManager.getString(
                R.string.billing_subscription_plan_monthly,
            )

            is SerenityPlusPlan.Yearly -> resourcesManager.getString(
                R.string.billing_subscription_plan_yearly,
            )

            is SerenityPlusPlan.Permanent -> resourcesManager.getString(
                R.string.billing_subscription_plan_permanent,
            )
        },
        hasTrial = plan.hasTrial,
        mainPrice = when (plan) {
            is SerenityPlusPlan.Monthly -> resourcesManager.getString(
                R.string.billing_subscription_plan_monthly_pattern,
                plan.price,
            )

            is SerenityPlusPlan.Yearly -> resourcesManager.getString(
                R.string.billing_subscription_plan_yearly_pattern,
                plan.price,
            )

            is SerenityPlusPlan.Permanent -> plan.price
        },
        secondPrice = when (plan) {
            is SerenityPlusPlan.Monthly -> null
            is SerenityPlusPlan.Yearly -> buildAnnotatedString {
                if (plan.perMonthPrice != null) {
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                        append(plan.perMonthPrice)
                    }
                    append(" ")
                }
                if (plan.yearlyPerMonthPrice != null) {
                    append(
                        resourcesManager.getString(
                            R.string.billing_subscription_plan_monthly_pattern,
                            plan.yearlyPerMonthPrice,
                        )
                    )
                }
            }.takeIf { it.text.isNotEmpty() }

            is SerenityPlusPlan.Permanent -> AnnotatedString(
                resourcesManager.getString(
                    R.string.billing_subscription_plan_permanent_body,
                )
            )
        },
        discount = if (plan is SerenityPlusPlan.Yearly && plan.discount != null) {
            resourcesManager.getString(R.string.billing_discount_badge_pattern, plan.discount)
        } else {
            null
        },
    )
}