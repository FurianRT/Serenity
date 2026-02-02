package com.furianrt.toolspanel.internal.ui.background.image

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

@HiltViewModel(assistedFactory = ImageBackgroundViewModel.Factory::class)
internal class ImageBackgroundViewModel @AssistedInject constructor(
    noteThemeProvider: NoteThemeProvider,
    @Assisted private val selectedThemeProvider: BackgroundSelectedThemeProvider,
) : ViewModel() {

    private val themes: List<UiNoteTheme.Image.Picture> = noteThemeProvider.getPictureThemes()

    val state: StateFlow<ImageBackgroundUiState> = selectedThemeProvider.selectedThemeState
        .map(::buildState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = buildState(
                selectedTheme = selectedThemeProvider.selectedThemeState.value,
            ),
        )

    private val _effect = MutableSharedFlow<ImageBackgroundEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<ImageBackgroundEffect> = _effect.asSharedFlow()

    fun onEvent(event: ImageBackgroundEvent) {
        when (event) {
            is ImageBackgroundEvent.OnThemeSelected -> {
                val currentThemeId = selectedThemeProvider.selectedThemeState.value?.imageId
                val theme = event.theme.takeIf { currentThemeId != event.theme.imageId }
                _effect.tryEmit(ImageBackgroundEffect.OnThemeSelected(theme))
            }

            is ImageBackgroundEvent.OnClearBackgroundClick -> {
                _effect.tryEmit(ImageBackgroundEffect.OnThemeSelected(null))
            }
        }
    }

    private fun buildState(
        selectedTheme: UiNoteTheme?,
    ) = ImageBackgroundUiState(
        themes = themes,
        selectedThemeIndex = if (selectedTheme is UiNoteTheme.Image.Picture) {
            themes.indexOfFirstOrNull { it.colorId == selectedTheme.colorId }
        } else {
            null
        },
    )

    @AssistedFactory
    interface Factory {
        fun create(
            selectedThemeProvider: BackgroundSelectedThemeProvider,
        ): ImageBackgroundViewModel
    }
}