package com.furianrt.security.internal.ui.lock.change

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.security.R
import com.furianrt.security.internal.ui.lock.elements.ButtonClose
import com.furianrt.security.internal.ui.lock.elements.Keyboard
import com.furianrt.security.internal.ui.lock.elements.Pins
import com.furianrt.security.internal.ui.lock.entities.PinCount
import com.furianrt.uikit.anim.ShakingState
import com.furianrt.uikit.anim.rememberShakingState
import com.furianrt.uikit.theme.SerenityTheme

@Composable
internal fun ChangePinScreen(
    openEmailScreen: (pin: String) -> Unit,
    onCloseRequest: () -> Unit,
) {
    val viewModel: ChangePinViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val hapticFeedback = LocalHapticFeedback.current

    val shakeState = rememberShakingState(
        strength = ShakingState.Strength.Strong,
        direction = ShakingState.Direction.LEFT_THEN_RIGHT,
    )

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)
    val openEmailScreenState by rememberUpdatedState(openEmailScreen)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ChangePinEffect.CloseScreen -> onCloseRequestState()
                is ChangePinEffect.ShowPinDoesNotMatchError -> {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
                    shakeState.shake(25)
                }

                is ChangePinEffect.OpenEmailScreen -> openEmailScreenState(effect.pin)
            }
        }
    }
    ScreenContent(uiState, shakeState, viewModel::onEvent)
}

@Composable
private fun ScreenContent(
    uiState: ChangePinUiState,
    pinsShakingState: ShakingState = rememberShakingState(),
    onEvent: (event: ChangePinEvent) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        ButtonClose(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .systemGestureExclusion(),
            onClick = { onEvent(ChangePinEvent.OnCloseClick) },
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Pins(
                title = when (uiState.mode) {
                    ChangePinUiState.Mode.INITIAL -> {
                        stringResource(R.string.lock_enter_new_pin_title)
                    }

                    ChangePinUiState.Mode.REPEAT -> {
                        stringResource(R.string.lock_repeat_your_pin_title)
                    }
                },
                pinsCount = uiState.pins,
                shakingState = pinsShakingState,
            )
            Keyboard(
                showFingerprint = false,
                onKeyClick = { onEvent(ChangePinEvent.OnKeyEntered(it)) },
                onClearKeyClick = { onEvent(ChangePinEvent.OnClearKeyClick) }
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    SerenityTheme {
        ScreenContent(
            uiState = ChangePinUiState(
                pins = PinCount.TWO,
                mode = ChangePinUiState.Mode.INITIAL,
            )
        )
    }
}
