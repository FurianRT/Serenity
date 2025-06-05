package com.furianrt.toolspanel.internal.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.theme.SerenityTheme

@Composable
internal fun ColorResetItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .alpha(0.5f)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(uiR.drawable.ic_exit),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
        )
    }
}

@Preview(widthDp = 40, heightDp = 40)
@Composable
private fun Preview() {
    SerenityTheme {
        ColorResetItem(
            onClick = {},
        )
    }
}