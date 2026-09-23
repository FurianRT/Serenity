package com.furianrt.settings.api

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.navigation
import com.furianrt.apptheme.api.appThemeScreen
import com.furianrt.apptheme.api.navigateToAppTheme
import com.furianrt.backup.api.backupScreen
import com.furianrt.backup.api.navigateToBackup
import com.furianrt.reminders.api.navigateToReminders
import com.furianrt.reminders.api.remindersNavigation
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
import com.furianrt.uikit.anim.defaultEnterTransition
import com.furianrt.uikit.anim.defaultExitForBillingTransition
import com.furianrt.uikit.anim.defaultExitTransition
import com.furianrt.uikit.anim.defaultPopEnterTransition
import com.furianrt.uikit.anim.defaultPopExitTransition
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

fun NavController.navigateToSettings(
    route: SettingsRoute = SettingsRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.settingsNavigation(
    navController: NavHostController,
    hasBillingRoute: (destination: NavDestination) -> Boolean,
    openGalleryScreen: () -> Unit,
    openBillingScreen: () -> Unit,
) {
    navigation<SettingsRoute>(
        startDestination = MainRoute,
        enterTransition = { defaultEnterTransition() },
        exitTransition = {
            if (hasBillingRoute(targetState.destination)) {
                defaultExitForBillingTransition()
            } else {
                defaultExitTransition()
            }
        },
        popExitTransition = { defaultPopExitTransition() },
        popEnterTransition = { defaultPopEnterTransition() },
    ) {
        settingsScreen(
            openSecurityScreen = navController::navigateToSecurity,
            openBackupScreen = navController::navigateToBackup,
            openNoteSettingsScreen = navController::navigateToNoteSettings,
            openAppThemeScreen = navController::navigateToAppTheme,
            openRemindersScreen = navController::navigateToReminders,
            openGalleryScreen = openGalleryScreen,
            openBillingScreen = openBillingScreen,
            onCloseRequest = {
                navController.popBackStack(route = SettingsRoute, inclusive = true)
            },
        )
        securityScreen(
            openChangePinScreen = navController::navigateToChangePin,
            openChangeEmailScreen = navController::navigateToChangeEmail,
            onCloseRequest = navController::navigateUp,
        )
        changePinNavigation(
            navController = navController,
        )
        changeEmailScreen(
            onCloseRequest = navController::navigateUp,
        )
        backupScreen(
            onCloseRequest = navController::navigateUp,
        )
        noteSettingsScreen(
            onCloseRequest = navController::navigateUp,
        )
        appThemeScreen(
            onCloseRequest = navController::navigateUp,
        )
        remindersNavigation(
            navController = navController,
        )
    }
}