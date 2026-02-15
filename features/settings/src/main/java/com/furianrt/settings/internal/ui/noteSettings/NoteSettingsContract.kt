package com.furianrt.settings.internal.ui.noteSettings

import com.furianrt.uikit.entities.UiThemeColor

internal data class NoteSettingsState(
    val theme: UiThemeColor,
    val content: Content,
) {
    sealed interface Content {
        data object Loading : Content
        data class Success(
            val isAutoDetectLocationEnabled: Boolean,
            val isMinimalisticHomeScreenEnabled: Boolean,
        ) : Content
    }
}

internal sealed interface NoteSettingsEffect {
    data object CloseScreen : NoteSettingsEffect
}

internal sealed interface NoteSettingsEvent {
    data object OnButtonBackClick : NoteSettingsEvent
    data class OnEnableAutoDetectLocationChanged(val isEnabled: Boolean) : NoteSettingsEvent
    data class OnEnableMinimalisticHomeScreenChanged(val isEnabled: Boolean) : NoteSettingsEvent
}
