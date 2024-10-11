package com.furianrt.mediaselector.api

import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorBottomSheet
import com.furianrt.mediaselector.internal.ui.viewer.MediaViewerScreen
import kotlinx.serialization.Serializable

@Serializable
data class MediaSelectorRoute(
    val dialogId: Int,
    val requestId: String,
)

@Serializable
data class MediaViewerRoute(
    val mediaId: Long,
    val dialogId: Int,
    val requestId: String,
)

fun NavController.navigateToMediaSelector(
    route: MediaSelectorRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.mediaSelectorDialog(
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    onCloseRequest: () -> Unit,
) {
    composable<MediaSelectorRoute> {
        MediaSelectorBottomSheet(openMediaViewer, onCloseRequest)
    }
}

fun NavController.navigateToMediaViewer(
    route: MediaViewerRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.mediaViewerScreen(
    onCloseRequest: () -> Unit,
) {
    dialog<MediaViewerRoute>(
        dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        content = { MediaViewerScreen(onCloseRequest) },
    )
}