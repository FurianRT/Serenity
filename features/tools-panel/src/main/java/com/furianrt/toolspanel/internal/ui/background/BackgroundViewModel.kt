package com.furianrt.toolspanel.internal.ui.background

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.toolspanel.api.NoteBackgroundProvider
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = BackgroundViewModel.Factory::class)
internal class BackgroundViewModel @AssistedInject constructor(
    backgroundProvider: NoteBackgroundProvider,
    @Assisted private val initialBackground: UiNoteBackground?,
) : ViewModel() {

    private val selectedPageIndex = MutableStateFlow(0)
    private val selectedBackground = MutableStateFlow(initialBackground)

    val state: StateFlow<BackgroundPanelUiState> = combine(
        flowOf(backgroundProvider.getDarkBackgrounds()),
        flowOf(backgroundProvider.getLightBackgrounds()),
        selectedBackground,
        selectedPageIndex,
        ::buildState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BackgroundPanelUiState(),
    )

    private val _effect = MutableSharedFlow<BackgroundPanelEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<BackgroundPanelEffect> = _effect.asSharedFlow()

    fun onEvent(event: BackgroundPanelEvent) {
        when (event) {
            is BackgroundPanelEvent.OnCloseClick -> {
                _effect.tryEmit(BackgroundPanelEffect.ClosePanel)
            }

            is BackgroundPanelEvent.OnBackgroundSelected -> {
                val background = if (selectedBackground.value == event.background) {
                    null
                } else {
                    event.background
                }
                selectedBackground.update { background }
                _effect.tryEmit(BackgroundPanelEffect.SelectBackground(background))
            }

            is BackgroundPanelEvent.OnContentPageChange -> selectedPageIndex.update {
                event.index
            }

            is BackgroundPanelEvent.OnKeyboardClick -> {
                _effect.tryEmit(BackgroundPanelEffect.ShowKeyboard)
            }

            is BackgroundPanelEvent.OnTitleTabClick -> {
                selectedPageIndex.update { event.index }
                _effect.tryEmit(BackgroundPanelEffect.ScrollContentToIndex(event.index))
            }

            is BackgroundPanelEvent.OnClearBackgroundClick -> {
                selectedBackground.update { null }
                _effect.tryEmit(BackgroundPanelEffect.SelectBackground(null))
            }
        }
    }

    private fun buildState(
        dark: List<UiNoteBackground>,
        light: List<UiNoteBackground>,
        background: UiNoteBackground?,
        selectedTabIndex: Int,
    ) = BackgroundPanelUiState(
        tabs = listOf(
            BackgroundPanelUiState.Tab.All(items = dark + light),
            BackgroundPanelUiState.Tab.Dark(items = dark),
            BackgroundPanelUiState.Tab.Light(items = light),
        ),
        selectedTabIndex = selectedTabIndex,
        selectedBackground = background,
    )

    @AssistedFactory
    interface Factory {
        fun create(
            initialBackground: UiNoteBackground?,
        ): BackgroundViewModel
    }
}