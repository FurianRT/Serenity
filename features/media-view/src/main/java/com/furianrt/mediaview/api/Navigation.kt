package com.furianrt.mediaview.api

import android.net.Uri
import android.os.Bundle
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.furianrt.common.LocalDateSerializer
import com.furianrt.mediaview.internal.ui.MediaViewScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDate
import kotlin.reflect.typeOf

@Serializable
class MediaViewRoute(
    val searchData: SearchData? = null,
    val dialogId: Int? = null,
    val requestId: String? = null,
    val allowDelete: Boolean = true,
    val allowGoToNote: Boolean = false,
) {
    @Serializable
    class SearchData(
        val noteId: String? = null,
        val mediaBlockId: String? = null,
        val initialMediaId: String? = null,
        @Serializable(with = LocalDateSerializer::class) val startDate: LocalDate? = null,
        @Serializable(with = LocalDateSerializer::class) val endDate: LocalDate? = null,
    )
}

fun NavController.navigateToMediaView(
    route: MediaViewRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.mediaViewScreen(
    onCloseRequest: () -> Unit,
    onOpenNoteViewRequest: (noteId: String) -> Unit,
) {
    composable<MediaViewRoute>(
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { fadeOut(animationSpec = tween(500)) },
        content = {
            MediaViewScreen(
                onCloseRequest = onCloseRequest,
                onOpenNoteViewRequest = onOpenNoteViewRequest,
            )
        },
        typeMap = mapOf(typeOf<MediaViewRoute.SearchData?>() to SearchDataType),
    )
}

internal val SearchDataType = object : NavType<MediaViewRoute.SearchData?>(
    isNullableAllowed = true,
) {
    override fun get(bundle: Bundle, key: String): MediaViewRoute.SearchData? {
        val string = bundle.getString(key)
        return if (string.isNullOrEmpty()) {
            null
        } else {
            Json.decodeFromString(string)
        }
    }

    override fun parseValue(value: String): MediaViewRoute.SearchData? {
        return if (value.isEmpty()) {
            null
        } else {
            Json.decodeFromString(Uri.decode(value))
        }
    }

    override fun serializeAsValue(value: MediaViewRoute.SearchData?): String {
        return if (value == null) {
            ""
        } else {
            Uri.encode(Json.encodeToString(value))
        }
    }

    override fun put(bundle: Bundle, key: String, value: MediaViewRoute.SearchData?) {
        value ?: return
        bundle.putString(key, Json.encodeToString(value))
    }
}
