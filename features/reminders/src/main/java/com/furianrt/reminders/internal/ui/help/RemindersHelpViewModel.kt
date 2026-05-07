package com.furianrt.reminders.internal.ui.help

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.repositories.DeviceInfoRepository
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.reminders.internal.schedulers.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class RemindersHelpViewModel @Inject constructor(
    deviceInfoRepository: DeviceInfoRepository,
    private val reminderScheduler: ReminderScheduler,
    private val permissionsUtils: PermissionsUtils,
) : ViewModel(),
    DefaultLifecycleObserver {

    private val permissionsInfoState = MutableStateFlow(getPermissionsInfo())

    val state: StateFlow<RemindersHelpUiState> = combine(
        permissionsInfoState,
        deviceInfoRepository.isPowerSaveModeEnabled(),
        deviceInfoRepository.isDndModeEnabled(),
        ::buildState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = buildState(
            permissionsInfo = getPermissionsInfo(),
            isPowerSaveModeEnabled = deviceInfoRepository.isPowerSaveModeEnabled().value,
            isDndModeEnabled = deviceInfoRepository.isDndModeEnabled().value,
        ),
    )

    private val _effect = MutableSharedFlow<RemindersHelpEffect>(extraBufferCapacity = 5)
    val effect = _effect.asSharedFlow()

    fun onEvent(event: RemindersHelpEvent) {
        when (event) {
            is RemindersHelpEvent.OnCloseScreenClick -> onCloseScreenClick()
            is RemindersHelpEvent.OnAlarmsPermissionClick -> onAlarmsPermissionClick()
            is RemindersHelpEvent.OnBatteryOptimizationClick -> onBatteryOptimizationClick()
            is RemindersHelpEvent.OnEnableNotificationsClick -> onEnableNotificationsClick()
            is RemindersHelpEvent.OnPowerSavingModeClick -> onPowerSavingModeClick()
        }
    }

    private fun onCloseScreenClick() {
        _effect.tryEmit(RemindersHelpEffect.CloseScreen)
    }

    private fun onAlarmsPermissionClick() {
        _effect.tryEmit(RemindersHelpEffect.OpenAlarmsSettingsScreen)
    }

    private fun onBatteryOptimizationClick() {
        _effect.tryEmit(RemindersHelpEffect.OpenBatteryOptimizationScreen)
    }

    private fun onEnableNotificationsClick() {
        _effect.tryEmit(RemindersHelpEffect.OpenNotificationsSettingsScreen)
    }

    private fun onPowerSavingModeClick() {
        _effect.tryEmit(RemindersHelpEffect.OpenPowerSavingScreen)
    }

    override fun onResume(owner: LifecycleOwner) {
        permissionsInfoState.update { getPermissionsInfo() }
    }

    private fun getPermissionsInfo() = PermissionsInfo(
        isNotificationsEnabled = permissionsUtils.hasNotificationsPermission(),
        hasAlarmsPermission = reminderScheduler.canScheduleExactAlarms(),
        isBatteryOptimizationEnabled = permissionsUtils.isBatteryOptimizationEnabled(),
    )

    private fun buildState(
        permissionsInfo: PermissionsInfo,
        isPowerSaveModeEnabled: Boolean,
        isDndModeEnabled: Boolean,
    ) = RemindersHelpUiState(
        isNotificationsEnabled = permissionsInfo.isNotificationsEnabled,
        hasAlarmsPermission = permissionsInfo.hasAlarmsPermission,
        isBatteryOptimizationEnabled = permissionsInfo.isBatteryOptimizationEnabled,
        isPowerSaveModeEnabled = isPowerSaveModeEnabled,
        isDndModeEnabled = isDndModeEnabled,
    )

    private data class PermissionsInfo(
        val isNotificationsEnabled: Boolean,
        val hasAlarmsPermission: Boolean,
        val isBatteryOptimizationEnabled: Boolean,
    )
}