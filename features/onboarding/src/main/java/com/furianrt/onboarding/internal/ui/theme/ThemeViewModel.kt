package com.furianrt.onboarding.internal.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.onboarding.R
import com.furianrt.onboarding.internal.ui.theme.model.ThemeTab
import com.furianrt.uikit.entities.UiThemeColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class ThemeViewModel @Inject constructor(
    appearanceRepository: AppearanceRepository,
    resourcesManager: ResourcesManager,
) : ViewModel() {

    private val themeTabs = listOf(
        ThemeTab.Dark(resourcesManager.getString(R.string.onboarding_theme_dark_title)),
        ThemeTab.Light(resourcesManager.getString(R.string.onboarding_theme_light_title)),
    )

    private val themes = UiThemeColor.getDarkThemesList() + UiThemeColor.getLightThemesList()

    private val selectedTabState = MutableStateFlow(themeTabs.first { it is ThemeTab.Dark })

    val state: StateFlow<ThemeState> = combine(
        appearanceRepository.getAppThemeColorId(),
        selectedTabState,
        ::buildState,
    ).stateIn(
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
                    if (selectedTabState.value is ThemeTab.Dark) {
                        theme.isLight
                    } else {
                        !theme.isLight
                    }
                }
                _effect.tryEmit(ThemeEffect.ScrollToTheme(themeIndex))
            }

            is ThemeEvent.OnThemeChange -> selectedTabState.update {
                if (event.theme.isLight) {
                    themeTabs.first { it is ThemeTab.Light }
                } else {
                    themeTabs.first { it is ThemeTab.Dark }
                }
            }
        }
    }

    private fun buildState(
        appThemeColorId: String?,
        selectedTab: ThemeTab,
    ): ThemeState {
        val appTheme = UiThemeColor.fromId(appThemeColorId)
        return ThemeState.Success(
            initialPageIndex = themes.indexOf(appTheme),
            themes = themes,
            tabs = themeTabs,
            selectedTab = selectedTab,
        )
    }
}