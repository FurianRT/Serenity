package com.furianrt.uikit.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.furianrt.uikit.R

@Composable
fun SerenityPlusIcon(
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_leaf),
        tint = MaterialTheme.colorScheme.primaryContainer,
        contentDescription = null,
    )
}