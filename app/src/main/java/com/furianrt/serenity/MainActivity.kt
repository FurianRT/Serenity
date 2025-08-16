package com.furianrt.serenity

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.mediaselector.api.mediaViewerScreen
import com.furianrt.mediaselector.api.navigateToMediaViewer
import com.furianrt.mediasorting.api.MediaSortingRoute
import com.furianrt.mediasorting.api.mediaSortingScreen
import com.furianrt.mediasorting.api.navigateToMediaSorting
import com.furianrt.mediaview.api.MediaViewRoute
import com.furianrt.mediaview.api.mediaViewScreen
import com.furianrt.mediaview.api.navigateToMediaView
import com.furianrt.notecreate.api.NoteCreateRoute
import com.furianrt.notecreate.api.navigateToNoteCreate
import com.furianrt.notecreate.api.noteCreateScreen
import com.furianrt.notelist.api.NoteListRoute
import com.furianrt.notelist.api.noteListScreen
import com.furianrt.noteview.api.NoteViewRoute
import com.furianrt.noteview.api.navigateToNoteView
import com.furianrt.noteview.api.noteViewScreen
import com.furianrt.search.api.NoteSearchRoute
import com.furianrt.search.api.navigateToNoteSearch
import com.furianrt.search.api.noteSearchScreen
import com.furianrt.security.api.CheckPinScreen
import com.furianrt.security.api.LockAuthorizer
import com.furianrt.settings.api.navigateToSettings
import com.furianrt.settings.api.settingsNavigation
import com.furianrt.uikit.anim.defaultEnterTransition
import com.furianrt.uikit.anim.defaultExitTransition
import com.furianrt.uikit.anim.defaultPopEnterTransition
import com.furianrt.uikit.anim.defaultPopExitTransition
import com.furianrt.uikit.entities.colorScheme
import com.furianrt.uikit.theme.LocalHasMediaRoute
import com.furianrt.uikit.theme.LocalHasMediaSortingRoute
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.IsAuthorizedProvider
import com.furianrt.uikit.utils.LocalAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val SPLASH_SCREEN_EXIT_ANIM_DURATION = 200L
private const val SPLASH_SCREEN_DELAY = 400L

@AndroidEntryPoint
internal class MainActivity : ComponentActivity(), IsAuthorizedProvider {

    @Inject
    lateinit var lockAuthorizer: LockAuthorizer

    private val viewModel: MainViewModel by viewModels()

    private var keepSplashScreen = true

