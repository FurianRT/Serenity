package com.furianrt.toolspanel.internal.ui.background.custom

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.domain.entities.NoteCustomBackground
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediaselector.api.MediaSelectorState
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.toolspanel.internal.ui.background.container.BackgroundSelectedThemeProvider
import com.furianrt.toolspanel.internal.ui.background.custom.extensions.toNoteCustomBackground
import com.furianrt.toolspanel.internal.ui.background.custom.extensions.toUiNoteTheme
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.utils.detectTheme
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = CustomBackgroundViewModel.Factory::class)
internal class CustomBackgroundViewModel @AssistedInject constructor(
    private val mediaRepository: MediaRepository,
    @Assisted private val selectedThemeProvider: BackgroundSelectedThemeProvider,
) : ViewModel() {

    val state: StateFlow<CustomBackgroundUiState> = combine(
        mediaRepository.getCustomNoteBackgrounds(),
        selectedThemeProvider.selectedThemeState,
        ::buildState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CustomBackgroundUiState.Loading,
    )

    private val _effect = MutableSharedFlow<CustomBackgroundEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<CustomBackgroundEffect> = _effect.asSharedFlow()

    fun onEvent(event: CustomBackgroundEvent) {
        when (event) {
            is CustomBackgroundEvent.OnSelectImageClick -> onSelectImageClick()
            is CustomBackgroundEvent.OnClearBackgroundClick -> onClearBackgroundClick()
            is CustomBackgroundEvent.OnThemeSelected -> onThemeSelected(event.theme)
            is CustomBackgroundEvent.OnDeleteThemeClick -> onDeleteThemeClick(event.theme)
        }
    }

    private fun onThemeSelected(theme: UiNoteTheme.Image.Picture) {
        val currentThemeId = selectedThemeProvider.selectedThemeState.value?.imageId
        val theme = theme.takeIf { currentThemeId != theme.imageId }
        _effect.tryEmit(CustomBackgroundEffect.OnThemeSelected(theme))
    }

    private fun onClearBackgroundClick() {
        _effect.tryEmit(CustomBackgroundEffect.OnThemeSelected(null))
    }

    private fun onDeleteThemeClick(theme: UiNoteTheme.Image.Picture) {
        launch {
            mediaRepository.deleteCustomNoteBackground(
                background = mediaRepository.getCustomNoteBackgrounds()
                    .first().find { it.id == theme.imageId } ?: return@launch
            )
        }
    }

    private fun onSelectImageClick() {
        val params = MediaSelectorState.Params(
            singleChoice = true,
            allowVideo = false,
            onMediaSelected = ::onMediaSelected,
        )
        _effect.tryEmit(CustomBackgroundEffect.OpenMediaSelector(params))
    }

    private suspend fun onMediaSelected(result: MediaResult) {
        val image = result.media.filterIsInstance<MediaResult.Media.Image>().first()
        val bitmap = mediaRepository.loadBitmapFromUri(image.uri)
        val detectedTheme = bitmap.detectTheme()
        val isLight = !detectedTheme.isDark
        val background = image.toNoteCustomBackground(
            primaryColor = detectedTheme.primary.toArgb(),
            accentColor = detectedTheme.secondary.toArgb(),
            isLight = isLight,
        )
        mediaRepository.insertCustomNoteBackground(background)
        onThemeSelected(background.toUiNoteTheme())
    }

    private fun buildState(
        backgrounds: List<NoteCustomBackground>,
        selectedTheme: UiNoteTheme?,
    ): CustomBackgroundUiState = if (backgrounds.isEmpty()) {
        CustomBackgroundUiState.Empty
    } else {
        CustomBackgroundUiState.Content(
            themes = backgrounds.map(NoteCustomBackground::toUiNoteTheme),
            selectedThemeIndex = if (selectedTheme is UiNoteTheme.Image.Picture) {
                backgrounds.indexOfFirstOrNull { it.id == selectedTheme.imageId }
            } else {
                null
            },
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(
            selectedThemeProvider: BackgroundSelectedThemeProvider,
        ): CustomBackgroundViewModel
    }
}