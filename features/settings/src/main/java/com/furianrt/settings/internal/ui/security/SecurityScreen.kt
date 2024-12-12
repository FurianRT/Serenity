package com.furianrt.settings.internal.ui.security

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.settings.R
import com.furianrt.uikit.R as uiR
import com.furianrt.settings.internal.ui.composables.GeneralButton
import com.furianrt.settings.internal.ui.composables.SwitchButton
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.theme.SerenityTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

@Composable
internal fun SecurityScreen(
    openChangePinScreen: () -> Unit,
    openChangeEmailScreen: () -> Unit,
    onCloseRequest: () -> Unit,
) {
    val viewModel: SecurityViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    var showPinDelayDialog by remember { mutableStateOf(false) }

    val hazeState = remember { HazeState() }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SecurityEffect.CloseScreen -> onCloseRequest()
                is SecurityEffect.OpenChangePinScreen -> openChangePinScreen()
                is SecurityEffect.OpenChangeEmailScreen -> openChangeEmailScreen()
                is SecurityEffect.ShowPinDelaysDialog -> showPinDelayDialog = true
            }
        }
    }

    ScreenContent(
        modifier = Modifier.haze(hazeState),
        uiState = uiState,
        onEvent = viewModel::onEvent
    )

    if (showPinDelayDialog) {
        PinDelayDialog(
            selectedDelay = (uiState as? SecurityUiState.Success)?.requestDelay ?: 0,
            hazeState = hazeState,
            onDismissRequest = { showPinDelayDialog = false },
            onDelayClick = { viewModel.onEvent(SecurityEvent.OnPinDelaySelected(it)) },
        )
    }
}

@Composable
private fun ScreenContent(
    uiState: SecurityUiState,
    onEvent: (event: SecurityEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        DefaultToolbar(
            modifier = Modifier.drawBehind {
                if (scrollState.canScrollBackward) {
                    drawBottomShadow(elevation = 8.dp)
                }
            },
            title = stringResource(uiR.string.security_title),
            onBackClick = { onEvent(SecurityEvent.OnButtonBackClick) },
        )
        when (uiState) {
            is SecurityUiState.Success -> SuccessScreen(uiState, scrollState, onEvent)
            is SecurityUiState.Loading -> LoadingScreen()
        }
    }
}

@Composable
private fun SuccessScreen(
    uiState: SecurityUiState.Success,
    scrollState: ScrollState,
    onEvent: (event: SecurityEvent) -> Unit,
) {
    val view = LocalView.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SwitchButton(
            title = stringResource(R.string.settings_enable_pin_title),
            checked = uiState.isPinEnabled,
            onCheckedChange = {
                if (uiState.isPinEnabled) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                }
                onEvent(SecurityEvent.OnEnablePinCheckChanged)
            },
        )
        GeneralButton(
            title = stringResource(R.string.settings_email_to_recover_pin_title),
            hint = uiState.recoveryEmail,
            enabled = uiState.isPinEnabled,
            onClick = { onEvent(SecurityEvent.OnChangeEmailClick) },
        )
        GeneralButton(
            title = stringResource(R.string.settings_pin_request_title),
            hint = if (uiState.requestDelay < 60 * 1000) {
                pluralStringResource(
                    R.plurals.settings_pin_delay_seconds_plural,
                    uiState.requestDelay / 1000,
                    uiState.requestDelay / 1000,
                )
            } else {
                pluralStringResource(
                    R.plurals.settings_pin_delay_minutes_plural,
                    uiState.requestDelay / 60 / 1000,
                    uiState.requestDelay / 60 / 1000,
                )
            },
            enabled = uiState.isPinEnabled,
            onClick = { onEvent(SecurityEvent.OnPinDelayClick) },
        )
        SwitchButton(
            title = stringResource(R.string.settings_enable_fingerprint_title),
            checked = uiState.isFingerprintEnabled,
            enabled = uiState.isPinEnabled,
            onCheckedChange = {
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                onEvent(SecurityEvent.OnFingerprintCheckChanged)
            },
        )
    }
}

@Composable
private fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
    }
}

@Preview
@Composable
private fun Preview() {
    SerenityTheme {
        ScreenContent(
            uiState = SecurityUiState.Success(
                isPinEnabled = true,
                isFingerprintEnabled = false,
                recoveryEmail = "DiaryApps@gmail.com",
                requestDelay = 0,
            ),
            onEvent = {},
        )
    }
}
