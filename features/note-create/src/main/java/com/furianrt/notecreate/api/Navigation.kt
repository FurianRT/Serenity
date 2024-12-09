package com.furianrt.notecreate.api

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.notecreate.internal.ui.NoteCreateScreen
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
    openMediaViewScreen: (noteId: String, mediaName: String, identifier: DialogIdentifier) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    onCloseRequest: () -> Unit,
) {
    composable<NoteCreateRoute>(
        exitTransition = { ExitTransition.None },
        popExitTransition = { defaultPopExitTransition() },
        popEnterTransition = { EnterTransition.None },
    ) {
        NoteCreateScreen(
            openMediaViewScreen = openMediaViewScreen,
            openMediaViewer = openMediaViewer,
            onCloseRequest = onCloseRequest,
        )
    }
}