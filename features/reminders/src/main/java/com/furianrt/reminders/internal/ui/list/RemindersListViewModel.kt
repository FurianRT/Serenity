package com.furianrt.reminders.internal.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.entities.Reminder
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.reminders.internal.domain.DeleteReminderUseCase
import com.furianrt.reminders.internal.domain.GetAllRemindersUseCase
import com.furianrt.reminders.internal.schedulers.ReminderScheduler
import com.furianrt.reminders.internal.ui.list.entities.ReminderItem
import com.furianrt.reminders.internal.ui.list.extensions.toReminderItem
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.extensions.launch
import com.kizitonwose.calendar.core.daysOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.map

@HiltViewModel
internal class RemindersListViewModel @Inject constructor(
    getAllRemindersUseCase: GetAllRemindersUseCase,
    appearanceRepository: AppearanceRepository,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val reminderScheduler: ReminderScheduler,
    private val permissionsUtils: PermissionsUtils,
) : ViewModel() {

    private val allDaysOfWeek = daysOfWeek()
    private val timeFormatter = DateTimeFormatter
        .ofLocalizedTime(FormatStyle.SHORT)
        .withLocale(Locale.getDefault())

    val state: StateFlow<RemindersListUiState> = combine(
        getAllRemindersUseCase(),
        appearanceRepository.getAppThemeColorId(),
        ::buildState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RemindersListUiState(
            theme = UiThemeColor.fromId(appearanceRepository.getAppThemeColorId().value),
            content = RemindersListUiState.Content.Loading,
        ),
    )

    private val _effect = MutableSharedFlow<RemindersListEffect>(extraBufferCapacity = 5)
    val effect = _effect.asSharedFlow()

    fun onEvent(event: RemindersListEvent) {
        when (event) {
            is RemindersListEvent.OnAddReminderClick -> onAddReminderClick()
            is RemindersListEvent.OnCloseScreenClick -> onCloseScreenClick()
            is RemindersListEvent.OnTroubleShootingClick -> onTroubleShootingClick()
            is RemindersListEvent.OnReminderClick -> onReminderClick(event.reminder)
            is RemindersListEvent.OnDeleteReminderClick -> onDeleteReminderClick(event.reminder)
            is RemindersListEvent.OnNotificationsPermissionSelected -> {
                checkNotificationsPermission()
            }
        }
    }

    private fun onAddReminderClick() {
        if (reminderScheduler.canScheduleExactAlarms()) {
            _effect.tryEmit(RemindersListEffect.RequestNotificationsPermission)
        } else {
            _effect.tryEmit(RemindersListEffect.OpenAlarmsSettingsScreen)
        }
    }

    private fun onCloseScreenClick() {
        _effect.tryEmit(RemindersListEffect.CloseScreen)
    }

    private fun onTroubleShootingClick() {
        _effect.tryEmit(RemindersListEffect.OpenTroubleShootingScreen)
    }

    private fun onReminderClick(reminder: ReminderItem) {
        _effect.tryEmit(RemindersListEffect.OpenReminderDetailsScreen(reminder.id))
    }

    private fun onDeleteReminderClick(reminder: ReminderItem) {
        launch { deleteReminderUseCase(reminder.id) }
    }

    private fun checkNotificationsPermission() {
        if (permissionsUtils.hasNotificationsPermission()) {
            _effect.tryEmit(RemindersListEffect.OpenReminderDetailsScreen(reminderId = null))
        } else {
            _effect.tryEmit(RemindersListEffect.ShowNotificationsPermissionsDeniedDialog)
        }
    }

    private fun buildState(
        reminders: List<Reminder>,
        appThemeColorId: String?,
    ) = RemindersListUiState(
        theme = UiThemeColor.fromId(appThemeColorId),
        content = if (reminders.isEmpty()) {
            RemindersListUiState.Content.Empty
        } else {
            RemindersListUiState.Content.Success(
                reminders = reminders.map { reminder ->
                    reminder.toReminderItem(
                        allDaysOfWeek = allDaysOfWeek,
                        timeFormatter = timeFormatter,
                    )
                },
            )
        },
    )
}
