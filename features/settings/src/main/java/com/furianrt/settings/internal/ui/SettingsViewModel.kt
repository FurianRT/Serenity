package com.furianrt.settings.internal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.common.BuildInfoProvider
import com.furianrt.core.DispatchersProvider
import com.furianrt.core.doWithState
import com.furianrt.domain.entities.AppLocale
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.domain.repositories.DeviceInfoRepository
import com.furianrt.domain.repositories.LocaleRepository
import com.furianrt.notelistui.extensions.toNoteFontFamily
import com.furianrt.notelistui.extensions.toUiNoteFontFamily
import com.furianrt.settings.BuildConfig
import com.furianrt.settings.R
import com.furianrt.settings.internal.domain.SettingsRepository
import com.furianrt.settings.internal.domain.usecases.GetAppDarkThemeListUseCase
import com.furianrt.settings.internal.domain.usecases.GetAppLightThemeListUseCase
import com.furianrt.settings.internal.ui.entities.UiTheme
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val MIN_GOOD_RATING = 4
private const val RATING_CLICK_DELAY = 250L
private const val PRIVACY_POLICY_LINK = "https://sites.google.com/view/serenityapp/privacy-policy"
private const val TERMS_AND_CONDITIONS_LINK =
    "https://sites.google.com/view/serenityapp/terms-and-conditions"

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    dispatchers: DispatchersProvider,
    private val getAppDarkThemeListUseCase: GetAppDarkThemeListUseCase,
    private val getAppLightThemeListUseCase: GetAppLightThemeListUseCase,
    private val settingsRepository: SettingsRepository,
    private val appearanceRepository: AppearanceRepository,
    private val deviceInfoRepository: DeviceInfoRepository,
    private val localeRepository: LocaleRepository,
    private val buildInfoProvider: BuildInfoProvider,
    private val resourcesManager: ResourcesManager,
) : ViewModel() {

    private val selectedTheme = MutableStateFlow<UiTheme?>(null)

    val state: StateFlow<SettingsUiState> = combine(
        selectedTheme,
        appearanceRepository.getAppThemeColorId(),
        settingsRepository.getAppRating(),
        localeRepository.getSelectedLocale(),
        ::buildState,
    ).flowOn(
        context = dispatchers.default,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState.Loading,
    )

    private val _effect = MutableSharedFlow<SettingsEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<SettingsEffect> = _effect.asSharedFlow()

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

            is SettingsEvent.OnAppThemeSelected -> selectedTheme.update { event.theme }
            is SettingsEvent.OnButtonFeedbackClick -> sendFeedback()
            is SettingsEvent.OnButtonReportIssueClick -> sendIssueFeedback()
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

            is SettingsEvent.OnButtonFontClick -> launch { showFontDialog() }
            is SettingsEvent.OnFontSelected -> launch {
                appearanceRepository.setAppFont(event.font.toNoteFontFamily())
            }

            is SettingsEvent.OnButtonTermsAndConditionsClick -> {
                _effect.tryEmit(SettingsEffect.OpenLink(TERMS_AND_CONDITIONS_LINK))
            }

            is SettingsEvent.OnButtonPrivacyPolicyClick -> {
                _effect.tryEmit(SettingsEffect.OpenLink(PRIVACY_POLICY_LINK))
            }

            is SettingsEvent.OnLocaleClick -> launch {
                state.doWithState<SettingsUiState.Success> { successState ->
                    _effect.tryEmit(
                        SettingsEffect.ShowLocaleDialog(
                            locale = localeRepository.getLocaleList().first(),
                            selectedLocale = successState.locale,
                        )
                    )
                }
            }

            is SettingsEvent.OnLocaleSelected -> localeRepository.setSelectedLocale(event.locale)
            is SettingsEvent.OnButtonNoteSettingsClick -> {
                _effect.tryEmit(SettingsEffect.OpenNoteSettingsScreen)
            }
        }
    }

    private fun sendFeedback() {
        _effect.tryEmit(
            SettingsEffect.SendFeedbackEmail(
                supportEmail = BuildConfig.SUPPORT_EMAIL,
                text = resourcesManager.getString(
                    R.string.settings_feedback_email_subject,
                    deviceInfoRepository.getDeviceInfoText(),
                )
            )
        )
    }

    private fun sendIssueFeedback() {
        _effect.tryEmit(
            SettingsEffect.SendFeedbackEmail(
                supportEmail = BuildConfig.SUPPORT_EMAIL,
                text = resourcesManager.getString(
                    R.string.settings_feedback_issue_email_subject,
                    deviceInfoRepository.getDeviceInfoText(),
                )
            )
        )
    }

    private suspend fun showFontDialog() {
        _effect.tryEmit(
            SettingsEffect.ShowFontDialog(
                fonts = appearanceRepository.getNoteFontsList()
                    .map(NoteFontFamily::toUiNoteFontFamily),
                selectedFont = appearanceRepository.getAppFont()
                    .map(NoteFontFamily::toUiNoteFontFamily)
                    .first(),
            )
        )
    }

    private fun buildState(
        selectedTheme: UiTheme?,
        selectedThemeColorId: String?,
        rating: Int,
        locale: AppLocale,
    ): SettingsUiState {
        val selectedThemeColor = UiThemeColor.fromId(selectedThemeColorId)
        val lightColors = getAppLightThemeListUseCase()
        val darkColors = getAppDarkThemeListUseCase()
        return SettingsUiState.Success(
            themes = listOf(
                UiTheme.Light(
                    isSelected = if (selectedTheme == null) {
                        lightColors.contains(selectedThemeColor)
                    } else {
                        selectedTheme is UiTheme.Light
                    },
                    colors = lightColors,
                    selectedColor = selectedThemeColor,
                ),
                UiTheme.Dark(
                    isSelected = if (selectedTheme == null) {
                        darkColors.contains(selectedThemeColor)
                    } else {
                        selectedTheme is UiTheme.Dark
                    },
                    colors = darkColors,
                    selectedColor = selectedThemeColor,
                ),
            ),
            rating = rating,
            appVersion = buildInfoProvider.getAppVersionName(),
            locale = locale,
        )
    }
}
