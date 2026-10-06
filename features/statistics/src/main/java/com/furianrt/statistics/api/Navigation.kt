package com.furianrt.statistics.api

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.statistics.internal.ui.StatsScreen
import com.furianrt.uikit.anim.defaultExitTransition
import com.furianrt.uikit.anim.defaultPopExitTransition
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data object StatisticsRoute

fun NavController.navigateToStatistics(
    route: StatisticsRoute = StatisticsRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.statisticsScreen(
    hasNoteCreateScreenRoute: (destination: NavDestination) -> Boolean,
    openCreateNoteRequest: (date: ZonedDateTime) -> Unit,
    onCloseRequest: () -> Unit,
) {
    composable<StatisticsRoute>(
        exitTransition = {
            if ( hasNoteCreateScreenRoute(targetState.destination)) {
                fadeOut(tween(250))
            } else {
                defaultExitTransition()
            }
        },
        popExitTransition = { defaultPopExitTransition() },
    ) {
        StatsScreen(
            openCreateNoteRequest = openCreateNoteRequest,
            onCloseRequest = onCloseRequest,
        )
    }
}