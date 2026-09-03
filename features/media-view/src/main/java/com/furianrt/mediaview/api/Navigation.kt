package com.furianrt.mediaview.api

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.mediaview.internal.ui.MediaViewScreen
import kotlinx.serialization.Serializable

@Serializable
class MediaViewRoute(
    val noteId: String? = null,
    val mediaBlockId: String? = null,
    val initialMediaId: String? = null,
    val dialogId: Int? = null,
    val requestId: String? = null,
    val allowDelete: Boolean = true,
    val allowGoToNote: Boolean = false,
)

fun NavController.navigateToMediaView(
    route: MediaViewRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.mediaViewScreen(
    onCloseRequest: () -> Unit,
    onOpenNoteViewRequest: (noteId: String) -> Unit,
) {
    composable<MediaViewRoute>(
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { fadeOut(animationSpec = tween(500)) },
        content = {
            MediaViewScreen(
                onCloseRequest = onCloseRequest,
                onOpenNoteViewRequest = onOpenNoteViewRequest,
            )
        },
    )
}