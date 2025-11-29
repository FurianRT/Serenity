package com.furianrt.settings.internal.ui.noteSettings

internal sealed interface NoteSettingsState {
    data object Loading : NoteSettingsState
    data class Success(
        val isAutoDetectLocationEnabled: Boolean,
        val isMinimalisticHomeScreenEnabled: Boolean,
    ) : NoteSettingsState
}

internal sealed interface NoteSettingsEffect {
    data object CloseScreen : NoteSettingsEffect
}

internal sealed interface NoteSettingsEvent {
    data object OnButtonBackClick : NoteSettingsEvent
    data class OnEnableAutoDetectLocationChanged(val isEnabled: Boolean) : NoteSettingsEvent
    data class OnEnableMinimalisticHomeScreenChanged(val isEnabled: Boolean) : NoteSettingsEvent
}
