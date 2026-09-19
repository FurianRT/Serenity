package com.furianrt.billing.internal.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.R as uiR

@Composable
internal fun Benefit(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(uiR.drawable.ic_photos),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Custom Stickers",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                modifier = Modifier.alpha(0.5f),
                text = "Add bla bla stickers bla bla cool awesome best fuck yeah",
                style = MaterialTheme.typography.labelSmall,
                lineHeight = MaterialTheme.typography.labelSmall.lineHeight * 0.9f,
            )
        }
    }
}