package com.furianrt.uikit.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.furianrt.uikit.R
import com.furianrt.uikit.theme.LocalIsLightTheme
import com.furianrt.uikit.utils.brighterBy

@Composable
fun SerenityPlusIcon(
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_leaf),
        tint = if (LocalIsLightTheme.current) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.primaryContainer.brighterBy(0.1f)
        },
        contentDescription = null,
    )
}