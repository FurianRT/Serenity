package com.furianrt.billing.internal.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.R as uiR

@Composable
internal fun Logo(
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier
            .alpha(0.85f)
            .size(120.dp),
        painter = painterResource(uiR.drawable.app_logo_big),
        tint = MaterialTheme.colorScheme.primaryContainer,
        contentDescription = null,
    )
}