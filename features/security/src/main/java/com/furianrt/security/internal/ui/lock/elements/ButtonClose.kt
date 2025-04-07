package com.furianrt.security.internal.ui.lock.elements

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun ButtonClose(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(uiR.drawable.ic_exit),
            tint = Color.Unspecified,
            contentDescription = null
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    ButtonClose(
        onClick = {},
    )
}
