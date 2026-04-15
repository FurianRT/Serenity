package com.furianrt.toolspanel.internal.ui.font

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.entities.NoteFontColor
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.entities.NoteTextAlignment
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.domain.repositories.DeviceInfoRepository
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.extensions.toNoteFontFamily
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
    @Assisted private val initialTextAlignment: NoteTextAlignment,
) : ViewModel() {

    private val selectedFontFamily = MutableStateFlow(initialFontFamily)
    private val selectedFontColor = MutableStateFlow(initialFontColor)
    private val selectedFontSize = MutableStateFlow(initialFontSize)
    private val selectedTextAlignment = MutableStateFlow(initialTextAlignment)

    val state: StateFlow<FontPanelUiState> = combine(
        selectedFontFamily,
        selectedFontColor,
        selectedFontSize,
        selectedTextAlignment,
        appearanceRepository.getAppFont(),
        ::buildState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = buildState(
            fontFamily = initialFontFamily,
            fontColor = initialFontColor,
            fontSize = initialFontSize,
            textAlignment = initialTextAlignment,
            appFontFamily = initialFontFamily?.toNoteFontFamily(),
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
            is FontPanelEvent.OnTextAlignmentClick -> onTextAlignmentClick()
        }
    }

    private fun onTextAlignmentClick() {
        val newAlignment = when (state.value.textAlignment) {
            NoteTextAlignment.START -> NoteTextAlignment.CENTER
            NoteTextAlignment.CENTER -> NoteTextAlignment.END
            NoteTextAlignment.END -> NoteTextAlignment.START
        }
        selectedTextAlignment.update { newAlignment }
        _effect.tryEmit(FontPanelEffect.SendAlignmentChangeAction(newAlignment))
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
        fontFamily: UiNoteFontFamily?,
        fontColor: UiNoteFontColor?,
        fontSize: Int,
        textAlignment: NoteTextAlignment,
        appFontFamily: NoteFontFamily?,
    ) = FontPanelUiState(
        selectedFontFamily = fontFamily,
        selectedFontColor = fontColor,
        selectedFontSize = fontSize,
        fontFamilies = appearanceRepository.getNoteFontsList()
            .map(NoteFontFamily::toUiNoteFontFamily),
        fontColors = appearanceRepository.getNoteFontColorsList()
            .map(NoteFontColor::toUiNoteFontColor),
        defaultFontFamily = appFontFamily?.toUiNoteFontFamily(),
        textAlignment = textAlignment,
    )

    @AssistedFactory
    interface Factory {
        fun create(
            initialFontFamily: UiNoteFontFamily?,
            initialFontColor: UiNoteFontColor?,
            initialFontSize: Int,
            initialTextAlignment: NoteTextAlignment,
        ): FontViewModel
    }
}