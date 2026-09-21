package com.furianrt.billing.internal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.furianrt.billing.internal.ui.entities.SubscriptionPlan
import com.furianrt.uikit.anim.shimmer
import com.furianrt.uikit.components.OptionButtonWrapper
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState

@Composable
internal fun SubscriptionOption(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    hazeState: HazeState,
    onSelected: (plan: SubscriptionPlan) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current

    OptionButtonWrapper(
        modifier = modifier,
        hazeState = hazeState,
        backgroundColor = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        } else {
            Color.Transparent
        },
        borderColor = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.background
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
                .clickable {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                    onSelected(plan)
                }
                .padding(vertical = 16.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RadioIndicator(
                iSelected = isSelected,
            )
            Content(
                plan = plan,
            )
        }
    }
}

@Composable
internal fun SubscriptionOptionSkeleton(
    isSelected: Boolean,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    OptionButtonWrapper(
        modifier = modifier,
        hazeState = hazeState,
        backgroundColor = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        } else {
            Color.Transparent
        },
        borderColor = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.background
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
                .padding(vertical = 16.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RadioIndicator(
                iSelected = isSelected,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 70.dp, height = 20.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.tertiary)
                        .shimmer()
                )
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 80.dp, height = 20.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.tertiary)
                            .shimmer()
                    )
                }
            }
        }
    }
}

@Composable
private fun Content(
    plan: SubscriptionPlan,
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
                text = plan.title,
                style = MaterialTheme.typography.titleSmall,
            )
            if (plan.discount != null) {
                DiscountBadge(
                    discount = plan.discount,
                )
            }
            Text(
                modifier = Modifier
                    .weight(1f)
                    .alpha(0.5f),
                text = plan.mainPrice,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
            )
        }
        if (plan.secondPrice != null) {
            Text(
                modifier = Modifier.alpha(0.5f),
                text = plan.secondPrice,
                style = MaterialTheme.typography.bodySmall,
            )
        }
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
    discount: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(6.dp),
            )
            .padding(horizontal = 4.dp, vertical = 2.dp),
        text = discount,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        letterSpacing = MaterialTheme.typography.labelSmall.letterSpacing * 0.9f,
    )
}

@PreviewWithBackground
@Composable
private fun PreviewSelected() {
    SerenityTheme {
        SubscriptionOption(
            plan = SubscriptionPlan(
                id = "",
                title = "Yearly",
                hasTrial = true,
                mainPrice = "199.00 P/month",
                secondPrice = AnnotatedString("1199.00 P/year"),
                discount = "50%",
            ),
            isSelected = true,
            hazeState = rememberHazeState(),
            onSelected = {},
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewUnselected() {
    SerenityTheme {
        SubscriptionOption(
            plan = SubscriptionPlan(
                id = "",
                title = "Yearly",
                hasTrial = true,
                mainPrice = "199.00 P/month",
                secondPrice = AnnotatedString("1199.00 P/year"),
                discount = "50%",
            ),
            isSelected = false,
            hazeState = rememberHazeState(),
            onSelected = {},
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewSkeletonSelected() {
    SerenityTheme {
        SubscriptionOptionSkeleton(
            isSelected = true,
            hazeState = rememberHazeState(),
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewSkeletonUnselected() {
    SerenityTheme {
        SubscriptionOptionSkeleton(
            isSelected = false,
            hazeState = rememberHazeState(),
        )
    }
}
