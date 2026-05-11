package com.furianrt.reminders.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.navigation
import com.furianrt.reminders.internal.ui.details.ReminderDetailsRoute
import com.furianrt.reminders.internal.ui.details.navigateToReminderDetails
import com.furianrt.reminders.internal.ui.details.reminderDetailsScreen
import com.furianrt.reminders.internal.ui.help.navigateToReminderHelp
import com.furianrt.reminders.internal.ui.help.reminderHelpScreen
import com.furianrt.reminders.internal.ui.list.RemindersListRoute
import com.furianrt.reminders.internal.ui.list.remindersListScreen
import kotlinx.serialization.Serializable

@Serializable
data object RemindersRoute

fun NavController.navigateToReminders(
    route: RemindersRoute = RemindersRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.remindersNavigation(
    navController: NavHostController,
) {
    navigation<RemindersRoute>(
        startDestination = RemindersListRoute,
    ) {
        remindersListScreen(
            openReminderDetailsScreen = { reminderId ->
                navController.navigateToReminderDetails(
                    route = ReminderDetailsRoute(
                        reminderId = reminderId,
                    )
                )
            },
            openTroubleShootingScreen = navController::navigateToReminderHelp,
            onCloseRequest = {
                navController.popBackStack(route = RemindersRoute, inclusive = true)
            },
        )
        reminderDetailsScreen(
            onCloseRequest = navController::navigateUp,
        )
        reminderHelpScreen(
            onCloseRequest = navController::navigateUp,
        )
    }
}