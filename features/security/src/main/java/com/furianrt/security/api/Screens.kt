package com.furianrt.security.api

import androidx.compose.runtime.Composable
import com.furianrt.security.internal.ui.lock.check.CheckPinScreenInternal
import dev.chrisbanes.haze.HazeState

@Composable
fun CheckPinScreen(
    hazeState: HazeState,
    onCloseRequest: () -> Unit,
) {
    CheckPinScreenInternal(
        hazeState = hazeState,
        onCloseRequest = onCloseRequest,
    )
}