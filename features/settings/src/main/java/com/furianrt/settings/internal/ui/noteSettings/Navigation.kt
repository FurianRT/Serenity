package com.furianrt.settings.internal.ui.noteSettings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
internal data object NoteSettingsRoute

internal fun NavController.navigateToNoteSettings(
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = NoteSettingsRoute, navOptions = navOptions)

internal fun NavGraphBuilder.noteSettingsScreen(
    onCloseRequest: () -> Unit,
) {
    composable<NoteSettingsRoute> {
        NoteSettingsScreen(
            onCloseRequest = onCloseRequest,
        )
    }
}
