package com.furianrt.onboarding.internal.ui.container

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.onboarding.internal.ui.complete.CompleteScreen
import com.furianrt.onboarding.internal.ui.container.model.OnboardingButtonState
import com.furianrt.onboarding.internal.ui.container.model.OnboardingPage
import com.furianrt.onboarding.internal.ui.greeting.GreetingScreen
import com.furianrt.onboarding.internal.ui.notifications.NotificationsScreen
import com.furianrt.onboarding.internal.ui.theme.ThemeScreen
import com.furianrt.onboarding.internal.ui.theme.ThemeScreenState
import com.furianrt.permissions.extensions.openNotificationsSettingsScreen
import com.furianrt.permissions.ui.NotificationsPermissionDialog
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.uikit.components.RegularButton
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.entities.colorScheme
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun ContainerScreen(
    onCloseRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ContainerViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    val hazeState = rememberHazeState()

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)

    val cameraPermissionState = rememberPermissionState(
        permission = PermissionsUtils.getNotificationsPermission(),
        onPermissionResult = {
            viewModel.onEvent(ContainerEvent.OnNotificationsPermissionSelected)
        },
    )

    var showNotificationsPermissionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is ContainerEffect.CloseScreen -> onCloseRequestState()
                    is ContainerEffect.RequestNotificationsPermission -> {
                        cameraPermissionState.launchPermissionRequest()
                    }

                    is ContainerEffect.ShowNotificationsPermissionsDeniedDialog -> {
                        showNotificationsPermissionDialog = true
                    }
                }
            }
    }
    when (uiState) {
        is ContainerState.Loading -> LoadingContent(
            modifier = modifier,
        )

        is ContainerState.Success -> SuccessContent(
            modifier = modifier.hazeSource(hazeState),
            uiState = uiState,
            onEvent = viewModel::onEvent,
        )
    }

    if (showNotificationsPermissionDialog) {
        NotificationsPermissionDialog(
            hazeState = hazeState,
            onSettingsClick = context::openNotificationsSettingsScreen,
            onDismissRequest = { showNotificationsPermissionDialog = false },
        )
    }
}

@Composable
private fun SuccessContent(
    uiState: ContainerState.Success,
    onEvent: (event: ContainerEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val themeScreenState = remember { ThemeScreenState(uiState.appThemeColor) }
    SerenityTheme(
        colorScheme = themeScreenState.selectedTheme.colorScheme,
        isLightTheme = themeScreenState.selectedTheme.isLight,
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .statusBarsPadding()
                .clickableNoRipple {},
        ) {
            Crossfade(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                targetState = uiState.page,
                animationSpec = tween(durationMillis = 400, easing = LinearEasing),
            ) { targetState ->
                when (targetState) {
                    is OnboardingPage.Greeting -> GreetingScreen()
                    is OnboardingPage.Theme -> ThemeScreen(state = themeScreenState)
                    is OnboardingPage.Notification -> NotificationsScreen()
                    is OnboardingPage.Complete -> CompleteScreen()
                }
            }
            AnimatedContent(
                modifier = Modifier.fillMaxWidth(),
                targetState = uiState.page.buttonState,
                transitionSpec = {
                    slideIntoContainer(
                        towards = SlideDirection.Up,
                        animationSpec = tween(durationMillis = 450, delayMillis = 500),
                    ).togetherWith(
                        slideOutOfContainer(
                            towards = SlideDirection.Down,
                            animationSpec = tween(durationMillis = 400),
                        )
                    )
                },
            ) { targetState ->
                Buttons(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                        .navigationBarsPadding(),
                    state = targetState,
                    onMainClick = {
                        onEvent(ContainerEvent.OnMainButtonClick(themeScreenState.selectedTheme))
                    },
                    onSkipClick = { onEvent(ContainerEvent.OnSkipButtonClick) },
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .clickableNoRipple {},
    )
}

@Composable
private fun Buttons(
    state: OnboardingButtonState,
    onMainClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RegularButton(
            modifier = Modifier.fillMaxWidth(),
            text = state.mainButtonTitle,
            onClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                onMainClick()
            },
        )
        val skipTitle = (state.skipButton as? OnboardingButtonState.ButtonState.Visible)?.title
        TextButton(
            modifier = Modifier.alpha(if (skipTitle == null) 0f else 1f),
            shape = RoundedCornerShape(16.dp),
            enabled = skipTitle != null,
            onClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                onSkipClick()
            },
        ) {
            Text(
                modifier = Modifier.alpha(0.5f),
                text = skipTitle.orEmpty(),
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SuccessContent(
        uiState = ContainerState.Success(
            page = OnboardingPage.Notification(
                buttonState = OnboardingButtonState(
                    mainButtonTitle = "Allow",
                    skipButton = OnboardingButtonState.ButtonState.Visible(
                        title = "Skip",
                    ),
                )
            ),
            appThemeColor = UiThemeColor.STORM_IN_THE_NIGHT_BLUE_LIGHT,
        ),
        onEvent = {},
    )
}
