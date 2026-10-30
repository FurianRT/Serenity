package com.furianrt.apptheme.api

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.apptheme.internal.ui.AppThemeScreen
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.uikit.anim.defaultPopExitTransition
import kotlinx.serialization.Serializable

@Serializable
data object AppThemeRoute

fun NavController.navigateToAppTheme(
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = AppThemeRoute, navOptions = navOptions)

fun NavGraphBuilder.appThemeScreen(
    onCloseRequest: () -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
) {
    composable<AppThemeRoute>(
        exitTransition = { ExitTransition.None },
        popExitTransition = { defaultPopExitTransition() },
        popEnterTransition = { EnterTransition.None },
        content = {
            AppThemeScreen(
                onCloseRequest = onCloseRequest,
                openMediaViewer = openMediaViewer,
            )
        },
    )
}