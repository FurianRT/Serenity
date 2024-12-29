package com.furianrt.toolspanel.internal.font

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.NoteFontColor
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.extensions.toUiNoteFontColor
import com.furianrt.notelistui.extensions.toUiNoteFontFamily
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = FontViewModel.Factory::class)
internal class FontViewModel @AssistedInject constructor(
    private val appearanceRepository: AppearanceRepository,
    @Assisted private val initialFontFamily: UiNoteFontFamily,
    @Assisted private val initialFontColor: UiNoteFontColor,
) : ViewModel() {

    private val selectedFontFamily = MutableStateFlow(initialFontFamily)
    private val selectedFontColor = MutableStateFlow(initialFontColor)

    val state = combine(
        selectedFontFamily,
        selectedFontColor,
    ) { fontFamily, fontColor ->
        buildState(
            fontFamilies = appearanceRepository.getNoteFontsList(),
            fontColors = appearanceRepository.getNoteFontColorsList(),
            fontFamily = fontFamily,
            fontColor = fontColor,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FontPanelUiState(
            selectedFontFamily = initialFontFamily,
            selectedFontColor = initialFontColor,
            fontFamilies = persistentListOf(initialFontFamily),
            fontColors = persistentListOf(initialFontColor),
        ),
    )

    fun onEvent(event: FontPanelEvent) {
        when (event) {
            is FontPanelEvent.OnFontFamilySelected -> selectedFontFamily.update { event.family }
            is FontPanelEvent.OnFontColorSelected -> selectedFontColor.update { event.color }
        }
    }

    private fun buildState(
        fontFamilies: List<NoteFontFamily>,
        fontColors: List<NoteFontColor>,
        fontFamily: UiNoteFontFamily,
        fontColor: UiNoteFontColor,
    ) = FontPanelUiState(
        selectedFontFamily = fontFamily,
        selectedFontColor = fontColor,
        fontFamilies = fontFamilies.mapImmutable(NoteFontFamily::toUiNoteFontFamily),
        fontColors = fontColors.mapImmutable(NoteFontColor::toUiNoteFontColor),
    )

    @AssistedFactory
    interface Factory {
        fun create(
            initialFontFamily: UiNoteFontFamily,
            initialFontColor: UiNoteFontColor,
        ): FontViewModel
    }
}