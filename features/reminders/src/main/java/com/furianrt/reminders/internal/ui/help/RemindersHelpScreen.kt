package com.furianrt.reminders.internal.ui.help

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.permissions.extensions.openAlarmsSettingsScreen
import com.furianrt.permissions.extensions.openBatteryOptimizationScreen
import com.furianrt.permissions.extensions.openNotificationsSettingsScreen
import com.furianrt.permissions.extensions.openPowerSavingScreen
import com.furianrt.reminders.R
import com.furianrt.reminders.internal.ui.help.composables.HelpOption
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.coroutines.flow.collectLatest
import com.furianrt.uikit.R as uiR

@Composable
internal fun RemindersHelpScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel: RemindersHelpViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)

    DisposableEffect(Unit) {
        lifecycle.addObserver(viewModel)
        onDispose {
            lifecycle.removeObserver(viewModel)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is RemindersHelpEffect.CloseScreen -> onCloseRequestState()
                    is RemindersHelpEffect.OpenNotificationsSettingsScreen -> {
                        context.openNotificationsSettingsScreen()
                    }

                    is RemindersHelpEffect.OpenAlarmsSettingsScreen -> {
                        context.openAlarmsSettingsScreen()
                    }

                    is RemindersHelpEffect.OpenBatteryOptimizationScreen -> {
                        context.openBatteryOptimizationScreen()
                    }

                    is RemindersHelpEffect.OpenPowerSavingScreen -> {
                        context.openPowerSavingScreen()
                    }
                }
            }
    }
    Content(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    uiState: RemindersHelpUiState,
    onEvent: (event: RemindersHelpEvent) -> Unit,
) {
    val scrollState = rememberScrollState()
    MovableToolbarScaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        listState = scrollState,
        enabled = false,
        toolbar = {
            DefaultToolbar(
                modifier = Modifier.statusBarsPadding(),
                title = stringResource(R.string.reminders_troubleshooting_screen_title),
                onBackClick = { onEvent(RemindersHelpEvent.OnCloseScreenClick) },
            )
        },
    ) { topPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(start = 12.dp, end = 4.dp, top = topPadding + 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            HelpOption(
                title = stringResource(R.string.reminders_troubleshooting_notification_permission_title),
                hint = stringResource(R.string.reminders_troubleshooting_notification_permission_hint),
                warningTint = MaterialTheme.colorScheme.errorContainer,
                actionText = stringResource(R.string.reminders_troubleshooting_open_settings_title),
                hasWarning = !uiState.isNotificationsEnabled,
                onActionClick = { onEvent(RemindersHelpEvent.OnEnableNotificationsClick) },
            )
            HelpOption(
                title = stringResource(R.string.reminders_troubleshooting_alarms_permission_title),
                hint = stringResource(R.string.reminders_troubleshooting_alarms_permission_hint),
                warningTint = MaterialTheme.colorScheme.errorContainer,
                actionText = stringResource(R.string.reminders_troubleshooting_open_settings_title),
                hasWarning = !uiState.hasAlarmsPermission,
                onActionClick = { onEvent(RemindersHelpEvent.OnAlarmsPermissionClick) },
            )
            HelpOption(
                title = stringResource(R.string.reminders_troubleshooting_battery_optimization_title),
                hint = stringResource(R.string.reminders_troubleshooting_battery_optimization_hint),
                warningTint = MaterialTheme.colorScheme.onSurface,
                actionText = stringResource(uiR.string.action_disable),
                hasWarning = uiState.isBatteryOptimizationEnabled,
                onActionClick = { onEvent(RemindersHelpEvent.OnBatteryOptimizationClick) },
            )
            HelpOption(
                title = stringResource(R.string.reminders_troubleshooting_power_saving_mode_title),
                hint = stringResource(R.string.reminders_troubleshooting_power_saving_mode_hint),
                warningTint = MaterialTheme.colorScheme.onSurface,
                actionText = stringResource(uiR.string.action_disable),
                hasWarning = uiState.isPowerSaveModeEnabled,
                onActionClick = { onEvent(RemindersHelpEvent.OnPowerSavingModeClick) },
            )
            HelpOption(
                title = stringResource(R.string.reminders_troubleshooting_do_not_disturb_title),
                hint = stringResource(R.string.reminders_troubleshooting_do_not_disturb_hint),
                warningTint = MaterialTheme.colorScheme.onSurface,
                actionText = null,
                hasWarning = uiState.isDndModeEnabled,
            )
            if (uiState.showRebootDeviceHint) {
                HelpOption(
                    title = stringResource(R.string.reminders_troubleshooting_reboot_device_title),
                    hint = stringResource(R.string.reminders_troubleshooting_reboot_device_hint),
                    warningTint = MaterialTheme.colorScheme.onSurface,
                    actionText = null,
                    hasWarning = true,
                )
            }
        }
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        Content(
            uiState = RemindersHelpUiState(
                isNotificationsEnabled = false,
                hasAlarmsPermission = false,
                isBatteryOptimizationEnabled = true,
                isPowerSaveModeEnabled = true,
                isDndModeEnabled = false,
            ),
            onEvent = {},
        )
    }
}
