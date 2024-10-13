package com.furianrt.mediaselector.api

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.mediaselector.internal.ui.viewer.MediaViewerScreen
import kotlinx.serialization.Serializable

@Serializable
data class MediaViewerRoute(
    val mediaId: Long,
    val dialogId: Int,
    val requestId: String,
)

fun NavController.navigateToMediaViewer(
    route: MediaViewerRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.mediaViewerScreen(
    onCloseRequest: () -> Unit,
) {
    composable<MediaViewerRoute>(
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) },
        content = { MediaViewerScreen(onCloseRequest) },
    )
}