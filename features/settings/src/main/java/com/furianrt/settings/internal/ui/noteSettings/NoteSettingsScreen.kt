package com.furianrt.settings.internal.ui.noteSettings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.settings.R
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.components.SwitchWithLabel
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun NoteSettingsScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel: NoteSettingsViewModel = hiltViewModel()
    val uiState: NoteSettingsState = viewModel.state.collectAsStateWithLifecycle().value

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is NoteSettingsEffect.CloseScreen -> onCloseRequestState()
                }
            }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            DefaultToolbar(
                modifier = Modifier.systemBarsPadding(),
                title = stringResource(R.string.settings_note_content_title),
                onBackClick = { viewModel.onEvent(NoteSettingsEvent.OnButtonBackClick) },
            )
        },
    ) { paddingValues ->
        when (uiState) {
            is NoteSettingsState.Success -> SuccessContent(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState,
                onEvent = viewModel::onEvent,
            )

            is NoteSettingsState.Loading -> LoadingContent(
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun SuccessContent(
    uiState: NoteSettingsState.Success,
    onEvent: (event: NoteSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SwitchWithLabel(
            title = stringResource(R.string.settings_note_detect_location_title),
            isChecked = uiState.isAutoDetectLocationEnabled,
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                } else {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                }
                onEvent(NoteSettingsEvent.OnEnableAutoDetectLocationChanged(isChecked))
            },
        )
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize())
}

@Preview
@Composable
private fun Preview() {
    SerenityTheme {
        SuccessContent(
            uiState = NoteSettingsState.Success(
                isAutoDetectLocationEnabled = true,
            ),
            onEvent = {},
        )
    }
}
