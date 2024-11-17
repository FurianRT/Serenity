package com.furianrt.lock.api

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.furianrt.lock.internal.ui.change.ChangePinRoute
import com.furianrt.lock.internal.ui.change.changePinScreen
import com.furianrt.lock.internal.ui.email.EmailScreen
import kotlinx.serialization.Serializable

@Serializable
data object SetPinRoute

@Serializable
data class ChangeEmailRoute(
    val pin: String?,
)

fun NavController.navigateToChangePin(
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = SetPinRoute, navOptions = navOptions)

fun NavGraphBuilder.changePinNavigation(
    navController: NavHostController,
) {
    navigation<SetPinRoute>(
        startDestination = ChangePinRoute,
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) },
    ) {
        changePinScreen(
            openEmailScreen = { navController.navigateToChangeEmail(ChangeEmailRoute(it)) },
            onCloseRequest = navController::navigateUp,
        )
        changeEmailScreen(
            onCloseRequest = {
                navController.popBackStack(route = SetPinRoute, inclusive = true)
            },
        )
    }
}

fun NavController.navigateToChangeEmail(
    route: ChangeEmailRoute = ChangeEmailRoute(pin = null),
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.changeEmailScreen(
    onCloseRequest: () -> Unit,
) {
    composable<ChangeEmailRoute>(
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) },
    ) {
        EmailScreen(
            onCloseRequest = onCloseRequest,
        )
    }
}
