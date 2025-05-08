package com.furianrt.security.internal.ui.lock.check

import android.hardware.biometrics.BiometricPrompt
import android.os.CancellationSignal
import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.furianrt.security.R
import com.furianrt.security.internal.ui.lock.elements.ButtonClose
import com.furianrt.security.internal.ui.lock.elements.Keyboard
import com.furianrt.security.internal.ui.lock.elements.Pins
import com.furianrt.security.internal.ui.lock.entities.PinCount
import com.furianrt.uikit.anim.ShakingState
import com.furianrt.uikit.anim.rememberShakingState
import com.furianrt.uikit.components.SnackBar
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import java.util.concurrent.Executors
import com.furianrt.uikit.R as uiR

@Composable
internal fun CheckPinScreenInternal(
    hazeState: HazeState,
    onCloseRequest: () -> Unit,
) {
    val viewModel: CheckPinViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val view = LocalView.current
    val activity = LocalActivity.current
    val focusManager = LocalFocusManager.current

    var recoveryDialogState: String? by remember { mutableStateOf(null) }

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

    val recoveryDialogHazeState = remember { HazeState() }
    val snackBarHostState = remember { SnackbarHostState() }

    val emailFailureText = stringResource(R.string.send_pin_recovery_email_failure)
    val emailSuccessText = stringResource(R.string.send_pin_recovery_email_success)

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)

    LaunchedEffect(Unit) {
        focusManager.clearFocus()
        viewModel.effect.collect { effect ->
            when (effect) {
                is CheckPinEffect.CloseScreen -> activity?.moveTaskToBack(true)
                is CheckPinEffect.ShowPinSuccess -> onCloseRequestState()
                is CheckPinEffect.ShowForgotPinDialog -> recoveryDialogState = effect.email
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

                is CheckPinEffect.ShowSendEmailFailure -> snackBarHostState.showSnackbar(
                    message = emailFailureText,
                    duration = SnackbarDuration.Short,
                )

                is CheckPinEffect.ShowSendEmailSuccess -> snackBarHostState.showSnackbar(
                    message = emailSuccessText,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    SnackBar(
                        title = data.visuals.message,
                        icon = painterResource(uiR.drawable.ic_email),
                        tonalColor = MaterialTheme.colorScheme.tertiary,
                    )
                },
            )
        },
        content = { paddingValues ->
            ScreenContent(
                modifier = Modifier
                    .clickableNoRipple {}
                    .haze(recoveryDialogHazeState)
                    .padding(paddingValues),
                uiState = uiState,
                hazeState = hazeState,
                pinsShakingState = shakeState,
                onEvent = viewModel::onEvent,
            )
        },
    )

    recoveryDialogState?.let { state ->
        ForgotPinDialog(
            email = state,
            hazeState = recoveryDialogHazeState,
            onConfirmClick = { viewModel.onEvent(CheckPinEvent.OnSendRecoveryEmailClick) },
            onDismissRequest = { recoveryDialogState = null },
        )
    }

    BackHandler {
        viewModel.onEvent(CheckPinEvent.OnCloseClick)
    }
}

@Composable
private fun ScreenContent(
    uiState: CheckPinUiState,
    modifier: Modifier = Modifier,
    hazeState: HazeState = HazeState(),
    pinsShakingState: ShakingState = rememberShakingState(),
    onEvent: (event: CheckPinEvent) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .hazeChild(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    tint = HazeTint(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                    blurRadius = 20.dp,
                ),
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ButtonClose(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                .align(Alignment.Start)
                .systemGestureExclusion(),
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
        ButtonForgotPin(
            modifier = Modifier.padding(bottom = 8.dp),
            state = uiState.forgotPinButtonState,
            onClick = { onEvent(CheckPinEvent.OnForgotPinClick) },
        )
    }
}

@Composable
private fun ButtonForgotPin(
    onClick: () -> Unit,
    state: CheckPinUiState.ForgotPinButtonState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.heightIn(min = 64.dp),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = state,
            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
            contentKey = { it is CheckPinUiState.ForgotPinButtonState.Loading },
            label = "ForgotPinButtonAnim",
        ) { targetState ->
            if (targetState is CheckPinUiState.ForgotPinButtonState.Loading) {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.anim_sending_mail),
                )
                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                )
                LottieAnimation(
                    modifier = Modifier.height(64.dp),
                    composition = composition,
                    progress = { progress },
                )
            } else {
                TextButton(
                    modifier = modifier
                        .applyIf(targetState !is CheckPinUiState.ForgotPinButtonState.Enabled) {
                            Modifier.alpha(0.5f)
                        }
                        .animateContentSize(),
                    enabled = targetState is CheckPinUiState.ForgotPinButtonState.Enabled,
                    onClick = onClick,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.lock_forgot_pin_title),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        if (targetState is CheckPinUiState.ForgotPinButtonState.Timer) {
                            Text(
                                text = targetState.timer,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
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
                forgotPinButtonState = CheckPinUiState.ForgotPinButtonState.Enabled,
            )
        )
    }
}
