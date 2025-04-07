package com.furianrt.security.internal.ui.lock.change

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
internal data object ChangePinRoute

internal fun NavGraphBuilder.changePinScreen(
    openEmailScreen: (pin: String) -> Unit,
    onCloseRequest: () -> Unit,
) {
    composable<ChangePinRoute> {
        ChangePinScreen(
            openEmailScreen = openEmailScreen,
            onCloseRequest = onCloseRequest,
        )
    }
}