package com.furianrt.onboarding.internal.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.entities.AppTheme
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.uikit.entities.UiAppTheme
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.entities.toUiAppTheme
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
        (UiThemeColor.getLightThemesList() +
                UiThemeColor.getDarkThemesList().reversed() +
                UiThemeColor.getPictureThemes()).map { theme ->
            UiAppTheme.Regular(
                color = theme,
            )
        }
    }

    val state: StateFlow<ThemeState> = appearanceRepository.getAppTheme()
        .map(::buildState)
        .flowOn(dispatchers.default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThemeState(
                theme = appearanceRepository.getAppTheme().value.toUiAppTheme(),
                content = ThemeState.Content.Loading,
            ),
        )

    private fun buildState(
        appTheme: AppTheme,
    ): ThemeState {
        val theme = appTheme.toUiAppTheme()
        return ThemeState(
            theme = theme,
            content = ThemeState.Content.Success(
                initialPageIndex = themes.indexOfFirst { it.id == theme.id },
                themes = themes,
            ),
        )
    }
}