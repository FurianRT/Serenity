package com.furianrt.serenity

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.furianrt.domain.entities.ThemeColor
import com.furianrt.domain.managers.LockManager
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.lock.api.CheckPinScreen
import com.furianrt.mediaselector.api.mediaViewerScreen
import com.furianrt.mediaselector.api.navigateToMediaViewer
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
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.search.api.NoteSearchRoute
import com.furianrt.search.api.navigateToNoteSearch
import com.furianrt.search.api.noteSearchScreen
import com.furianrt.serenity.extensions.toUiThemeColor
import com.furianrt.settings.api.navigateToSettings
import com.furianrt.settings.api.settingsNavigation
import com.furianrt.uikit.anim.defaultEnterTransition
import com.furianrt.uikit.anim.defaultExitTransition
import com.furianrt.uikit.anim.defaultPopEnterTransition
import com.furianrt.uikit.anim.defaultPopExitTransition
import com.furianrt.uikit.constants.SystemBarsConstants
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.IsAuthorizedProvider
import com.furianrt.uikit.utils.LocalAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val SPLASH_SCREEN_EXIT_ANIM_DURATION = 250L
private const val SPLASH_SCREEN_DELAY = 400L

@AndroidEntryPoint
internal class MainActivity : ComponentActivity(), IsAuthorizedProvider {

    @Inject
    lateinit var permissionsUtils: PermissionsUtils

    @Inject
    lateinit var lockManager: LockManager

    @Inject
    lateinit var appearanceRepository: AppearanceRepository

    private val viewModel: MainViewModel by viewModels()

    private var keepSplashScreen = true

    override suspend fun isAuthorized(): Boolean = lockManager.isAuthorized().first()

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
                    .alpha(0f)
                    .duration = SPLASH_SCREEN_EXIT_ANIM_DURATION

                splashScreenViewProvider.view
                    .animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .duration = SPLASH_SCREEN_EXIT_ANIM_DURATION

                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.dark(SystemBarsConstants.Color.toArgb()),
                    navigationBarStyle = SystemBarStyle.dark(SystemBarsConstants.Color.toArgb()),
                )
            }
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(SystemBarsConstants.Color.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(SystemBarsConstants.Color.toArgb()),
        )

        setContent {
            val uiState by viewModel.state.collectAsStateWithLifecycle()
            val navController = rememberNavController()
            val hazeState = remember { HazeState() }
            val themeColor by appearanceRepository.getAppThemeColor()
                .map(ThemeColor::toUiThemeColor)
                .collectAsStateWithLifecycle(initialValue = UiThemeColor.GREEN)

            SerenityTheme(color = themeColor) {
                CompositionLocalProvider(LocalAuth provides this) {
                    NavHost(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .haze(hazeState),
                        navController = navController,
                        startDestination = NoteListRoute,
                        enterTransition = { defaultEnterTransition() },
                        exitTransition = { defaultExitTransition() },
                        popExitTransition = { defaultPopExitTransition() },
                        popEnterTransition = { defaultPopEnterTransition() },
                    ) {
                        noteListScreen(
                            searchScreenRoute = NoteSearchRoute.javaClass.name,
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
                            openMediaViewScreen = { noteId, mediaName, identifier ->
                                navController.navigateToMediaView(
                                    route = MediaViewRoute(
                                        noteId = noteId,
                                        mediaName = mediaName,
                                        dialogId = identifier.dialogId,
                                        requestId = identifier.requestId,
                                    ),
                                )
                            },
                            openMediaViewer = navController::navigateToMediaViewer,
                            onCloseRequest = navController::navigateUp,
                        )

                        noteCreateScreen(
                            openMediaViewScreen = { noteId, mediaName, identifier ->
                                navController.navigateToMediaView(
                                    route = MediaViewRoute(
                                        noteId = noteId,
                                        mediaName = mediaName,
                                        dialogId = identifier.dialogId,
                                        requestId = identifier.requestId,
                                    ),
                                )
                            },
                            openMediaViewer = navController::navigateToMediaViewer,
                            onCloseRequest = navController::navigateUp,
                        )

                        settingsNavigation(navController)
                        mediaViewScreen(onCloseRequest = navController::navigateUp)
                        mediaViewerScreen(onCloseRequest = navController::navigateUp)
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