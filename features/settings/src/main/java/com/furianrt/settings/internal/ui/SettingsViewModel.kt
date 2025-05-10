package com.furianrt.settings.internal.ui

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.settings.BuildConfig
import com.furianrt.settings.internal.domain.GetAppThemeListUseCase
import com.furianrt.settings.internal.entities.AppTheme
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val appearanceRepository: AppearanceRepository,
    private val getAppThemeListUseCase: GetAppThemeListUseCase,
) : ViewModel() {

    val state = appearanceRepository.getAppThemeColorId()
        .map { colorId ->
            buildState(
                themes = getAppThemeListUseCase(),
                selectedThemeColor = UiThemeColor.fromId(colorId),
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

            is SettingsEvent.OnButtonBackupClick -> {
                _effect.tryEmit(SettingsEffect.OpenBackupScreen)
            }

            is SettingsEvent.OnAppThemeColorSelected -> launch {
                appearanceRepository.updateAppThemeColor(event.color.id)
            }

            is SettingsEvent.OnButtonFeedbackClick -> {
                _effect.tryEmit(
                    SettingsEffect.SendFeedbackEmail(
                        supportEmail = BuildConfig.SUPPORT_EMAIL,
                        androidVersion = Build.VERSION.SDK_INT,
                        language = Locale.getDefault().language,
                        device = Build.MODEL,
                    )
                )
            }
        }
    }

    private fun buildState(
        themes: ImmutableList<AppTheme>,
        selectedThemeColor: UiThemeColor,
    ) = SettingsUiState.Success(
        themes = themes,
        selectedThemeColor = selectedThemeColor,
    )
}
