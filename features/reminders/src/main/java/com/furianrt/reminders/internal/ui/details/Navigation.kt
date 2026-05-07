package com.furianrt.reminders.internal.ui.details

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
internal data class ReminderDetailsRoute(
    val reminderId: String?,
)

internal fun NavController.navigateToReminderDetails(
    route: ReminderDetailsRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

internal fun NavGraphBuilder.reminderDetailsScreen(
    onCloseRequest: () -> Unit,
) {
    composable<ReminderDetailsRoute> {
        RemindersDetailsScreen(
            onCloseRequest = onCloseRequest,
        )
    }
}