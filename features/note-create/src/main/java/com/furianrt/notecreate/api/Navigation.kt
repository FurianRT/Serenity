package com.furianrt.notecreate.api

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.notecreate.internal.ui.NoteCreateScreen
import com.furianrt.uikit.anim.defaultExitTransition
import com.furianrt.uikit.anim.defaultPopEnterTransition
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
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    onCloseRequest: () -> Unit,
) {
    composable<NoteCreateRoute>(
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
        popEnterTransition = { defaultPopEnterTransition() },
    ) {
        NoteCreateScreen(
            openMediaViewScreen = openMediaViewScreen,
            openMediaViewer = openMediaViewer,
            onCloseRequest = onCloseRequest,
        )
    }
}