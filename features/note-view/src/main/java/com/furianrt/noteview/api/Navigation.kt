package com.furianrt.noteview.api

import android.net.Uri
import android.os.Bundle
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.furianrt.common.LocalDateSerializer
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.noteview.internal.ui.NoteViewScreen
import com.furianrt.uikit.utils.DialogIdentifier
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDate
import kotlin.reflect.typeOf
import kotlinx.serialization.encodeToString

@Serializable
data class NoteViewRoute(
    val noteId: String,
    val dialogId: Int,
    val requestId: String,
    val searchData: SearchData? = null,
) {
    @Serializable
    data class SearchData(
        val query: String,
        val tags: Set<String>,
        @Serializable(with = LocalDateSerializer::class)
        val startDate: LocalDate?,
        @Serializable(with = LocalDateSerializer::class)
        val endDate: LocalDate?,
    )
}

internal val SearchDataType = object : NavType<NoteViewRoute.SearchData?>(
    isNullableAllowed = true,
) {
    override fun get(bundle: Bundle, key: String): NoteViewRoute.SearchData? {
        val string = bundle.getString(key)
        return if (string.isNullOrEmpty()) {
            null
        } else {
            Json.decodeFromString(string)
        }
    }

    override fun parseValue(value: String): NoteViewRoute.SearchData? {
        return if (value.isEmpty()) {
            null
        } else {
            Json.decodeFromString(Uri.decode(value))
        }
    }

    override fun serializeAsValue(value: NoteViewRoute.SearchData?): String {
        return if (value == null) {
            ""
        } else {
            Uri.encode(Json.encodeToString(value))
        }
    }

    override fun put(bundle: Bundle, key: String, value: NoteViewRoute.SearchData?) {
        value ?: return
        bundle.putString(key, Json.encodeToString(value))
    }
}

fun NavController.navigateToNoteView(
    route: NoteViewRoute,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) = navigate(route = route, builder = navOptions)

fun NavGraphBuilder.noteViewScreen(
    openMediaViewScreen: (noteId: String, mediaName: String, identifier: DialogIdentifier) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    onCloseRequest: () -> Unit,
) {
    composable<NoteViewRoute>(
        exitTransition = { ExitTransition.None },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                targetOffset = { (it * 0.8f).toInt() },
                animationSpec = tween(
                    durationMillis = 400,
                    easing = LinearEasing,
                ),
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = { EnterTransition.None },
        typeMap = mapOf(typeOf<NoteViewRoute.SearchData?>() to SearchDataType),
    ) {
        NoteViewScreen(
            openMediaViewScreen = openMediaViewScreen,
            openMediaViewer = openMediaViewer,
            onCloseRequest = onCloseRequest,
        )
    }
}