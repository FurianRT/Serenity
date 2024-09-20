package com.furianrt.uikit.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.R
import com.furianrt.uikit.extensions.clickableWithScaleAnim

@Composable
fun ButtonMenu(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier.clickableWithScaleAnim(
            maxScale = 1.2f,
            indication = ripple(bounded = false, radius = 20.dp),
            onClick = onClick,
        ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = modifier.padding(8.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_action_menu),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}
