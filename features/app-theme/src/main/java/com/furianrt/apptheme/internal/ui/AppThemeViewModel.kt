package com.furianrt.apptheme.internal.ui

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.apptheme.internal.ui.entities.ThemeListItem
import com.furianrt.apptheme.internal.ui.extensions.toAppCustomBackground
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.entities.AppCustomBackground
import com.furianrt.domain.entities.AppTheme
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.uikit.entities.UiAppTheme
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.entities.toUiAppTheme
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.utils.detectTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class AppThemeViewModel @Inject constructor(
    dispatchers: DispatchersProvider,
    private val appearanceRepository: AppearanceRepository,
    private val mediaRepository: MediaRepository,
    private val permissionsUtils: PermissionsUtils,
) : ViewModel() {

    private val regularThemes by lazy {
        (UiThemeColor.getPictureThemes() +
                UiThemeColor.getDarkThemesList() +
                UiThemeColor.getLightThemesList()).map { theme ->
            ThemeListItem.RegularBackground(
                theme = UiAppTheme.Regular(
                    color = theme,
                ),
            )
        }
    }

    val state: StateFlow<AppThemeState> = combine(
        appearanceRepository.getAppCustomBackgrounds(),
        appearanceRepository.getAppTheme(),
        ::buildState,
    ).flowOn(
        context = dispatchers.default
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppThemeState(
            theme = appearanceRepository.getAppTheme().value.toUiAppTheme(),
            content = AppThemeState.Content.Loading,
        ),
    )

    private val _effect = MutableSharedFlow<AppThemeEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<AppThemeEffect> = _effect.asSharedFlow()

    fun onEvent(event: AppThemeEvent) {
        when (event) {
            is AppThemeEvent.OnThemeClick -> onThemeClick(event.theme)
            is AppThemeEvent.OnBackClick -> onBackClick()
            is AppThemeEvent.OnAddClick -> tryRequestMediaPermissions()
            is AppThemeEvent.OnMediaPermissionsSelected -> tryOpenMediaSelector()
            is AppThemeEvent.OnOpenMediaViewerRequest -> onOpenMediaViewerRequest(event.route)
            is AppThemeEvent.OnDeleteBackgroundClick -> onDeleteBackgroundClick(event.id)
        }
    }

    private fun onDeleteBackgroundClick(id: String) = launch {
        appearanceRepository.deleteAppCustomBackground(id)
    }

    private fun onOpenMediaViewerRequest(route: MediaViewerRoute) {
        _effect.tryEmit(AppThemeEffect.OpenMediaViewer(route))
    }

    private fun onThemeClick(theme: UiAppTheme) {
        launch {
            appearanceRepository.updateAppTheme(theme.id)
        }
    }

    private fun onBackClick() {
        _effect.tryEmit(AppThemeEffect.CloseScreen)
    }

    private fun tryRequestMediaPermissions() {
        if (permissionsUtils.mediaAccessDenied()) {
            _effect.tryEmit(AppThemeEffect.RequestStoragePermissions)
        } else {
            _effect.tryEmit(
                AppThemeEffect.OpenMediaSelector(onMediaSelected = { addCustomBackground(it) })
            )
        }
    }

    private fun tryOpenMediaSelector() {
        if (permissionsUtils.mediaAccessDenied()) {
            _effect.tryEmit(AppThemeEffect.ShowPermissionsDeniedDialog)
        } else {
            _effect.tryEmit(
                AppThemeEffect.OpenMediaSelector(onMediaSelected = { addCustomBackground(it) })
            )
        }
    }

    private fun addCustomBackground(result: MediaResult) = launch {
        val image = result.media.filterIsInstance<MediaResult.Media.Image>().first()
        val bitmap = mediaRepository.loadBitmapFromUri(image.uri)
        val detectedTheme = bitmap.detectTheme()
        val isLight = !detectedTheme.isDark
        val background = image.toAppCustomBackground(
            primaryColor = detectedTheme.primary.toArgb(),
            accentColor = detectedTheme.secondary.toArgb(),
            isLight = isLight,
        )
        appearanceRepository.upsertAppCustomBackground(background)
        appearanceRepository.updateAppTheme(background.id)
    }

    private fun buildState(
        customBackgrounds: List<AppCustomBackground>,
        appTheme: AppTheme,
    ): AppThemeState {
        val theme = appTheme.toUiAppTheme()
        return AppThemeState(
            theme = theme,
            content = AppThemeState.Content.Success(
                items = buildList {
                    if (customBackgrounds.isNotEmpty()) {
                        addAll(
                            customBackgrounds.map { background ->
                                ThemeListItem.CustomBackground(
                                    theme = background.toUiAppTheme(),
                                )
                            },
                        )
                        add(ThemeListItem.Hint)
                    }
                    addAll(regularThemes)
                },
                selectedId = theme.id,
            ),
        )
    }
}