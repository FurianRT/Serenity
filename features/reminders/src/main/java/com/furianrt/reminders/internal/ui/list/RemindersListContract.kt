package com.furianrt.reminders.internal.ui.list

import com.furianrt.reminders.internal.ui.list.entities.ReminderItem
import com.furianrt.uikit.entities.UiThemeColor

internal data class RemindersListUiState(
    val theme: UiThemeColor,
    val content: Content,
) {
    sealed interface Content {
        data object Loading : Content
        data object Empty : Content
        data class Success(
            val reminders: List<ReminderItem>,
        ) : Content
    }
}

internal sealed interface RemindersListEvent {
    data object OnTroubleShootingClick : RemindersListEvent
    data object OnAddReminderClick : RemindersListEvent
    data object OnCloseScreenClick : RemindersListEvent
    data class OnReminderClick(val reminder: ReminderItem) : RemindersListEvent
    data class OnDeleteReminderClick(val reminder: ReminderItem) : RemindersListEvent
    data object OnNotificationsPermissionSelected : RemindersListEvent
}

internal sealed interface RemindersListEffect {
    data object CloseScreen : RemindersListEffect
    data object OpenTroubleShootingScreen : RemindersListEffect
    data class OpenReminderDetailsScreen(val reminderId: String?) : RemindersListEffect
    data object RequestNotificationsPermission : RemindersListEffect
    data object ShowNotificationsPermissionsDeniedDialog : RemindersListEffect
    data object OpenAlarmsSettingsScreen : RemindersListEffect
}
