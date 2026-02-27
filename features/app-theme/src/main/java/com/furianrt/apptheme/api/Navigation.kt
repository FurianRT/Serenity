package com.furianrt.apptheme.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.apptheme.internal.ui.AppThemeScreen
import kotlinx.serialization.Serializable

@Serializable
data object AppThemeRoute

fun NavController.navigateToAppTheme(
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = AppThemeRoute, navOptions = navOptions)

fun NavGraphBuilder.appThemeScreen(
    onCloseRequest: () -> Unit,
) {
    composable<AppThemeRoute>(
        content = {
            AppThemeScreen(
                onCloseRequest = onCloseRequest,
            )
        },
    )
}