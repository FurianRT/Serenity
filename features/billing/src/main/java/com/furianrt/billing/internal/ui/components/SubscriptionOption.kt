package com.furianrt.billing.internal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.furianrt.billing.R
import com.furianrt.billing.internal.ui.entities.SubscriptionPlan
import com.furianrt.uikit.components.OptionButtonWrapper
import dev.chrisbanes.haze.HazeState

@Composable
internal fun SubscriptionOption(
    plan: SubscriptionPlan,
    hazeState: HazeState,
    onSelected: (plan: SubscriptionPlan) -> Unit,
    modifier: Modifier = Modifier,
) {
    OptionButtonWrapper(
        modifier = modifier,
        hazeState = hazeState,
        backgroundColor = if (plan.iSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        } else {
            Color.Transparent
        },
        borderColor = if (plan.iSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.background
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
                .clickable { onSelected(plan) }
                .padding(vertical = 16.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RadioIndicator(
                iSelected = plan.iSelected,
            )
            when (plan) {
                is SubscriptionPlan.Yearly -> YearlyContent(
                    plan = plan,
                )

                is SubscriptionPlan.Monthly -> MonthlyContent(
                    plan = plan,
                )

                is SubscriptionPlan.Permanent -> PermanentContent(
                    plan = plan,
                )
            }
        }
    }
}

@Composable
private fun YearlyContent(
    plan: SubscriptionPlan.Yearly,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(R.string.billing_subscription_plan_yearly),
                style = MaterialTheme.typography.titleSmall,
            )
            DiscountBadge(
                discount = plan.discount,
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .alpha(0.5f),
                text = "190.00 ₽/month",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
            )
        }
        Row(
            modifier = Modifier.alpha(0.5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "3 588.00 ₽",
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = "1 990.00 ₽/year",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun MonthlyContent(
    plan: SubscriptionPlan.Monthly,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.billing_subscription_plan_monthly),
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            modifier = Modifier.alpha(0.5f),
            text = "1 990.00 ₽/year",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun PermanentContent(
    plan: SubscriptionPlan.Permanent,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.billing_subscription_plan_permanent),
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                modifier = Modifier.alpha(0.5f),
                text = stringResource(R.string.billing_subscription_plan_permanent_body),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
            )
        }
        Text(
            modifier = Modifier
                .weight(1f)
                .alpha(0.5f),
            text = "3 000 ₽",
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun RadioIndicator(
    iSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    RadioButton(
        modifier = modifier,
        selected = iSelected,
        colors = RadioButtonDefaults.colors(
            selectedColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        ),
        onClick = null,
    )
}

@Composable
private fun DiscountBadge(
    discount: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(6.dp),
            )
            .padding(horizontal = 4.dp, vertical = 2.dp),
        text = stringResource(R.string.billing_discount_badge_pattern, discount),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        letterSpacing = MaterialTheme.typography.labelSmall.letterSpacing * 0.9f,
    )
}
