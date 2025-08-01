package com.furianrt.security.internal.ui.security

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.security.R
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.components.GeneralButton
import com.furianrt.uikit.components.SwitchWithLabel
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import com.furianrt.uikit.R as uiR

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

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)
    val openChangePinScreenState by rememberUpdatedState(openChangePinScreen)
    val openChangeEmailScreenState by rememberUpdatedState(openChangeEmailScreen)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SecurityEffect.CloseScreen -> onCloseRequestState()
                is SecurityEffect.OpenChangePinScreen -> openChangePinScreenState()
                is SecurityEffect.OpenChangeEmailScreen -> openChangeEmailScreenState()
                is SecurityEffect.ShowPinDelaysDialog -> showPinDelayDialog = true
            }
        }
    }

    ScreenContent(
        modifier = Modifier.hazeSource(hazeState),
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
    val shadowColor = MaterialTheme.colorScheme.surfaceDim
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            DefaultToolbar(
                modifier = Modifier
                    .systemBarsPadding()
                    .drawBehind {
                        if (scrollState.canScrollBackward) {
                            drawBottomShadow(color = shadowColor)
                        }
                    },
                title = stringResource(uiR.string.security_title),
                onBackClick = { onEvent(SecurityEvent.OnButtonBackClick) },
            )
        },
    ) { paddingValues ->
        when (uiState) {
            is SecurityUiState.Success -> SuccessScreen(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState,
                scrollState = scrollState,
                onEvent = onEvent
            )

            is SecurityUiState.Loading -> LoadingScreen(
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun SuccessScreen(
    uiState: SecurityUiState.Success,
    scrollState: ScrollState,
    onEvent: (event: SecurityEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SwitchWithLabel(
            title = stringResource(R.string.security_enable_pin_title),
            isChecked = uiState.isPinEnabled,
            onCheckedChange = { isChecked ->
                if (!isChecked) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                }
                onEvent(SecurityEvent.OnEnablePinCheckChanged(isChecked))
            },
        )
        GeneralButton(
            title = stringResource(R.string.security_email_to_recover_pin_title),
            hint = uiState.recoveryEmail,
            enabled = uiState.isPinEnabled,
            onClick = { onEvent(SecurityEvent.OnChangeEmailClick) },
        )
        GeneralButton(
            title = stringResource(R.string.security_pin_request_title),
            hint = if (uiState.requestDelay < 60 * 1000) {
                pluralStringResource(
                    R.plurals.security_pin_delay_seconds_plural,
                    uiState.requestDelay / 1000,
                    uiState.requestDelay / 1000,
                )
            } else {
                pluralStringResource(
                    R.plurals.security_pin_delay_minutes_plural,
                    uiState.requestDelay / 60 / 1000,
                    uiState.requestDelay / 60 / 1000,
                )
            },
            enabled = uiState.isPinEnabled,
            onClick = { onEvent(SecurityEvent.OnPinDelayClick) },
        )
        SwitchWithLabel(
            title = stringResource(R.string.security_enable_fingerprint_title),
            isChecked = uiState.isFingerprintEnabled,
            enabled = uiState.isPinEnabled,
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                } else {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                }
                onEvent(SecurityEvent.OnFingerprintCheckChanged(isChecked))
            },
        )
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize())
}

@PreviewWithBackground
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
