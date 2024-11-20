package com.furianrt.lock.internal.ui.email

import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.lock.R
import com.furianrt.lock.internal.ui.elements.ButtonClose
import com.furianrt.uikit.anim.ShakingState
import com.furianrt.uikit.anim.rememberShakingState
import com.furianrt.uikit.anim.shakable
import com.furianrt.uikit.components.RegularButton
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.R as uiR

@Composable
internal fun EmailScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel: EmailViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val emailShakeState = rememberShakingState(
        strength = ShakingState.Strength.Strong,
        direction = ShakingState.Direction.LEFT_THEN_RIGHT,
    )

    var showErrorMessage by remember { mutableStateOf(false) }

    val view = LocalView.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is EmailEffect.CloseScreen -> onCloseRequest()
                is EmailEffect.ShowEmailFormatError -> {
                    view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                    emailShakeState.shake(25)
                    showErrorMessage = true
                }
            }
        }
    }

    LaunchedEffect(uiState.email.text) {
        showErrorMessage = false
    }

    ScreenContent(
        uiState = uiState,
        showErrorMessage = showErrorMessage,
        emailShakeState = emailShakeState,
        onEvent = viewModel::onEvent,
    )

    BackHandler {
        viewModel.onEvent(EmailEvent.OnCloseClick)
    }
}

@Composable
private fun ScreenContent(
    uiState: EmailUiState,
    showErrorMessage: Boolean,
    emailShakeState: ShakingState = rememberShakingState(),
    onEvent: (event: EmailEvent) -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .systemBarsPadding(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(40.dp),
        ) {
            ButtonClose(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
                onClick = { onEvent(EmailEvent.OnCloseClick) }
            )
            Title(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                text = stringResource(R.string.recovery_email_hint),
            )
            EmailInput(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .focusRequester(focusRequester)
                    .shakable(emailShakeState),
                state = uiState.email,
                hint = stringResource(uiR.string.title_email),
                showError = showErrorMessage,
            )
        }
        RegularButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .align(Alignment.BottomCenter)
                .imePadding(),
            text = stringResource(uiR.string.action_confirm),
            onClick = { onEvent(EmailEvent.OnConfirmClick) },
            enabled = uiState.email.text.isNotBlank(),
        )
    }
}

@Composable
private fun Title(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.titleSmall,
    )
}

@Composable
private fun EmailInput(
    state: TextFieldState,
    hint: String,
    showError: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp))
                .padding(16.dp),
            state = state,
            textStyle = MaterialTheme.typography.bodyMedium,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
                showKeyboardOnFocus = true,
            ),
            decorator = { innerTextField ->
                if (state.text.isEmpty()) {
                    Text(
                        modifier = Modifier.alpha(0.5f),
                        text = hint,
                        style = MaterialTheme.typography.labelMedium,
                        fontStyle = FontStyle.Italic,
                    )
                }
                innerTextField()
            },
        )
        if (showError) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = stringResource(R.string.recovery_email_format_error),
                color = MaterialTheme.colorScheme.errorContainer,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SerenityTheme {
        ScreenContent(
            uiState = EmailUiState(email = TextFieldState("test@test.com")),
            showErrorMessage = true,
        )
    }
}
