package com.furianrt.toolspanel.internal.ui.font

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.entities.NoteFontColor
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.domain.repositories.DeviceInfoRepository
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.extensions.toUiNoteFontColor
import com.furianrt.notelistui.extensions.toUiNoteFontFamily
import com.furianrt.toolspanel.BuildConfig
import com.furianrt.toolspanel.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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

@HiltViewModel(assistedFactory = FontViewModel.Factory::class)
internal class FontViewModel @AssistedInject constructor(
    private val appearanceRepository: AppearanceRepository,
    private val resourcesManager: ResourcesManager,
    private val deviceInfoRepository: DeviceInfoRepository,
    @Assisted private val initialFontFamily: UiNoteFontFamily?,
    @Assisted private val initialFontColor: UiNoteFontColor?,
    @Assisted private val initialFontSize: Int,
) : ViewModel() {

    private val selectedFontFamily = MutableStateFlow(initialFontFamily)
    private val selectedFontColor = MutableStateFlow(initialFontColor)
    private val selectedFontSize = MutableStateFlow(initialFontSize)

    val state: StateFlow<FontPanelUiState> = combine(
        selectedFontFamily,
        selectedFontColor,
        selectedFontSize,
        appearanceRepository.getAppFont(),
    ) { fontFamily, fontColor, fontSize, appFont ->
        buildState(
            fontFamilies = appearanceRepository.getNoteFontsList(),
            fontColors = appearanceRepository.getNoteFontColorsList(),
            fontFamily = fontFamily,
            appFontFamily = appFont,
            fontColor = fontColor,
            fontSize = fontSize,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FontPanelUiState(
            selectedFontFamily = initialFontFamily,
            selectedFontColor = initialFontColor,
            selectedFontSize = initialFontSize,
            fontFamilies = emptyList(),
            fontColors = emptyList(),
            defaultFontFamily = initialFontFamily,
        ),
    )

    private val _effect = MutableSharedFlow<FontPanelEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<FontPanelEffect> = _effect.asSharedFlow()

    fun onEvent(event: FontPanelEvent) {
        when (event) {
            is FontPanelEvent.OnFontFamilySelected -> selectedFontFamily.update { event.family }
            is FontPanelEvent.OnFontColorSelected -> selectedFontColor.update { event.color }
            is FontPanelEvent.OnFontSizeSelected -> selectedFontSize.update { event.size }
            is FontPanelEvent.OnFontSendFeedbackClick -> sendFontFeedback()
        }
    }

    private fun sendFontFeedback() {
        _effect.tryEmit(
            FontPanelEffect.SendFeedbackEmail(
                supportEmail = BuildConfig.SUPPORT_EMAIL,
                text = resourcesManager.getString(
                    R.string.font_feedback_email_subject,
                    deviceInfoRepository.getDeviceInfoText(),
                )
            )
        )
    }

    private fun buildState(
        fontFamilies: List<NoteFontFamily>,
        fontColors: List<NoteFontColor>,
        fontFamily: UiNoteFontFamily?,
        appFontFamily: NoteFontFamily,
        fontColor: UiNoteFontColor?,
        fontSize: Int,
    ) = FontPanelUiState(
        selectedFontFamily = fontFamily,
        selectedFontColor = fontColor,
        selectedFontSize = fontSize,
        fontFamilies = fontFamilies.map(NoteFontFamily::toUiNoteFontFamily),
        fontColors = fontColors.map(NoteFontColor::toUiNoteFontColor),
        defaultFontFamily = appFontFamily.toUiNoteFontFamily(),
    )

    @AssistedFactory
    interface Factory {
        fun create(
            initialFontFamily: UiNoteFontFamily?,
            initialFontColor: UiNoteFontColor?,
            initialFontSize: Int,
        ): FontViewModel
    }
}