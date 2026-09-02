package com.furianrt.gallery.api

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.gallery.internal.ui.GalleryScreen
import com.furianrt.uikit.anim.defaultExitTransition
import com.furianrt.uikit.anim.defaultPopExitTransition
import kotlinx.serialization.Serializable

@Serializable
data object GalleryRoute

fun NavController.navigateToGallery(
    route: GalleryRoute = GalleryRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) {
    navigate(route = route, navOptions = navOptions)
}

fun NavGraphBuilder.galleryScreen(
    hasNoteCreateScreenRoute: (destination: NavDestination) -> Boolean,
    onCloseRequest: () -> Unit,
    openCreateNoteRequest: () -> Unit,
) {
    composable<GalleryRoute>(
        exitTransition = {
            if (hasNoteCreateScreenRoute(targetState.destination)) {
                fadeOut(tween(250))
            } else {
                defaultExitTransition()
            }
        },
        popExitTransition = { defaultPopExitTransition() },
    ) {
        GalleryScreen(
            onCloseRequest = onCloseRequest,
            openCreateNoteRequest = openCreateNoteRequest,
        )
    }
}
