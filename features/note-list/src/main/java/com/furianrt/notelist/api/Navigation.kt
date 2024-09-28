package com.furianrt.notelist.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.notelist.internal.ui.NoteListScreen
import com.furianrt.uikit.utils.DialogIdentifier
import kotlinx.serialization.Serializable

@Serializable
data object NoteListRoute

fun NavController.navigateToNoteList(
    route: NoteListRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.noteListScreen(
    openNoteViewScreen: (noteId: String, identifier: DialogIdentifier) -> Unit,
    openNoteCreateScreen: (identifier: DialogIdentifier) -> Unit,
    openSettingsScreen: () -> Unit,
) {
    composable<NoteListRoute> {
        NoteListScreen(
            openNoteViewScreen = openNoteViewScreen,
            openNoteCreateScreen = openNoteCreateScreen,
            openSettingsScreen = openSettingsScreen,
        )
    }
}