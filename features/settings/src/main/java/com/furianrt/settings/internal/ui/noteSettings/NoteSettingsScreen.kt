package com.furianrt.settings.internal.ui.noteSettings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.OptionButtonWrapper
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
    val scrollState = rememberScrollState()
    MovableToolbarScaffold(
        modifier = modifier,
        listState = scrollState,
        enabled = false,
        toolbar = {
            DefaultToolbar(
                modifier = Modifier.statusBarsPadding(),
                title = stringResource(R.string.settings_note_content_title),
                onBackClick = { onEvent(NoteSettingsEvent.OnButtonBackClick) },
            )
        }
    ) { topPadding ->
        AppBackground(
            modifier = Modifier.hazeSource(hazeState),
            theme = uiState.theme,
        )
        when (uiState.content) {
            is NoteSettingsState.Content.Success -> SuccessContent(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(top = topPadding),
                uiState = uiState.content,
                onEvent = onEvent,
                hazeState = hazeState,
            )

            is NoteSettingsState.Content.Loading -> LoadingContent(
                modifier = Modifier.padding(top = topPadding),
            )
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
            .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 24.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OptionButtonWrapper(
            modifier = Modifier.fillMaxWidth(),
            hazeState = hazeState,
        ) {
            SwitchWithLabel(
                title = stringResource(R.string.settings_note_detect_location_title),
                hint = stringResource(R.string.settings_note_detect_location_hint),
                isChecked = uiState.isAutoDetectLocationEnabled,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                onCheckedChange = { isChecked ->
                    onEvent(NoteSettingsEvent.OnEnableAutoDetectLocationChanged(isChecked))
                },
            )
        }
        OptionButtonWrapper(
            modifier = Modifier.fillMaxWidth(),
            hazeState = hazeState,
        ) {
            SwitchWithLabel(
                title = stringResource(R.string.settings_note_keep_prev_background_title),
                hint = stringResource(R.string.settings_note_keep_prev_background_hint),
                isChecked = uiState.isKeepPrevBackgroundEnabled,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                onCheckedChange = { isChecked ->
                    onEvent(NoteSettingsEvent.OnKeepNotePrevBackgroundChanged(isChecked))
                },
            )
        }
        OptionButtonWrapper(
            modifier = Modifier.fillMaxWidth(),
            hazeState = hazeState,
        ) {
            SwitchWithLabel(
                title = stringResource(R.string.settings_note_keep_prev_text_align_title),
                hint = stringResource(R.string.settings_note_keep_prev_text_align_hint),
                isChecked = uiState.isKeepPrevTextAlignEnabled,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                onCheckedChange = { isChecked ->
                    onEvent(NoteSettingsEvent.OnKeepNoteTextAlignChanged(isChecked))
                },
            )
        }
        OptionButtonWrapper(
            modifier = Modifier.fillMaxWidth(),
            hazeState = hazeState,
        ) {
            SwitchWithLabel(
                title = stringResource(R.string.settings_note_keep_prev_line_height_title),
                hint = stringResource(R.string.settings_note_keep_prev_line_height_hint),
                isChecked = uiState.isKeepPrevLineHeightEnabled,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                onCheckedChange = { isChecked ->
                    onEvent(NoteSettingsEvent.OnKeepNoteLineHeightChanged(isChecked))
                },
            )
        }
        OptionButtonWrapper(
            modifier = Modifier.fillMaxWidth(),
            hazeState = hazeState,
        ) {
            SwitchWithLabel(
                title = stringResource(R.string.settings_note_minimalistic_home_screen_title),
                hint = stringResource(R.string.settings_note_minimalistic_home_screen_hint),
                isChecked = uiState.isMinimalisticHomeScreenEnabled,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                onCheckedChange = { isChecked ->
                    onEvent(NoteSettingsEvent.OnEnableMinimalisticHomeScreenChanged(isChecked))
                },
            )
        }
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
                theme = UiThemeColor.defaultTheme,
                content = NoteSettingsState.Content.Success(
                    isAutoDetectLocationEnabled = true,
                    isMinimalisticHomeScreenEnabled = false,
                    isKeepPrevBackgroundEnabled = true,
                    isKeepPrevLineHeightEnabled = true,
                    isKeepPrevTextAlignEnabled = true,
                ),
            ),
            onEvent = {},
        )
    }
}
