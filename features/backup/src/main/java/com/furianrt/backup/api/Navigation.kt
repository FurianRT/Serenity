package com.furianrt.backup.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.furianrt.backup.internal.ui.BackupScreen
import kotlinx.serialization.Serializable

@Serializable
data object BackupRoute

@Serializable
internal data object BackupScreenRoute

fun NavController.navigateToBackup(
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = BackupRoute, navOptions = navOptions)

fun NavGraphBuilder.backupNavigation(
    navController: NavHostController,
) {
    navigation<BackupRoute>(
        startDestination = BackupScreenRoute,
    ) {
        backupScreen(
            onCloseRequest = navController::navigateUp,
        )
    }
}

private fun NavGraphBuilder.backupScreen(
    onCloseRequest: () -> Unit,
) {
    composable<BackupScreenRoute> {
        BackupScreen(
            onCloseRequest = onCloseRequest,
        )
    }
}
