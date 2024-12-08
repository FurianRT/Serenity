package com.furianrt.notelist.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.furianrt.notelist.internal.ui.NoteListScreen
import com.furianrt.uikit.utils.DialogIdentifier
import kotlinx.serialization.Serializable

@Serializable
data object NoteListRoute

fun NavGraphBuilder.noteListScreen(
    openNoteViewScreen: (noteId: String, identifier: DialogIdentifier) -> Unit,
    openNoteCreateScreen: (identifier: DialogIdentifier) -> Unit,
    openNoteSearchScreen: () -> Unit,
    openSettingsScreen: () -> Unit,
) {
    composable<NoteListRoute> {
        NoteListScreen(
            openNoteViewScreen = openNoteViewScreen,
            openNoteCreateScreen = openNoteCreateScreen,
            openNoteSearchScreen = openNoteSearchScreen,
            openSettingsScreen = openSettingsScreen,
        )
    }
}