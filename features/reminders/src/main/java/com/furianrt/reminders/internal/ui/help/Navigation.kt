package com.furianrt.reminders.internal.ui.help

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
internal data object ReminderHelpRoute

internal fun NavController.navigateToReminderHelp(
    route: ReminderHelpRoute = ReminderHelpRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

internal fun NavGraphBuilder.reminderHelpScreen(
    onCloseRequest: () -> Unit,
) {
    composable<ReminderHelpRoute> {
        RemindersHelpScreen(
            onCloseRequest = onCloseRequest,
        )
    }
}