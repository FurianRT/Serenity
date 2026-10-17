package com.furianrt.toolspanel.internal.ui.font

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import codes.side.colorpicker.state.ColorPickerState
import com.furianrt.core.DispatchersProvider
import com.furianrt.core.combine
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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = FontViewModel.Factory::class)
internal class FontViewModel @AssistedInject constructor(
    dispatchers: DispatchersProvider,
    private val appearanceRepository: AppearanceRepository,
    private val resourcesManager: ResourcesManager,
    private val deviceInfoRepository: DeviceInfoRepository,
    @Assisted private val initialFontFamily: UiNoteFontFamily?,
    @Assisted private val initialFontColor: UiNoteFontColor?,
    @Assisted("initialCustomFontColor") private val initialCustomFontColor: Int?,
    @Assisted private val initialFontSize: Int,
    @Assisted private val initialTextAlignment: NoteTextAlignment,
) : ViewModel() {

    private val selectedFontFamily = MutableStateFlow(initialFontFamily)

    private val selectedFontSize = MutableStateFlow(initialFontSize)

    private val selectedTextAlignment = MutableStateFlow(initialTextAlignment)

    private val pickerState =
        ColorPickerState(initialCustomFontColor?.let { Color(it) } ?: Color.White)
    private val showCustomColorSelectorState = MutableStateFlow(false)
    private val selectedFontColorState = MutableStateFlow(
        when {
            initialFontColor != null -> FontPanelUiState.SelectedFontColor.Predefined(
                fontColor = initialFontColor,
            )

            else -> FontPanelUiState.SelectedFontColor.None
        }
    )

    val state: StateFlow<FontPanelUiState> = combine(
        selectedFontFamily,
        selectedFontColorState,
        selectedFontSize,
        selectedTextAlignment,
        showCustomColorSelectorState,
        appearanceRepository.getAppFont(),
        ::buildState,
    ).flowOn(
        context = dispatchers.default,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = buildState(
            showCustomColorSelector = showCustomColorSelectorState.value,
            fontColor = selectedFontColorState.value,
            fontFamily = initialFontFamily,
            fontSize = initialFontSize,
            textAlignment = initialTextAlignment,
            appFontFamily = initialFontFamily?.toNoteFontFamily(),
        ),
    )

    private val _effect = MutableSharedFlow<FontPanelEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<FontPanelEffect> = _effect.asSharedFlow()

    fun onEvent(event: FontPanelEvent) {
        when (event) {
            is FontPanelEvent.OnFontFamilySelected -> onFontFamilySelected(event.family)
            is FontPanelEvent.OnFontColorSelected -> onFontColorSelected(event.color)
            is FontPanelEvent.OnCustomFontColorClick -> onCustomFontColorClick()
            is FontPanelEvent.OnCloseCustomFontSelectorClick -> onCloseCustomFontSelectorClick()
            is FontPanelEvent.OnFontSizeSelected -> onFontSizeSelected(event.size)
            is FontPanelEvent.OnFontSendFeedbackClick -> sendFontFeedback()
            is FontPanelEvent.OnTextAlignmentClick -> onTextAlignmentClick()
            is FontPanelEvent.OnPanelClosed -> onPanelClosed()
        }
    }

    private fun onPanelClosed() {
        showCustomColorSelectorState.update { false }
    }

    private fun onFontColorSelected(color: UiNoteFontColor?) {
        selectedFontColorState.update {
            if (color != null) {
                FontPanelUiState.SelectedFontColor.Predefined(fontColor = color)
            } else {
                FontPanelUiState.SelectedFontColor.None
            }
        }
    }

    private fun onCustomFontColorClick() {
        showCustomColorSelectorState.update { true }
    }

    private fun onCloseCustomFontSelectorClick() {
        showCustomColorSelectorState.update { false }
    }

    private fun onFontFamilySelected(family: UiNoteFontFamily?) {
        selectedFontFamily.update { family }
    }

    private fun onFontSizeSelected(size: Int) {
        selectedFontSize.update { size }
    }

    private fun onTextAlignmentClick() {
        (state.value.content as? FontPanelUiState.Content.Main)?.let { mainContent ->
            val newAlignment = when (mainContent.textAlignment) {
                NoteTextAlignment.START -> NoteTextAlignment.CENTER
                NoteTextAlignment.CENTER -> NoteTextAlignment.END
                NoteTextAlignment.END -> NoteTextAlignment.START
            }
            selectedTextAlignment.update { newAlignment }
            _effect.tryEmit(FontPanelEffect.SendAlignmentChangeAction(newAlignment))
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
        fontFamily: UiNoteFontFamily?,
        fontColor: FontPanelUiState.SelectedFontColor,
        fontSize: Int,
        textAlignment: NoteTextAlignment,
        showCustomColorSelector: Boolean,
        appFontFamily: NoteFontFamily?,
    ) = FontPanelUiState(
        content = if (showCustomColorSelector) {
            FontPanelUiState.Content.ColorPicker(
                pickerState = pickerState,
            )
        } else {
            FontPanelUiState.Content.Main(
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
        },
    )

    @AssistedFactory
    interface Factory {
        fun create(
            initialFontFamily: UiNoteFontFamily?,
            initialFontColor: UiNoteFontColor?,
            @Assisted("initialCustomFontColor") initialCustomFontColor: Int?,
            initialFontSize: Int,
            initialTextAlignment: NoteTextAlignment,
        ): FontViewModel
    }
}