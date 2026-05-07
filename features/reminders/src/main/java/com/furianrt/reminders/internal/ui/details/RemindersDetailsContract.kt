package com.furianrt.reminders.internal.ui.details

import com.furianrt.reminders.internal.ui.entities.DayItem
import com.furianrt.uikit.entities.UiThemeColor
import java.time.LocalTime

internal data class RemindersDetailsUiState(
    val theme: UiThemeColor,
    val content: Content,
) {
    internal sealed interface Content {
        data object Loading : Content
        data class Success(
            val initialTime: LocalTime,
            val notificationText: String,
            val daysOfWeek: List<DayItem>,
        ) : Content
    }
}

internal sealed interface RemindersDetailsEvent {
    data class OnDayClick(val day: DayItem) : RemindersDetailsEvent
    data object OnSaveClick : RemindersDetailsEvent
    data object OnEnterNotificationTextClick : RemindersDetailsEvent
    data class OnNotificationTextEntered(val text: String) : RemindersDetailsEvent
    data class OnTimeSelected(val time: LocalTime) : RemindersDetailsEvent
    data object OnCloseScreenClick : RemindersDetailsEvent
}

internal sealed interface RemindersDetailsEffect {
    data class ShowNotificationTextDialog(val text: String) : RemindersDetailsEffect
    data object PerformTimeHaptic : RemindersDetailsEffect
    data object CloseScreen : RemindersDetailsEffect
}
