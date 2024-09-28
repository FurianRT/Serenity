package com.furianrt.mediaselector.api

import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.dialog
import com.furianrt.mediaselector.internal.ui.MediaSelectorBottomSheet
import kotlinx.serialization.Serializable

@Serializable
data class MediaSelectorRoute(
    val dialogId: Int,
    val requestId: String,
)

fun NavController.navigateToMediaSelector(
    route: MediaSelectorRoute,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) = navigate(route = route, builder = navOptions)

fun NavGraphBuilder.mediaSelectorDialog(
    onCloseRequest: () -> Unit,
) {
    dialog<MediaSelectorRoute>(
        dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        content = { MediaSelectorBottomSheet(onCloseRequest) },
    )
}