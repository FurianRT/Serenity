package com.furianrt.onboarding.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.furianrt.onboarding.internal.ui.container.ContainerScreen

@Composable
fun OnboardingScreen(
    onCloseRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ContainerScreen(
        modifier = modifier,
        onCloseRequest = onCloseRequest,
    )
}