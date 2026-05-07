package com.furianrt.reminders.internal.ui.help

internal data class RemindersHelpUiState(
    val isNotificationsEnabled: Boolean,
    val hasAlarmsPermission: Boolean,
    val isBatteryOptimizationEnabled: Boolean,
    val isPowerSaveModeEnabled: Boolean,
    val isDndModeEnabled: Boolean,
) {
    val showRebootDeviceHint = isNotificationsEnabled &&
            hasAlarmsPermission &&
            !isBatteryOptimizationEnabled &&
            !isPowerSaveModeEnabled &&
            !isDndModeEnabled
}

internal sealed interface RemindersHelpEvent {
    data object OnCloseScreenClick : RemindersHelpEvent
    data object OnEnableNotificationsClick : RemindersHelpEvent
    data object OnAlarmsPermissionClick : RemindersHelpEvent
    data object OnBatteryOptimizationClick : RemindersHelpEvent
    data object OnPowerSavingModeClick : RemindersHelpEvent
}

internal sealed interface RemindersHelpEffect {
    data object CloseScreen : RemindersHelpEffect
    data object OpenNotificationsSettingsScreen : RemindersHelpEffect
    data object OpenAlarmsSettingsScreen : RemindersHelpEffect
    data object OpenBatteryOptimizationScreen : RemindersHelpEffect
    data object OpenPowerSavingScreen : RemindersHelpEffect
}
