package com.furianrt.notecreate.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
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
    openMediaSelectorScreen: (identifier: DialogIdentifier) -> Unit,
    onCloseRequest: () -> Unit,
) {
    composable<NoteCreateRoute> {
        NoteCreateScreen(
            openMediaViewScreen = openMediaViewScreen,
            openMediaSelectorScreen = openMediaSelectorScreen,
            onCloseRequest = onCloseRequest,
        )
    }
}