package com.furianrt.onboarding.internal.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.onboarding.R
import com.furianrt.onboarding.internal.ui.theme.model.ThemeTab
import com.furianrt.uikit.entities.UiThemeColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class ThemeViewModel @Inject constructor(
    appearanceRepository: AppearanceRepository,
    resourcesManager: ResourcesManager,
    dispatchers: DispatchersProvider,
) : ViewModel() {

    private val themeTabs = listOf(
        ThemeTab.Dark(resourcesManager.getString(R.string.onboarding_theme_dark_title)),
        ThemeTab.Light(resourcesManager.getString(R.string.onboarding_theme_light_title)),
    )

    private val themes = UiThemeColor.getDarkThemesList() + UiThemeColor.getLightThemesList()

    val state: StateFlow<ThemeState> = appearanceRepository.getAppThemeColorId()
        .map(::buildState)
        .flowOn(dispatchers.default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThemeState.Loading,
        )

    private val _effect = MutableSharedFlow<ThemeEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<ThemeEffect> = _effect.asSharedFlow()

    fun onEvent(event: ThemeEvent) {
        when (event) {
            is ThemeEvent.OnThemeTabClick -> {
                val themeIndex = themes.indexOfFirst { theme ->
                    if (event.theme.isLight) {
                        !theme.isLight
                    } else {
                        theme.isLight
                    }
                }
                _effect.tryEmit(ThemeEffect.ScrollToTheme(themeIndex))
            }
        }
    }

    private fun buildState(
        appThemeColorId: String?,
    ): ThemeState {
        val appTheme = UiThemeColor.fromId(appThemeColorId)
        return ThemeState.Success(
            initialPageIndex = themes.indexOf(appTheme),
            themes = themes,
            tabs = themeTabs.map { it.title },
        )
    }
}