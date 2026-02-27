package com.furianrt.onboarding.internal.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.uikit.entities.UiThemeColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class ThemeViewModel @Inject constructor(
    appearanceRepository: AppearanceRepository,
    dispatchers: DispatchersProvider,
) : ViewModel() {

    private val themes by lazy {
        UiThemeColor.getLightThemesList() +
                UiThemeColor.getDarkThemesList().reversed() +
                UiThemeColor.getPictureThemes()
    }

    val state: StateFlow<ThemeState> = appearanceRepository.getAppThemeColorId()
        .map(::buildState)
        .flowOn(dispatchers.default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThemeState(
                theme = UiThemeColor.fromId(appearanceRepository.getAppThemeColorId().value),
                content = ThemeState.Content.Loading,
            ),
        )

    fun onEvent(event: ThemeEvent) {

    }

    private fun buildState(
        appThemeColorId: String?,
    ): ThemeState {
        val appTheme = UiThemeColor.fromId(appThemeColorId)
        return ThemeState(
            theme = UiThemeColor.fromId(appThemeColorId),
            content = ThemeState.Content.Success(
                initialPageIndex = themes.indexOf(appTheme),
                themes = themes,
            ),
        )
    }
}