package com.furianrt.statistics.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.statistics.internal.ui.StatsScreen
import kotlinx.serialization.Serializable

@Serializable
data object StatisticsRoute

fun NavController.navigateToStatistics(
    route: StatisticsRoute = StatisticsRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.statisticsScreen(
    onCloseRequest: () -> Unit,
) {
    composable<StatisticsRoute> {
        StatsScreen(
            onCloseRequest = onCloseRequest,
        )
    }
}