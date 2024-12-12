package com.furianrt.settings.internal.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.ThemeColor
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.settings.internal.ui.main.SettingsUiState.Success.AppThemeColor
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val appearanceRepository: AppearanceRepository,
) : ViewModel() {

    val state = appearanceRepository.getAppThemeColor()
        .map { color ->
            buildState(
                themeColors = appearanceRepository.getAppThemeColorsList(),
                selectedThemeColor = color,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState.Loading,
        )

    private val _effect = MutableSharedFlow<SettingsEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnButtonBackClick -> _effect.tryEmit(SettingsEffect.CloseScreen)
            is SettingsEvent.OnButtonSecurityClick -> {
                _effect.tryEmit(SettingsEffect.OpenSecurityScreen)
            }

            is SettingsEvent.OnAppThemeColorSelected -> launch {
                appearanceRepository.updateAppThemeColor(event.color.toThemeColor())
            }
        }
    }

    private fun buildState(
        themeColors: List<ThemeColor>,
        selectedThemeColor: ThemeColor,
    ) = SettingsUiState.Success(
        themeColors = themeColors.mapImmutable(ThemeColor::toUiThemeColor),
        selectedThemeColor = selectedThemeColor.toUiThemeColor(),
    )
}

private fun ThemeColor.toUiThemeColor() = when (this) {
    ThemeColor.BLACK -> AppThemeColor.BLACK
    ThemeColor.GREEN -> AppThemeColor.GREEN
    ThemeColor.BLUE -> AppThemeColor.BLUE
}

private fun AppThemeColor.toThemeColor() = when (this) {
    AppThemeColor.BLACK -> ThemeColor.BLACK
    AppThemeColor.GREEN -> ThemeColor.GREEN
    AppThemeColor.BLUE -> ThemeColor.BLUE
}
