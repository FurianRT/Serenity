package com.furianrt.mediasorting.api

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.mediasorting.internal.ui.MediaSortingScreen
import com.furianrt.uikit.anim.defaultPopExitTransition
import com.furianrt.uikit.utils.DialogIdentifier
import kotlinx.serialization.Serializable

@Serializable
data class MediaSortingRoute(
    val noteId: String,
    val mediaBlockId: String,
    val dialogId: Int,
    val requestId: String,
)

fun NavController.navigateToMediaSorting(
    route: MediaSortingRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.mediaSortingScreen(
    onCloseRequest: () -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    openMediaViewScreen: (
        noteId: String,
        mediaId: String,
        mediaBlockId: String,
        identifier: DialogIdentifier,
    ) -> Unit,
) {
    composable<MediaSortingRoute>(
        exitTransition = { ExitTransition.None },
        popExitTransition = { defaultPopExitTransition() },
        popEnterTransition = { EnterTransition.None },
        content = {
            MediaSortingScreen(
                onCloseRequest = onCloseRequest,
                openMediaViewer = openMediaViewer,
                openMediaViewScreen = openMediaViewScreen,
            )
        },
    )
}