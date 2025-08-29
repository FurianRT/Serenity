package com.furianrt.settings.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.navigation
import com.furianrt.backup.api.backupNavigation
import com.furianrt.backup.api.navigateToBackup
import com.furianrt.security.api.changeEmailScreen
import com.furianrt.security.api.changePinNavigation
import com.furianrt.security.api.navigateToChangeEmail
import com.furianrt.security.api.navigateToChangePin
import com.furianrt.security.api.navigateToSecurity
import com.furianrt.security.api.securityScreen
import com.furianrt.settings.internal.ui.MainRoute
import com.furianrt.settings.internal.ui.noteSettings.navigateToNoteSettings
import com.furianrt.settings.internal.ui.noteSettings.noteSettingsScreen
import com.furianrt.settings.internal.ui.settingsScreen
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
            openNoteSettingsScreen = navController::navigateToNoteSettings,
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
        noteSettingsScreen(
            onCloseRequest = navController::navigateUp,
        )
    }
}