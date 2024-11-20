package com.furianrt.lock.api

import androidx.compose.runtime.Composable
import com.furianrt.lock.internal.ui.check.CheckPinScreenInternal
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