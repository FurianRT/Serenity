package com.furianrt.lock.internal.ui.check

import android.hardware.biometrics.BiometricPrompt
import android.os.CancellationSignal
import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.lock.R
import com.furianrt.lock.internal.ui.elements.ButtonClose
import com.furianrt.lock.internal.ui.elements.Keyboard
import com.furianrt.lock.internal.ui.elements.Pins
import com.furianrt.lock.internal.ui.entities.PinCount
import com.furianrt.uikit.anim.ShakingState
import com.furianrt.uikit.anim.rememberShakingState
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild
import java.util.concurrent.Executors

@Composable
internal fun CheckPinScreenInternal(
    hazeState: HazeState,
    onCloseRequest: () -> Unit,
) {
    val viewModel: CheckPinViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val view = LocalView.current
    val activity = LocalActivity.current

    val biometricPrompt = remember {
        activity?.let {
            BiometricPrompt.Builder(activity)
                .setTitle(activity.getString(R.string.lock_fingerprint_title))
                .setNegativeButton(
                    activity.getString(R.string.lock_use_pin_title),
                    Executors.newSingleThreadExecutor(),
                    { _, _ -> },
                )
                .build()
        }
    }

    LifecycleStartEffect(Unit) {
        viewModel.onEvent(CheckPinEvent.OnScreenStarted)
        onStopOrDispose {}
    }

    val shakeState = rememberShakingState(
        strength = ShakingState.Strength.Strong,
        direction = ShakingState.Direction.LEFT_THEN_RIGHT,
    )

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CheckPinEffect.CloseScreen -> activity?.moveTaskToBack(true)
                is CheckPinEffect.ShowPinSuccess -> onCloseRequest()
                is CheckPinEffect.ShowForgotPinDialog -> {}
                is CheckPinEffect.ShowWrongPinError -> {
                    view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                    shakeState.shake(25)
                }

                is CheckPinEffect.ShowBiometricScanner -> biometricPrompt?.authenticate(
                    CancellationSignal(),
                    Executors.newSingleThreadExecutor(),
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(
                            result: BiometricPrompt.AuthenticationResult?,
                        ) {
                            viewModel.onEvent(CheckPinEvent.OnBiometricSucceeded)
                        }
                    }
                )
            }
        }
    }

    ScreenContent(
        uiState = uiState,
        hazeState = hazeState,
        pinsShakingState = shakeState,
        onEvent = viewModel::onEvent,
    )

    BackHandler {
        viewModel.onEvent(CheckPinEvent.OnCloseClick)
    }
}

@Composable
private fun ScreenContent(
    uiState: CheckPinUiState,
    hazeState: HazeState = HazeState(),
    pinsShakingState: ShakingState = rememberShakingState(),
    onEvent: (event: CheckPinEvent) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .hazeChild(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    tint = HazeTint.Color(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    ),
                    blurRadius = 20.dp,
                ),
            )
            .clickableNoRipple {}
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ButtonClose(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                .align(Alignment.Start),
            onClick = { onEvent(CheckPinEvent.OnCloseClick) },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Pins(
                title = stringResource(R.string.lock_enter_your_pin_title),
                pinsCount = uiState.pins,
                shakingState = pinsShakingState,
            )
            Keyboard(
                showFingerprint = uiState.showFingerprint,
                onKeyClick = { onEvent(CheckPinEvent.OnKeyEntered(it)) },
                onClearKeyClick = { onEvent(CheckPinEvent.OnClearKeyClick) },
                onFingerprintClick = { onEvent(CheckPinEvent.OnFingerprintClick) },
            )
        }
        TextButton(
            modifier = Modifier.padding(bottom = 24.dp),
            onClick = { onEvent(CheckPinEvent.OnForgotPinClick) },
        ) {
            Text(
                text = stringResource(R.string.lock_forgot_pin_title),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        ScreenContent(
            uiState = CheckPinUiState(
                showFingerprint = true,
                pins = PinCount.TWO,
            )
        )
    }
}
