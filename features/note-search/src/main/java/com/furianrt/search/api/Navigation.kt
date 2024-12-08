package com.furianrt.search.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.search.api.entities.QueryData
import com.furianrt.search.internal.ui.SearchScreen
import com.furianrt.uikit.utils.DialogIdentifier
import kotlinx.serialization.Serializable

@Serializable
data object NoteSearchRoute

fun NavController.navigateToNoteSearch(
    route: NoteSearchRoute = NoteSearchRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.noteSearchScreen(
    openNoteViewScreen: (noteId: String, identifier: DialogIdentifier, data: QueryData) -> Unit,
    onCloseRequest: () -> Unit,
) {
    composable<NoteSearchRoute> {
        SearchScreen(
            openNoteViewScreen = openNoteViewScreen,
            onCloseRequest = onCloseRequest,
        )
    }
}
