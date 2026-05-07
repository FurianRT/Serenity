package com.furianrt.reminders.internal.ui.list

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
internal data object RemindersListRoute

internal fun NavGraphBuilder.remindersListScreen(
    openReminderDetailsScreen: (reminderId: String?) -> Unit,
    openTroubleShootingScreen: () -> Unit,
    onCloseRequest: () -> Unit,
) {
    composable<RemindersListRoute> {
        RemindersListScreen(
            openReminderDetailsScreen = openReminderDetailsScreen,
            openTroubleShootingScreen = openTroubleShootingScreen,
            onCloseRequest = onCloseRequest,
        )
    }
}