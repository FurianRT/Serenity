package com.furianrt.apptheme.internal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.extensions.launch
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
internal class AppThemeViewModel @Inject constructor(
    dispatchers: DispatchersProvider,
    private val appearanceRepository: AppearanceRepository,
) : ViewModel() {

    private val themes by lazy {
        UiThemeColor.getPictureThemes() +
                UiThemeColor.getDarkThemesList() +
                UiThemeColor.getLightThemesList()
    }

    val state: StateFlow<AppThemeState> = appearanceRepository.getAppThemeColorId()
        .map(::buildState)
        .flowOn(dispatchers.default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppThemeState(
                theme = UiThemeColor.fromId(appearanceRepository.getAppThemeColorId().value),
                content = AppThemeState.Content.Loading,
            ),
        )

    private val _effect = MutableSharedFlow<AppThemeEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<AppThemeEffect> = _effect.asSharedFlow()

    fun onEvent(event: AppThemeEvent) {
        when (event) {
            is AppThemeEvent.OnThemeClick -> onThemeClick(event.theme)
            is AppThemeEvent.OnBackClick -> onBackClick()
        }
    }

    private fun onThemeClick(theme: UiThemeColor) {
        launch {
            appearanceRepository.updateAppThemeColor(theme.id)
        }
    }

    private fun onBackClick() {
        _effect.tryEmit(AppThemeEffect.CloseScreen)
    }

    private fun buildState(
        appThemeColorId: String?,
    ): AppThemeState {
        val theme = UiThemeColor.fromId(appThemeColorId)
        return AppThemeState(
            theme = theme,
            content = AppThemeState.Content.Success(
                themes = themes,
                selectedId = theme.id,
            ),
        )
    }
}