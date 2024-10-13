package com.furianrt.uikit.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun isGestureNavigationEnabled() = WindowInsets.navigationBars.asPaddingValues()
    .calculateBottomPadding() < 24.dp