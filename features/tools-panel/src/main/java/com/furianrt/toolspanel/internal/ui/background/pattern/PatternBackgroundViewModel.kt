package com.furianrt.toolspanel.internal.ui.background.pattern

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.toolspanel.api.NoteThemeProvider
import com.furianrt.toolspanel.internal.ui.background.container.BackgroundSelectedThemeProvider
import com.furianrt.toolspanel.internal.ui.background.pattern.PatternBackgroundEffect.*
import com.furianrt.uikit.entities.UiThemeColor
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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = PatternBackgroundViewModel.Factory::class)
internal class PatternBackgroundViewModel @AssistedInject constructor(
    noteThemeProvider: NoteThemeProvider,
    appearanceRepository: AppearanceRepository,
    @Assisted private val selectedThemeProvider: BackgroundSelectedThemeProvider,
) : ViewModel() {

    private val images = noteThemeProvider.getPatternImages()
    private val colors = noteThemeProvider.getSolidThemes().map(UiNoteTheme.Solid::color)

    private val selectedColorState = MutableStateFlow(
        (selectedThemeProvider.selectedThemeState.value as? UiNoteTheme.Image.Pattern)?.color,
    )

    val state: StateFlow<PatternBackgroundUiState> = combine(
        selectedColorState,
        selectedThemeProvider.selectedThemeState.onEach { theme ->
            selectedColorState.update { colors.find { it.id == theme?.colorId } }
        },
        appearanceRepository.getAppThemeColorId(),
        ::buildState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PatternBackgroundUiState.Loading,
    )

    private val _effect = MutableSharedFlow<PatternBackgroundEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<PatternBackgroundEffect> = _effect.asSharedFlow()

    fun onEvent(event: PatternBackgroundEvent) {
        when (event) {
            is PatternBackgroundEvent.OnClearClick -> {
                selectedColorState.update { null }
                _effect.tryEmit(SendThemeSelected(theme = null))
            }

            is PatternBackgroundEvent.OnImageSelected -> {
                val theme = selectedThemeProvider.selectedThemeState.value
                if (theme?.imageId == event.image.id) {
                    selectedColorState.update { null }
                    _effect.tryEmit(SendThemeSelected(theme = null))
                } else {
                    _effect.tryEmit(
                        SendThemeSelected(
                            theme = UiNoteTheme.Image.Pattern(
                                color = selectedColorState.value,
                                image = event.image,
                            )
                        )
                    )
                }
            }

            is PatternBackgroundEvent.OnColorSelected -> {
                val theme = selectedThemeProvider.selectedThemeState.value
                val image = (theme as? UiNoteTheme.Image.Pattern)?.image
                val color = event.color.takeIf { selectedColorState.value?.id != event.color.id }
                selectedColorState.update { color }
                if (image != null) {
                    _effect.tryEmit(
                        SendThemeSelected(
                            theme = UiNoteTheme.Image.Pattern(
                                color = event.color,
                                image = image,
                            )
                        )
                    )
                }
            }
        }
    }

    private fun buildState(
        selectedColor: UiNoteBackground?,
        selectedTheme: UiNoteTheme?,
        appThemeColorId: String?,
    ) = PatternBackgroundUiState.Success(
        images = images,
        colors = colors,
        selectedImageIndex = images.indexOfFirstOrNull { it.id == selectedTheme?.imageId },
        selectedColorIndex = colors.indexOfFirstOrNull { it.id == selectedColor?.id },
        appTheme = UiThemeColor.fromId(appThemeColorId),
    )

    @AssistedFactory
    interface Factory {
        fun create(
            selectedThemeProvider: BackgroundSelectedThemeProvider,
        ): PatternBackgroundViewModel
    }
}