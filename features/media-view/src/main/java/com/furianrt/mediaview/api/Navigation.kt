package com.furianrt.mediaview.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.mediaview.internal.ui.MediaViewScreen
import kotlinx.serialization.Serializable

@Serializable
data class MediaViewRoute(
    val noteId: String,
    val mediaName: String,
    val dialogId: Int,
    val requestId: String,
)

fun NavController.navigateToMediaView(
    route: MediaViewRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.mediaViewScreen(
    onCloseRequest: () -> Unit,
) {
    composable<MediaViewRoute> {
        MediaViewScreen(onCloseRequest)
    }
}