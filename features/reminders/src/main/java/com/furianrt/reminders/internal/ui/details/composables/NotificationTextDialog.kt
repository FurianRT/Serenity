package com.furianrt.reminders.internal.ui.details.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.furianrt.reminders.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import com.furianrt.uikit.R as uiR

private const val MAX_TEXT_LENGTH = 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationTextDialog(
    initialText: String,
    hazeState: HazeState,
    onTextEntered: (text: String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val auth = LocalAuth.current

    LifecycleStartEffect(Unit) {
        scope.launch {
            if (!auth.isAuthorized()) {
                onDismissRequest()
            }
        }
        onStopOrDispose {}
    }

    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest
    ) {
        Content(
            initialText = initialText,
            hazeState = hazeState,
            onTextEntered = onTextEntered,
            onDismissRequest = onDismissRequest,
        )
    }
}

@Composable
private fun Content(
    initialText: String,
    hazeState: HazeState,
    onTextEntered: (text: String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val textState = rememberTextFieldState(initialText)

    val isTextEmpty by remember(textState) {
        derivedStateOf { textState.text.isEmpty() }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .hazeEffect(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    blurRadius = 20.dp,
                )
            )
            .background(MaterialTheme.colorScheme.surfaceTint)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp),
            contentAlignment = Alignment.Center,
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = spring(
                            stiffness = 3500f,
                            visibilityThreshold = IntSize.VisibilityThreshold,
                        ),
                    )
                    .focusRequester(focusRequester),
                state = textState,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                lineLimits = TextFieldLineLimits.MultiLine(),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.surfaceContainer),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Sentences,
                    showKeyboardOnFocus = true,
                ),
                inputTransformation = InputTransformation {
                    if (asCharSequence().length > MAX_TEXT_LENGTH) {
                        revertAllChanges()
                    }
                },
                decorator = { innerTextField ->
                    if (isTextEmpty) {
                        Text(
                            modifier = Modifier.alpha(0.5f),
                            text = stringResource(R.string.reminders_notification_text_hint),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    innerTextField()
                },
            )
        }
        Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextButton(
                onClick = onDismissRequest,
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = stringResource(uiR.string.action_cancel),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            TextButton(
                onClick = {
                    onTextEntered(textState.text.toString())
                    onDismissRequest()
                },
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = stringResource(uiR.string.action_done),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primaryContainer,
                )
            }
        }
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        Content(
            initialText = "",
            hazeState = rememberHazeState(),
            onTextEntered = {},
            onDismissRequest = {},
        )
    }
}
