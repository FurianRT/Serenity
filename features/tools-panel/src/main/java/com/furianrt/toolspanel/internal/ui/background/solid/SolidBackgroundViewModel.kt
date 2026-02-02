package com.furianrt.toolspanel.internal.ui.background.solid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.toolspanel.api.NoteThemeProvider
import com.furianrt.toolspanel.internal.ui.background.container.BackgroundSelectedThemeProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = SolidBackgroundViewModel.Factory::class)
internal class SolidBackgroundViewModel @AssistedInject constructor(
    noteThemeProvider: NoteThemeProvider,
    @Assisted private val selectedThemeProvider: BackgroundSelectedThemeProvider,
) : ViewModel() {

    private val themes: List<UiNoteTheme.Solid> = noteThemeProvider.getSolidThemes()

    val state: StateFlow<SolidBackgroundUiState> = selectedThemeProvider.selectedThemeState
        .map(::buildState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = buildState(
                selectedTheme = selectedThemeProvider.selectedThemeState.value,
            ),
        )

    private val _effect = MutableSharedFlow<SolidBackgroundEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<SolidBackgroundEffect> = _effect.asSharedFlow()

    fun onEvent(event: SolidBackgroundEvent) {
        when (event) {
            is SolidBackgroundEvent.OnThemeSelected -> {
                val currentThemeId = selectedThemeProvider.selectedThemeState.value?.colorId
                val theme = event.theme.takeIf { currentThemeId != event.theme.colorId }
                _effect.tryEmit(SolidBackgroundEffect.OnThemeSelected(theme))
            }

            is SolidBackgroundEvent.OnClearBackgroundClick -> {
                _effect.tryEmit(SolidBackgroundEffect.OnThemeSelected(null))
            }
        }
    }

    private fun buildState(
        selectedTheme: UiNoteTheme?,
    ) = SolidBackgroundUiState(
        themes = themes,
        selectedThemeIndex = if (selectedTheme is UiNoteTheme.Solid) {
            themes.indexOfFirstOrNull { it.colorId == selectedTheme.colorId }
        } else {
            null
        },
    )

    @AssistedFactory
    interface Factory {
        fun create(
            selectedThemeProvider: BackgroundSelectedThemeProvider,
        ): SolidBackgroundViewModel
    }
}