package com.furianrt.notelist.api

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.graphics.TransformOrigin
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
    hasNoteCreateScreenRoute: (destination: NavDestination) -> Boolean,
) {
    composable<NoteListRoute>(
        exitTransition = {
            val showDefaultAnim = !hasSearchScreenRoute(targetState.destination) &&
                    !hasNoteCreateScreenRoute(targetState.destination)
            if (showDefaultAnim) {
                defaultExitTransition()
            } else {
                fadeOut() + scaleOut(
                    targetScale = 0.95f,
                    transformOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 0f),
                )
            }
        },
        popEnterTransition = {
            if (hasSearchScreenRoute(initialState.destination)) {
                fadeIn() + scaleIn(
                    initialScale = 0.95f,
                    transformOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 0f),
                )
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