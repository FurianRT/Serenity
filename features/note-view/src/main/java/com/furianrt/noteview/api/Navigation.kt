package com.furianrt.noteview.api

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.noteview.internal.ui.NoteViewScreen
import com.furianrt.uikit.utils.DialogIdentifier
import kotlinx.serialization.Serializable

@Serializable
data class NoteViewRoute(
    val noteId: String,
    val dialogId: Int,
    val requestId: String,
)

fun NavController.navigateToNoteView(
    route: NoteViewRoute,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) = navigate(route = route, builder = navOptions)

fun NavGraphBuilder.noteViewScreen(
    openMediaViewScreen: (noteId: String, mediaName: String, identifier: DialogIdentifier) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    onCloseRequest: () -> Unit,
) {
    composable<NoteViewRoute>(
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
        NoteViewScreen(
            openMediaViewScreen = openMediaViewScreen,
            openMediaViewer = openMediaViewer,
            onCloseRequest = onCloseRequest,
        )
    }
}