package com.furianrt.billing.internal.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.furianrt.billing.internal.ui.entities.BenefitItem
import com.furianrt.uikit.components.OptionButtonWrapper
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState

@Composable
internal fun Benefit(
    benefit: BenefitItem,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    OptionButtonWrapper(
        modifier = modifier,
        hazeState = hazeState,
        borderColor = Color.Transparent,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = benefit.getIcon(),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = benefit.getTitle(),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    modifier = Modifier.alpha(0.5f),
                    text = benefit.getSubtitle(),
                    style = MaterialTheme.typography.labelSmall,
                    lineHeight = MaterialTheme.typography.labelSmall.lineHeight * 0.9f,
                )
            }
        }
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        Benefit(
            benefit = BenefitItem.CUSTOM_STICKERS,
            hazeState = rememberHazeState(),
        )
    }
}
