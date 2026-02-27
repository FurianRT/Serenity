package com.furianrt.settings.internal.ui.noteSettings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.settings.R
import com.furianrt.uikit.components.AppBackground
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.components.SwitchWithLabel
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.theme.SerenityTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun NoteSettingsScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel: NoteSettingsViewModel = hiltViewModel()
    val uiState: NoteSettingsState by viewModel.state.collectAsStateWithLifecycle()

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
    Content(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun Content(
    uiState: NoteSettingsState,
    onEvent: (event: NoteSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hazeState = rememberHazeState()
    Box(
        modifier = modifier,
    ) {
        AppBackground(
            modifier = Modifier.hazeSource(hazeState),
            theme = uiState.theme,
        )
        Column {
            DefaultToolbar(
                modifier = Modifier.systemBarsPadding(),
                title = stringResource(R.string.settings_note_content_title),
                onBackClick = { onEvent(NoteSettingsEvent.OnButtonBackClick) },
            )
            when (uiState.content) {
                is NoteSettingsState.Content.Success -> SuccessContent(
                    uiState = uiState.content,
                    onEvent = onEvent,
                    hazeState = hazeState,
                )

                is NoteSettingsState.Content.Loading -> LoadingContent()
            }
        }

    }
}

@Composable
private fun SuccessContent(
    uiState: NoteSettingsState.Content.Success,
    hazeState: HazeState,
    onEvent: (event: NoteSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SwitchWithLabel(
            title = stringResource(R.string.settings_note_detect_location_title),
            hint = stringResource(R.string.settings_note_detect_location_hint),
            isChecked = uiState.isAutoDetectLocationEnabled,
            hazeState = hazeState,
            onCheckedChange = { isChecked ->
                onEvent(NoteSettingsEvent.OnEnableAutoDetectLocationChanged(isChecked))
            },
        )
        SwitchWithLabel(
            title = stringResource(R.string.settings_note_keep_prev_background_title),
            hint = stringResource(R.string.settings_note_keep_prev_background_hint),
            isChecked = uiState.isKeepPrevBackgroundEnabled,
            hazeState = hazeState,
            onCheckedChange = { isChecked ->
                onEvent(NoteSettingsEvent.OnKeepNotePrevBackgroundChanged(isChecked))
            },
        )
        SwitchWithLabel(
            title = stringResource(R.string.settings_note_minimalistic_home_screen_title),
            hint = stringResource(R.string.settings_note_minimalistic_home_screen_hint),
            isChecked = uiState.isMinimalisticHomeScreenEnabled,
            hazeState = hazeState,
            onCheckedChange = { isChecked ->
                onEvent(NoteSettingsEvent.OnEnableMinimalisticHomeScreenChanged(isChecked))
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
        Content(
            uiState = NoteSettingsState(
                theme = UiThemeColor.STORM_IN_THE_NIGHT_BLUE_LIGHT,
                content = NoteSettingsState.Content.Success(
                    isAutoDetectLocationEnabled = true,
                    isMinimalisticHomeScreenEnabled = false,
                    isKeepPrevBackgroundEnabled = true,
                ),
            ),
            onEvent = {},
        )
    }
}
