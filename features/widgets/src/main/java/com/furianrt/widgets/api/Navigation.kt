package com.furianrt.widgets.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.widgets.internal.screen.WidgetsScreen
import kotlinx.serialization.Serializable

@Serializable
data object WidgetsRoute

fun NavController.navigateToWidgets(
    route: WidgetsRoute = WidgetsRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.widgetsScreen(
    onCloseRequest: () -> Unit,
) {
    composable<WidgetsRoute> {
        WidgetsScreen(
            onCloseRequest = onCloseRequest,
        )
    }
}