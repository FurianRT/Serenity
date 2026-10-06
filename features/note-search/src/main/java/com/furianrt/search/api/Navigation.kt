package com.furianrt.search.api

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.search.api.entities.QueryData
import com.furianrt.search.internal.ui.SearchScreen
import com.furianrt.uikit.anim.defaultEnterTransition
import com.furianrt.uikit.anim.defaultExitTransition
import com.furianrt.uikit.anim.defaultPopEnterTransition
import com.furianrt.uikit.anim.defaultPopExitTransition
import com.furianrt.uikit.utils.DialogIdentifier
import kotlinx.serialization.Serializable

@Serializable
data class NoteSearchRoute(
    val startDate: String? = null,
    val endDate: String? = null,
)

fun NavController.navigateToNoteSearch(
    route: NoteSearchRoute = NoteSearchRoute(),
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) {
    navigate(route = route, navOptions = navOptions)
}

fun NavGraphBuilder.noteSearchScreen(
    hasStatsRoute: (destination: NavDestination) -> Boolean,
    openNoteViewScreen: (noteId: String, identifier: DialogIdentifier, data: QueryData) -> Unit,
    onCloseRequest: () -> Unit,
) {
    composable<NoteSearchRoute>(
        enterTransition = {
            if (hasStatsRoute(initialState.destination)) {
                defaultEnterTransition()
            } else {
                fadeIn(spring(stiffness = Spring.StiffnessMediumLow))
            }
        },
        exitTransition = { defaultExitTransition() },
        popExitTransition = {
            if (hasStatsRoute(targetState.destination)) {
                defaultPopExitTransition()
            } else {
                fadeOut()
            }
        },
        popEnterTransition = { defaultPopEnterTransition() },
    ) {
        SearchScreen(
            openNoteViewScreen = openNoteViewScreen,
            onCloseRequest = onCloseRequest,
        )
    }
}
