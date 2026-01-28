package com.furianrt.toolspanel.internal.ui.background.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.toolspanel.internal.ui.background.container.BackgroundContainerUiState.Success.Tab
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

@HiltViewModel(assistedFactory = BackgroundContainerViewModel.Factory::class)
internal class BackgroundContainerViewModel @AssistedInject constructor(
    @Assisted private val noteId: String,
    @Assisted private val initialTheme: UiNoteTheme?,
) : ViewModel(),
    BackgroundSelectedThemeProvider {

    private val tabs = listOf(
        Tab.Picture,
        Tab.Pattern,
        Tab.Solid,
    )

    private val selectedTabState = MutableStateFlow(
        when (initialTheme) {
            is UiNoteTheme.Image.Picture, null -> Tab.Picture
            is UiNoteTheme.Image.Pattern -> Tab.Pattern
            is UiNoteTheme.Solid -> Tab.Solid
        }
    )

    override val selectedThemeState = MutableStateFlow(initialTheme)

    val state: StateFlow<BackgroundContainerUiState> = combine(
        selectedTabState,
        selectedThemeState,
        ::buildState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BackgroundContainerUiState.Loading,
    )

    private val _effect = MutableSharedFlow<BackgroundContainerEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<BackgroundContainerEffect> = _effect.asSharedFlow()

    fun onEvent(event: BackgroundContainerEvent) {
        when (event) {
            is BackgroundContainerEvent.OnCloseClick -> {
                _effect.tryEmit(BackgroundContainerEffect.ClosePanel)
            }

            is BackgroundContainerEvent.OnContentPageChange -> selectedTabState.update {
                tabs[event.index]
            }

            is BackgroundContainerEvent.OnKeyboardClick -> {
                _effect.tryEmit(BackgroundContainerEffect.ShowKeyboard)
            }

            is BackgroundContainerEvent.OnTitleTabClick -> {
                _effect.tryEmit(BackgroundContainerEffect.ScrollToPage(event.index))
            }

            is BackgroundContainerEvent.OnThemeSelected -> selectedThemeState.update {
                event.theme
            }
        }
    }

    private fun buildState(
        selectedTab: Tab,
        selectedTheme: UiNoteTheme?,
    ) = BackgroundContainerUiState.Success(
        noteId = noteId,
        tabs = tabs,
        selectedTabIndex = tabs.indexOf(selectedTab),
        selectedTheme = selectedTheme,
        selectedThemeProvider = this,
    )

    @AssistedFactory
    interface Factory {
        fun create(
            noteId: String,
            initialTheme: UiNoteTheme?,
        ): BackgroundContainerViewModel
    }
}