package com.furianrt.notecreate.api

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.notecreate.internal.ui.NoteCreateScreen
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
    openMediaViewScreen: (noteId: String, mediaName: String, identifier: DialogIdentifier) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    onCloseRequest: () -> Unit,
) {
    composable<NoteCreateRoute>(
        exitTransition = { ExitTransition.None },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                targetOffset = { (it * 0.8f).toInt() },
                animationSpec = tween(
                    durationMillis = 400,
                    easing = LinearEasing,
                ),
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = { EnterTransition.None },
    ) {
        NoteCreateScreen(
            openMediaViewScreen = openMediaViewScreen,
            openMediaViewer = openMediaViewer,
            onCloseRequest = onCloseRequest,
        )
    }
}