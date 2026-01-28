package com.furianrt.toolspanel.internal.ui.background.container

import com.furianrt.notelistui.entities.UiNoteTheme
import kotlinx.coroutines.flow.StateFlow

internal interface BackgroundSelectedThemeProvider {
    val selectedThemeState: StateFlow<UiNoteTheme?>
}