package com.furianrt.setiings.internal.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.furianrt.setiings.internal.ui.composables.Toolbar
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun SettingsScreenInternal(navHostController: NavHostController) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.CloseScreen -> {
                    navHostController.popBackStack()
                }
            }
        }
    }

    ScreenContent(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun ScreenContent(
    uiState: SettingsUiState,
    onEvent: (event: SettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Row {
            Toolbar(onBackButtonClick = { onEvent(SettingsEvent.OnButtonBackClick) })
            when (uiState) {
                is SettingsUiState.Success -> SuccessScreen(uiState, onEvent, modifier)
                is SettingsUiState.Loading -> LoadingScreen(modifier)
            }
        }
    }
}

@Composable
private fun SuccessScreen(
    uiState: SettingsUiState.Success,
    onEvent: (event: SettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxSize()
    ) {

    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
    }
}

@Composable
@PreviewWithBackground
private fun ScreenContentPreview() {
    SerenityTheme {
        ScreenContent(
            uiState = SettingsUiState.Success,
            onEvent = {},
        )
    }
}
