package com.furianrt.settings.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.navigation
import com.furianrt.backup.api.backupNavigation
import com.furianrt.backup.api.navigateToBackup
import com.furianrt.lock.api.changeEmailScreen
import com.furianrt.lock.api.changePinNavigation
import com.furianrt.lock.api.navigateToChangeEmail
import com.furianrt.lock.api.navigateToChangePin
import com.furianrt.settings.internal.ui.main.MainRoute
import com.furianrt.settings.internal.ui.main.settingsScreen
import com.furianrt.settings.internal.ui.security.navigateToSecurity
import com.furianrt.settings.internal.ui.security.securityScreen
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

fun NavController.navigateToSettings(
    route: SettingsRoute = SettingsRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.settingsNavigation(
    navController: NavHostController,
) {
    navigation<SettingsRoute>(
        startDestination = MainRoute,
    ) {
        settingsScreen(
            openSecurityScreen = navController::navigateToSecurity,
            openBackupScreen = navController::navigateToBackup,
            onCloseRequest = {
                navController.popBackStack(route = SettingsRoute, inclusive = true)
            },
        )
        securityScreen(
            openChangePinScreen = navController::navigateToChangePin,
            openChangeEmailScreen = navController::navigateToChangeEmail,
            onCloseRequest = { navController.popBackStack(route = MainRoute, inclusive = false) },
        )
        changePinNavigation(
            navController = navController,
        )
        changeEmailScreen(
            onCloseRequest = navController::navigateUp,
        )
        backupNavigation(
            navController = navController,
        )
    }
}