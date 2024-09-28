package com.furianrt.noteview.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
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
    openMediaSelectorScreen: (identifier: DialogIdentifier) -> Unit,
    onCloseRequest: () -> Unit,
) {
    composable<NoteViewRoute> {
        NoteViewScreen(
            openMediaViewScreen = openMediaViewScreen,
            openMediaSelectorScreen = openMediaSelectorScreen,
            onCloseRequest = onCloseRequest,
        )
    }
}