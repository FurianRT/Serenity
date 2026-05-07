package com.furianrt.reminders.internal.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.furianrt.domain.entities.Reminder
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.reminders.internal.domain.CreateReminderUseCase
import com.furianrt.reminders.internal.domain.GetReminderUseCase
import com.furianrt.reminders.internal.domain.UpdateReminderUseCase
import com.furianrt.reminders.internal.ui.entities.DayItem
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.extensions.launch
import com.kizitonwose.calendar.core.daysOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
internal class RemindersDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    appearanceRepository: AppearanceRepository,
    getReminderUseCase: GetReminderUseCase,
    private val createReminderUseCase: CreateReminderUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<ReminderDetailsRoute>()

    private val allDaysOfWeek = daysOfWeek()

    private val selectedDaysState = MutableStateFlow(emptySet<DayOfWeek>())
    private val notificationTextState = MutableStateFlow("")
    private var selectedTime = LocalTime.now()

    private var isSaveButtonClicked = false

    private val reminderFlow: Flow<Reminder?> = getReminderUseCase(route.reminderId)
        .take(1)
        .onEach { reminder ->
            selectedDaysState.update { reminder?.daysOfWeek.orEmpty() }
            notificationTextState.update { reminder?.title.orEmpty() }
            selectedTime = reminder?.time ?: selectedTime
        }

    val state: StateFlow<RemindersDetailsUiState> = combine(
        reminderFlow,
        selectedDaysState,
        notificationTextState,
        appearanceRepository.getAppThemeColorId(),
        ::buildState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RemindersDetailsUiState(
            theme = UiThemeColor.fromId(appearanceRepository.getAppThemeColorId().value),
            content = RemindersDetailsUiState.Content.Loading,
        ),
    )

    private val _effect = MutableSharedFlow<RemindersDetailsEffect>(extraBufferCapacity = 5)
    val effect = _effect.asSharedFlow()

    fun onEvent(event: RemindersDetailsEvent) {
        when (event) {
            is RemindersDetailsEvent.OnDayClick -> onDayClick(event.day)
            is RemindersDetailsEvent.OnSaveClick -> onSaveClick()
            is RemindersDetailsEvent.OnCloseScreenClick -> onCloseScreenClick()
            is RemindersDetailsEvent.OnEnterNotificationTextClick -> onEnterNotificationTextClick()
            is RemindersDetailsEvent.OnTimeSelected -> onTimeSelected(event.time)
            is RemindersDetailsEvent.OnNotificationTextEntered -> {
                onNotificationTextEntered(event.text)
            }
        }
    }

    private fun onDayClick(dayItem: DayItem) {
        selectedDaysState.update { selectedDays ->
            if (selectedDays.contains(dayItem.day)) {
                selectedDays.toMutableSet().apply { remove(dayItem.day) }
            } else {
                selectedDays.toMutableSet().apply { add(dayItem.day) }
            }
        }
    }

    private fun onSaveClick() {
        if (isSaveButtonClicked) {
            return
        }
        isSaveButtonClicked = true
        launch {
            if (route.reminderId == null) {
                createReminderUseCase(
                    title = notificationTextState.value,
                    time = selectedTime,
                    daysOfWeek = selectedDaysState.value,
                )
            } else {
                updateReminderUseCase(
                    id = route.reminderId,
                    title = notificationTextState.value,
                    time = selectedTime,
                    daysOfWeek = selectedDaysState.value,
                )
            }
            _effect.tryEmit(RemindersDetailsEffect.CloseScreen)
        }
    }

    private fun onEnterNotificationTextClick() {
        (state.value.content as? RemindersDetailsUiState.Content.Success)?.let { successContent ->
            _effect.tryEmit(
                RemindersDetailsEffect.ShowNotificationTextDialog(
                    text = successContent.notificationText,
                )
            )
        }
    }

    private fun onNotificationTextEntered(text: String) {
        notificationTextState.update { text.ifBlank { "" }.trim() }
    }

    private fun onTimeSelected(time: LocalTime) {
        if (selectedTime.minute != time.minute || selectedTime.hour != time.hour) {
            _effect.tryEmit(RemindersDetailsEffect.PerformTimeHaptic)
        }
        selectedTime = time
    }

    private fun onCloseScreenClick() {
        _effect.tryEmit(RemindersDetailsEffect.CloseScreen)
    }

    private fun buildState(
        reminder: Reminder?,
        selectedDays: Set<DayOfWeek>,
        notificationText: String,
        appThemeColorId: String?,
    ) = RemindersDetailsUiState(
        theme = UiThemeColor.fromId(appThemeColorId),
        content = RemindersDetailsUiState.Content.Success(
            initialTime = reminder?.time ?: selectedTime,
            notificationText = notificationText,
            daysOfWeek = allDaysOfWeek.map { day ->
                DayItem(
                    day = day,
                    isSelected = selectedDays.contains(day),
                )
            },
        ),
    )
}