package com.furianrt.notelist.api

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.furianrt.notelist.internal.ui.NoteListScreen
import com.furianrt.uikit.anim.defaultExitTransition
import com.furianrt.uikit.anim.defaultPopEnterTransition
import com.furianrt.uikit.utils.DialogIdentifier
import kotlinx.serialization.Serializable

@Serializable
data object NoteListRoute

fun NavGraphBuilder.noteListScreen(
    openNoteViewScreen: (noteId: String, identifier: DialogIdentifier) -> Unit,
    openNoteCreateScreen: (identifier: DialogIdentifier) -> Unit,
    openNoteSearchScreen: () -> Unit,
    openSettingsScreen: () -> Unit,
    hasSearchScreenRoute: (destination: NavDestination) -> Boolean,
) {
    composable<NoteListRoute>(
        exitTransition = {
            if (hasSearchScreenRoute(targetState.destination)) {
                fadeOut()
            } else {
               defaultExitTransition()
            }
        },
        popEnterTransition = {
            if (hasSearchScreenRoute(initialState.destination)) {
                fadeIn()
            } else {
                defaultPopEnterTransition()
            }
        },
    ) {
        NoteListScreen(
            openNoteViewScreen = openNoteViewScreen,
            openNoteCreateScreen = openNoteCreateScreen,
            openNoteSearchScreen = openNoteSearchScreen,
            openSettingsScreen = openSettingsScreen,
        )
    }
}