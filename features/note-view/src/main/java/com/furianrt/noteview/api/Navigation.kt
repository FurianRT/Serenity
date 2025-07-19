package com.furianrt.noteview.api

import android.net.Uri
import android.os.Bundle
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.furianrt.common.LocalDateSerializer
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.noteview.internal.ui.NoteViewScreen
import com.furianrt.uikit.anim.defaultPopExitTransition
import com.furianrt.uikit.utils.DialogIdentifier
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDate
import kotlin.reflect.typeOf

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
    openMediaViewScreen: (noteId: String, mediaId: String, identifier: DialogIdentifier) -> Unit,
    openMediaSortingScreen: (noteId: String, blockId: String, identifier: DialogIdentifier) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    onCloseRequest: () -> Unit,
) {
    composable<NoteViewRoute>(
        exitTransition = { ExitTransition.None },
        popExitTransition = { defaultPopExitTransition() },
        popEnterTransition = { EnterTransition.None },
        typeMap = mapOf(typeOf<NoteViewRoute.SearchData?>() to SearchDataType),
    ) {
        NoteViewScreen(
            openMediaViewScreen = openMediaViewScreen,
            openMediaSortingScreen = openMediaSortingScreen,
            openMediaViewer = openMediaViewer,
            onCloseRequest = onCloseRequest,
        )
    }
}