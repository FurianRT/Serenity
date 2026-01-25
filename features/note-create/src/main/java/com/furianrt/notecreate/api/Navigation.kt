package com.furianrt.notecreate.api

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.notecreate.internal.ui.NoteCreateScreen
import com.furianrt.uikit.anim.defaultExitTransition
import com.furianrt.uikit.anim.defaultPopEnterTransition
import com.furianrt.uikit.anim.defaultPopExitTransition
import com.furianrt.uikit.utils.DialogIdentifier
import kotlinx.serialization.Serializable

@Serializable
data class NoteCreateRoute(
    val dialogId: Int,
    val requestId: String,
)

fun NavController.navigateToNoteCreate(
    route: NoteCreateRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.noteCreateScreen(
    openMediaViewScreen: (noteId: String, mediaId: String, identifier: DialogIdentifier) -> Unit,
    openMediaSortingScreen: (noteId: String, blockId: String, identifier: DialogIdentifier) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    hasMediaSortingRoute: (destination: NavDestination) -> Boolean,
    onCloseRequest: () -> Unit,
) {
    composable<NoteCreateRoute>(
        exitTransition = {
            if (hasMediaSortingRoute(targetState.destination)) {
                defaultExitTransition()
            } else {
                ExitTransition.None
            }
        },
        popExitTransition = { defaultPopExitTransition() },
        enterTransition = {
            fadeIn(tween(durationMillis = 350)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                initialOffset = { (it * 0.05f).toInt() },
                animationSpec = tween(
                    durationMillis = 350,
                    easing = FastOutSlowInEasing,
                ),
            )
        },
        popEnterTransition = {
            if (hasMediaSortingRoute(initialState.destination)) {
                defaultPopEnterTransition()
            } else {
                EnterTransition.None
            }
        },
    ) {
        NoteCreateScreen(
            openMediaViewScreen = openMediaViewScreen,
            openMediaSortingScreen = openMediaSortingScreen,
            openMediaViewer = openMediaViewer,
            onCloseRequest = onCloseRequest,
        )
    }
}