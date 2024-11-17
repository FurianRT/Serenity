package com.furianrt.settings.internal.ui.security

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
internal data object SecurityRoute

internal fun NavController.navigateToSecurity(
    route: SecurityRoute = SecurityRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

internal fun NavGraphBuilder.securityScreen(
    openChangePinScreen: () -> Unit,
    openChangeEmailScreen: () -> Unit,
    onCloseRequest: () -> Unit,
) {
    composable<SecurityRoute>(
        popEnterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) },
        popExitTransition = null,
    ) {
        SecurityScreen(
            openChangePinScreen = openChangePinScreen,
            openChangeEmailScreen = openChangeEmailScreen,
            onCloseRequest = onCloseRequest,
        )
    }
}