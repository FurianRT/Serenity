package com.furianrt.settings.internal.ui.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
internal data object MainRoute

internal fun NavGraphBuilder.settingsScreen(
    openSecurityScreen: () -> Unit,
    openBackupScreen: () -> Unit,
    onCloseRequest: () -> Unit,
) {
    composable<MainRoute> {
        SettingsScreen(
            openSecurityScreen = openSecurityScreen,
            openBackupScreen = openBackupScreen,
            onCloseRequest = onCloseRequest,
        )
    }
}