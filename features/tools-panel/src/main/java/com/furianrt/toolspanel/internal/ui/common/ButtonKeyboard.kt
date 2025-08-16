package com.furianrt.toolspanel.internal.ui.common

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.furianrt.toolspanel.R

@Composable
internal fun ButtonKeyboard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_keyboard),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}
