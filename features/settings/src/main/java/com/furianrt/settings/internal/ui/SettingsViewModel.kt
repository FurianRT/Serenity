package com.furianrt.settings.internal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.domain.repositories.DeviceInfoRepository
import com.furianrt.settings.BuildConfig
import com.furianrt.settings.internal.domain.GetAppThemeListUseCase
import com.furianrt.settings.internal.domain.SettingsRepository
import com.furianrt.settings.internal.entities.AppTheme
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val MIN_GOOD_RATING = 4
private const val RATING_CLICK_DELAY = 250L

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    getAppThemeListUseCase: GetAppThemeListUseCase,
    private val settingsRepository: SettingsRepository,
    private val appearanceRepository: AppearanceRepository,
    private val deviceInfoRepository: DeviceInfoRepository,
) : ViewModel() {

    val state = combine(
        getAppThemeListUseCase(),
        appearanceRepository.getAppThemeColorId(),
        settingsRepository.getAppRating(),
        ::buildState,
    ).stateIn(
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

            is SettingsEvent.OnButtonFeedbackClick -> sendFeedback()
            is SettingsEvent.OnRatingSelected -> launch {
                settingsRepository.setAppRating(event.rating)
                delay(RATING_CLICK_DELAY)
                if (event.rating < MIN_GOOD_RATING) {
                    _effect.tryEmit(SettingsEffect.ShowBadRatingDialog)
                } else {
                    _effect.tryEmit(
                        SettingsEffect.OpenMarketPage(url = deviceInfoRepository.getMarketUrl())
                    )
                }
            }
        }
    }

    private fun sendFeedback() {
        _effect.tryEmit(
            SettingsEffect.SendFeedbackEmail(
                supportEmail = BuildConfig.SUPPORT_EMAIL,
                androidVersion = deviceInfoRepository.getAndroidVersion(),
                language = deviceInfoRepository.getDeviceLanguage(),
                device = deviceInfoRepository.getDeviceModel(),
                appVersion = deviceInfoRepository.getAppVersionName(),
            )
        )
    }

    private fun buildState(
        themes: ImmutableList<AppTheme>,
        selectedThemeColorId: String?,
        rating: Int,
    ) = SettingsUiState.Success(
        themes = themes,
        selectedThemeColor = UiThemeColor.fromId(selectedThemeColorId),
        rating = rating,
    )
}
