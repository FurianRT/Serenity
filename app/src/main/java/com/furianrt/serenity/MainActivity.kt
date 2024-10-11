package com.furianrt.serenity

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.rememberNavController
import com.furianrt.core.orFalse
import com.furianrt.mediaselector.api.MediaSelectorRoute
import com.furianrt.mediaselector.api.mediaSelectorDialog
import com.furianrt.mediaselector.api.mediaViewerScreen
import com.furianrt.mediaselector.api.navigateToMediaSelector
import com.furianrt.mediaselector.api.navigateToMediaViewer
import com.furianrt.mediaview.api.MediaViewRoute
import com.furianrt.mediaview.api.mediaViewScreen
import com.furianrt.mediaview.api.navigateToMediaView
import com.furianrt.notecreate.api.NoteCreateRoute
import com.furianrt.notecreate.api.navigateToNoteCreate
import com.furianrt.notecreate.api.noteCreateScreen
import com.furianrt.notelist.api.noteListScreen
import com.furianrt.noteview.api.NoteViewRoute
import com.furianrt.noteview.api.navigateToNoteView
import com.furianrt.noteview.api.noteViewScreen
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.serenity.navigation.SerenityNavHost
import com.furianrt.settings.api.navigateToSettings
import com.furianrt.settings.api.settingsScreen
import com.furianrt.uikit.constants.SystemBarsConstants
import com.furianrt.uikit.theme.SerenityTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val SPLASH_SCREEN_EXIT_ANIM_DURATION = 250L
private const val SPLASH_SCREEN_DELAY = 400L

@AndroidEntryPoint
internal class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissionsUtils: PermissionsUtils

    private var keepSplashScreen = true

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
            SerenityTheme {
                val navController = rememberNavController()

                LifecycleStartEffect(lifecycleOwner = this, key1 = Unit) {
                    val isMediaSelector = navController.currentDestination
                        ?.hierarchy
                        ?.any { it.hasRoute(MediaSelectorRoute::class) }
                        .orFalse()
                    if (isMediaSelector && permissionsUtils.mediaAccessDenied()) {
                        navController.popBackStack()
                    }
                    onStopOrDispose {}
                }

                SerenityNavHost(navController = navController) {
                    noteListScreen(
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
                        openMediaSelectorScreen = { identifier ->
                            navController.navigateToMediaSelector(
                                route = MediaSelectorRoute(
                                    dialogId = identifier.dialogId,
                                    requestId = identifier.requestId,
                                ),
                            )
                        },
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
                        openMediaSelectorScreen = { identifier ->
                            navController.navigateToMediaSelector(
                                route = MediaSelectorRoute(
                                    dialogId = identifier.dialogId,
                                    requestId = identifier.requestId,
                                ),
                            )
                        },
                        onCloseRequest = navController::navigateUp,
                    )

                    settingsScreen(
                        onCloseRequest = navController::navigateUp,
                    )

                    mediaViewScreen(
                        onCloseRequest = navController::navigateUp,
                    )

                    mediaSelectorDialog(
                        openMediaViewer = navController::navigateToMediaViewer,
                        onCloseRequest = navController::navigateUp,
                    )

                    mediaViewerScreen(
                        onCloseRequest = navController::navigateUp,
                    )
                }
            }
        }
    }
}