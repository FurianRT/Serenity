package com.furianrt.backup.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.backup.internal.ui.BackupScreen
import kotlinx.serialization.Serializable

@Serializable
data object BackupRoute

fun NavController.navigateToBackup(
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = BackupRoute, navOptions = navOptions)

fun NavGraphBuilder.backupScreen(
    onCloseRequest: () -> Unit,
) {
    composable<BackupRoute> {
        BackupScreen(
            onCloseRequest = onCloseRequest,
        )
    }
}