    override suspend fun isAuthorized(): Boolean = lockAuthorizer.isAuthorized().first()

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet,
    ): View? = super.onCreateView(parent, name, context, attrs)
        .also { parent?.postDelayed({ keepSplashScreen = false }, SPLASH_SCREEN_DELAY) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { keepSplashScreen }
            setOnExitAnimationListener { splashScreenViewProvider ->
                splashScreenViewProvider.view
                    .animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .alpha(0f)
                    .setDuration(SPLASH_SCREEN_EXIT_ANIM_DURATION)
                    .withEndAction { splashScreenViewProvider.remove() }
            }
        }

        setContent {
            val uiState by viewModel.state.collectAsStateWithLifecycle()
            val navController = rememberNavController()
            val hazeState = remember { HazeState() }
            val activity = LocalActivity.current as ComponentActivity

            SerenityTheme(
                colorScheme = uiState.appColor.colorScheme,
                font = uiState.appFont,
                isLightTheme = uiState.appColor.isLight,
            ) {
                LaunchedEffect(uiState.appColor.isLight) {
                    navController.currentBackStackEntryFlow
                        .filter { entry ->
                            val hasNoteViewRoute = entry.destination.hasRoute<NoteViewRoute>()
                            val hasNoteCreateRoute = entry.destination.hasRoute<NoteCreateRoute>()
                            !hasNoteViewRoute && !hasNoteCreateRoute
                        }
                        .collect { entry ->
                            val hasNoteViewRoute = entry.destination.hasRoute<NoteViewRoute>() ||
                                    entry.destination.hasRoute<NoteCreateRoute>()
                            val hasMediaViewRoute = entry.destination.hasRoute<MediaViewRoute>() ||
                                    entry.destination.hasRoute<MediaViewerRoute>()
                            val color = Color.Transparent.toArgb()
                            when {
                                hasMediaViewRoute -> activity.enableEdgeToEdge(
                                    statusBarStyle = SystemBarStyle.dark(color),
                                    navigationBarStyle = SystemBarStyle.dark(color),
                                )

                                !hasNoteViewRoute ->  if (uiState.appColor.isLight) {
                                    activity.enableEdgeToEdge(
                                        statusBarStyle = SystemBarStyle.light(
                                            scrim = color,
                                            darkScrim = color
                                        ),
                                        navigationBarStyle = SystemBarStyle.light(
                                            scrim = color,
                                            darkScrim = color
                                        ),
                                    )
                                }
                            }
                        }
                }

                val currentEntry by navController.currentBackStackEntryFlow
                    .collectAsState(null)
                val currentDestination = currentEntry?.destination
                val hasMediaRoute = currentDestination?.hasRoute<MediaViewRoute>() == true ||
                        currentDestination?.hasRoute<MediaViewerRoute>() == true
                val hasMediaSortingRoute = currentDestination?.hasRoute<MediaSortingRoute>() == true

                CompositionLocalProvider(
                    LocalAuth provides this,
                    LocalHasMediaRoute provides hasMediaRoute,
                    LocalHasMediaSortingRoute provides hasMediaSortingRoute,
                ) {
                    NavHost(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .hazeSource(hazeState),
                        navController = navController,
                        startDestination = NoteListRoute,
                        enterTransition = { defaultEnterTransition() },
                        exitTransition = { defaultExitTransition() },
                        popExitTransition = { defaultPopExitTransition() },
                        popEnterTransition = { defaultPopEnterTransition() },
                    ) {
                        noteListScreen(
                            hasSearchScreenRoute = { it.hasRoute<NoteSearchRoute>() },
                            hasNoteCreateScreenRoute = { it.hasRoute<NoteCreateRoute>() },
                            openSettingsScreen = navController::navigateToSettings,
                            openNoteCreateScreen = { identifier ->
                                navController.navigateToNoteCreate(
                                    route = NoteCreateRoute(
                                        dialogId = identifier.dialogId,
                                        requestId = identifier.requestId,
                                    ),
                                )
                            },
                            openNoteViewScreen = { noteId, identifier ->
                                navController.navigateToNoteView(
                                    route = NoteViewRoute(
                                        noteId = noteId,
                                        dialogId = identifier.dialogId,
                                        requestId = identifier.requestId,
                                    ),
                                )
                            },
                            openNoteSearchScreen = {
                                navController.navigateToNoteSearch()
                            }
                        )

                        noteViewScreen(
                            openMediaViewScreen = { noteId, mediaId, identifier ->
                                navController.navigateToMediaView(
                                    route = MediaViewRoute(
                                        noteId = noteId,
                                        mediaId = mediaId,
                                        dialogId = identifier.dialogId,
                                        requestId = identifier.requestId,
                                    ),
                                )
                            },
                            openMediaSortingScreen = { noteId, mediaBlockId, identifier ->
                                navController.navigateToMediaSorting(
                                    route = MediaSortingRoute(
                                        noteId = noteId,
                                        mediaBlockId = mediaBlockId,
                                        dialogId = identifier.dialogId,
                                        requestId = identifier.requestId,
                                    )
                                )
                            },
                            openMediaViewer = navController::navigateToMediaViewer,
                            onCloseRequest = navController::navigateUp,
                        )

                        noteCreateScreen(
                            openMediaViewScreen = { noteId, mediaId, identifier ->
                                navController.navigateToMediaView(
                                    route = MediaViewRoute(
                                        noteId = noteId,
                                        mediaId = mediaId,
                                        dialogId = identifier.dialogId,
                                        requestId = identifier.requestId,
                                    ),
                                )
                            },
                            openMediaSortingScreen = { noteId, mediaBlockId, identifier ->
                                navController.navigateToMediaSorting(
                                    route = MediaSortingRoute(
                                        noteId = noteId,
                                        mediaBlockId = mediaBlockId,
                                        dialogId = identifier.dialogId,
                                        requestId = identifier.requestId,
                                    )
                                )
                            },
                            openMediaViewer = navController::navigateToMediaViewer,
                            onCloseRequest = navController::navigateUp,
                        )

                        settingsNavigation(navController)
                        mediaViewScreen(onCloseRequest = navController::navigateUp)
                        mediaViewerScreen(onCloseRequest = navController::navigateUp)
                        mediaSortingScreen(
                            openMediaViewScreen = { noteId, mediaId, mediaBlockId, identifier ->
                                navController.navigateToMediaView(
                                    route = MediaViewRoute(
                                        noteId = noteId,
                                        mediaBlockId = mediaBlockId,
                                        mediaId = mediaId,
                                        dialogId = identifier.dialogId,
                                        requestId = identifier.requestId,
                                    ),
                                )
                            },
                            openMediaViewer = navController::navigateToMediaViewer,
                            onCloseRequest = navController::navigateUp
                        )
                        noteSearchScreen(
                            openNoteViewScreen = { noteId, identifier, data ->
                                navController.navigateToNoteView(
                                    route = NoteViewRoute(
                                        noteId = noteId,
                                        dialogId = identifier.dialogId,
                                        requestId = identifier.requestId,
                                        searchData = NoteViewRoute.SearchData(
                                            query = data.query,
                                            tags = data.tags,
                                            startDate = data.startDate,
                                            endDate = data.endDate,
                                        ),
                                    ),
                                )
                            },
                            onCloseRequest = navController::navigateUp
                        )
                    }
                    AnimatedVisibility(
                        visible = uiState.isScreenLocked,
                        enter = EnterTransition.None,
                        exit = fadeOut(),
                    ) {
                        CheckPinScreen(
                            hazeState = hazeState,
                            onCloseRequest = { viewModel.onEvent(MainEvent.OnUnlockScreenRequest) },
                        )
                    }
                }
            }
        }
    }
}