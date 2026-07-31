package com.furianrt.uikit.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.extensions.clickableNoRipple

@Composable
fun PendingView(
    modifier: Modifier = Modifier,
) {
    BackHandler(
        enabled = true,
        onBack = {},
    )
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .clickableNoRipple {},
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.tertiaryContainer,
            strokeWidth = 4.dp,
        )
    }
}