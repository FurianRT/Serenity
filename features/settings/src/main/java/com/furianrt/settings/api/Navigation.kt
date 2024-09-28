package com.furianrt.settings.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.settings.internal.ui.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

fun NavController.navigateToSettings(
    route: SettingsRoute = SettingsRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.settingsScreen(
    onCloseRequest: () -> Unit,
) {
    composable<SettingsRoute> {
        SettingsScreen(
            onCloseRequest = onCloseRequest,
        )
    }
}